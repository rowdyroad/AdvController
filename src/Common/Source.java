package Common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import Streamer.RingBuffer;

public class Source implements Runnable {


	public static class SourceException extends Exception
	{
		private static final long serialVersionUID = 5707762550983986760L;
		public  enum ErrorType
		{
			SAMPLE_RATE,
			CHANNELS,
			SAMPLE_SIZE
		}

		private static String[] errors = {"Incorrect sample rate", "Incorrect number of channels","Incorrect sample size"};

		private String error_;
		private ErrorType errno_;

		public SourceException(ErrorType error)
		{
			errno_ = error;	
			error_ = errors[error.ordinal()];
		} 
		@Override
		public String toString()
		{
			return error_;
		}

		public String Error()
		{
			return error_;
		}
		public ErrorType Errno()
		{
			return errno_;
		}
	}


	private InputStream stream_;

	public interface AudioReceiver
	{
		public void OnSamplesReceived( float[] db);
		public void OnCompleted();
	}


	public enum Channel
	{
		LEFT_CHANNEL,
		RIGHT_CHANNEL
	}

	private final int frame_size_;
	private final int read_chunk_size_;
	private final Settings settings_;
	private Map<Channel, Vector<AudioReceiver>> receivers_ = new TreeMap<Channel, Vector<AudioReceiver>>();
	
	public Settings GetSettings()
	{
		return settings_;
	}

	private Thread thread_;
	private RingBuffer ring_buffer_ ;
	private byte[] read_buffer_;
	private byte[] process_buffer_;
	private OutputStream out_;
	private final float left_kill_gate_;
	private final float right_kill_gate_;
	
	public Source(InputStream stream, Settings settings, int buffer_count, float left_kill_gate, float right_kill_gate)
	{
		this(stream,settings,buffer_count, left_kill_gate, right_kill_gate, null);
	}
	
	public Source(InputStream stream, Settings settings, int buffer_count, float left_kill_gate, float right_kill_gate, OutputStream out)
	{
		stream_ = stream;
		frame_size_ = settings.Channels() * settings.SampleSize();
		read_chunk_size_ =  settings.WindowSize() * frame_size_;
		read_buffer_ =  new byte[read_chunk_size_];
		process_buffer_ = new byte[read_chunk_size_];
		ring_buffer_ = new RingBuffer(read_chunk_size_* buffer_count);
		settings_ = settings;
		out_ = out;
		left_kill_gate_ = left_kill_gate;
		right_kill_gate_ = right_kill_gate;
	}



	public void RegisterAudioReceiver(Channel channel, AudioReceiver receiver)
	{
		if (receivers_.get(channel) == null)
		{
			receivers_.put(channel, new Vector<AudioReceiver>());
		}
		receivers_.get(channel).add(receiver);
	}

	private float  convertFromAr(byte[] b, int start)
	{		
		ByteBuffer buf =  ByteBuffer.wrap(b, start, settings_.SampleSize());
		float ret = Float.MIN_VALUE;
		switch (settings_.SampleSize())
		{
			case 1:
				ret =  (float) buf.get() / Byte.MAX_VALUE;
			break;
			case 2:
				buf.order(settings_.IsBigEndian()? ByteOrder.BIG_ENDIAN :  ByteOrder.LITTLE_ENDIAN);
				ret = (float) buf.getShort() / Short.MAX_VALUE;
			break;
			case 4:
				buf.order(settings_.IsBigEndian()? ByteOrder.BIG_ENDIAN :  ByteOrder.LITTLE_ENDIAN);
				ret = (float)buf.getInt() / Integer.MAX_VALUE;
			break;
		}		
		return ret;		
		//final float res = 	Math.max((float) buf.getShort() / Short.MAX_VALUE - Config.Instance().KillGate(), 0);   ;
		//return (res - Config.Instance().KillGate() <= 0) ? 0 : res;
	} 

	private void complete()
	{
		Vector<AudioReceiver> left_recv = receivers_.get(Channel.LEFT_CHANNEL);
		Vector<AudioReceiver> right_recv = receivers_.get(Channel.RIGHT_CHANNEL);

		if (left_recv != null)
		{	
			for (AudioReceiver recv : left_recv)
			{
					recv.OnCompleted();
			}
			
		}
		
		if (right_recv	 != null)
		{
			for (AudioReceiver recv : right_recv)
			{
				recv.OnCompleted();
			}
		}
	}
	
	private void process(byte[] buf, int len)
	{
		final int channels  = settings_.Channels();
		final int window_size = settings_.WindowSize();		
		final float length = len - frame_size_;
		final int sample_size = settings_.SampleSize();				
		Vector<AudioReceiver> left_recv = receivers_.get(Channel.LEFT_CHANNEL);
		Vector<AudioReceiver> right_recv = receivers_.get(Channel.RIGHT_CHANNEL);
		
		float[] left = null;
		float[] right = null;
		float max_left = 0.0f;
		float max_right = 0.0f;
		
		final int r_len = len / (sample_size * channels);
				
		int j =  0;
		if (channels == 2)
		{
			if (left_recv != null && right_recv!=null)
			{
				left = new float[r_len];
				right = new float[r_len];
				for (int i = 0; i < length; i+=frame_size_, j++)
				{				
						final float left_sample  = convertFromAr(buf, i);
						final float right_sample = convertFromAr(buf, i + sample_size);
						left[j] = left_sample;					
						right[j] =  right_sample;
						max_left = Math.max(max_left, Math.abs(left_sample));
						max_right = Math.max(max_right, Math.abs(right_sample));						
				}				
		
			}
			else if (left_recv!=null)
			{
				left = new float[r_len];
				for (int i = 0; i < length; i+=frame_size_, j++)
				{				
						final float left_sample  = convertFromAr(buf, i);
						left[j] = left_sample;					
						max_left = Math.max(max_left, Math.abs(left_sample));
				}
			}
			else
			{
				right = new float[r_len];
				for (int i = 0; i < length; i+=frame_size_, j++)
				{				
					final float right_sample = convertFromAr(buf, i + sample_size);
					right[j] =  right_sample;
					max_right = Math.max(max_right, Math.abs(right_sample));			
				}				
			}
		}
		else
		{
			left = right = new float[len / sample_size ];
			for (int i = 0; i < length; i+=frame_size_, j++)
			{	
				final float sample  = convertFromAr(buf, i);
				left[j] =  sample;
				max_left = max_right = Math.max(max_left, Math.abs(sample));			
			}
		}
	
		if (left != null && left_recv != null)
		{	
			for (AudioReceiver recv : left_recv)
			{
					recv.OnSamplesReceived(left);
			}
		}
		
		if (right != null && right_recv	 != null)
		{
			for (AudioReceiver recv : right_recv)
			{
				recv.OnSamplesReceived( right);
			}
		}
	}

	int readed = 0;
	public  void Process()	
	{
		if (receivers_.isEmpty()) 
		{
			Dbg.Warn("There aren't any receivers has been registered");
			return;
		}
		
		thread_ = new Thread(this);
		thread_.setDaemon(true);
		thread_.start();

		while (true)
		{
				synchronized(this)
				{
					if (thread_.isAlive())
					{
						try {
							wait(10000);
						} catch (InterruptedException e) {
							e.printStackTrace();
							return;
						}
					}
					else
					{
						if (ring_buffer_.getAvailable()  < read_chunk_size_)
						{
							final int len =  ring_buffer_.getAvailable();
							ring_buffer_.get(process_buffer_,0,len);
							process(process_buffer_, len);						
							complete();
							break;
						}
					}
				}
				
				while (ring_buffer_.getAvailable() >= read_chunk_size_)
				{
					Dbg.Debug("Read:%d / %d", ring_buffer_.getAvailable(),read_chunk_size_);
					ring_buffer_.get(process_buffer_,0,read_chunk_size_);		
					Dbg.Debug("Mod:%d",ring_buffer_.getAvailable());
					process(process_buffer_, read_chunk_size_);			
				}
		}
	}


	public InputStream Stream()
	{
		return stream_;
	}	
	
	@Override
	public  void  run() 
	{
		while (true)
		{
				try 
				{					
					int ret = stream_.read(read_buffer_);			
					if (ret == -1)
					{
						Dbg.Info("Nothing to read");
						break;
					}										
					if (ret > 0 )
					{							
						if (out_ != null)
						{
							out_.write(read_buffer_, 0, ret);
						}						
						ring_buffer_.put(read_buffer_, 0, ret);
						if (ring_buffer_.getAvailable() >= read_chunk_size_  || ring_buffer_.putAvailable() < read_chunk_size_ )
						{
				//			Utils.Dbg("Write: %d / %d",ring_buffer_.getAvailable(), read_chunk_size_);
							synchronized(this)
							{
								notify();
							}
						}
					}
				} 
			catch (IOException e) 
			{
				Dbg.Error("EXCEPTION:%s",e.getMessage());
				e.printStackTrace();
				break;
			}
		}
		
		synchronized(this)
		{
			notify();
		}

	}

}
