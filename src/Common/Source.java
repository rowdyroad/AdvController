package Common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
	}


	public enum Channel
	{
		LEFT_CHANNEL,
		RIGHT_CHANNEL
	}

	private int frame_size_;
	private int read_chunk_size_;
	private Settings settings_;
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
	
	public Source(InputStream stream, Settings settings, int buffer_count)
	{
		this(stream,settings,buffer_count,null);
	}
	
	public Source(InputStream stream, Settings settings, int buffer_count, OutputStream out)
	{
		stream_ = stream;
		frame_size_ = settings.Channels() * settings.SampleSize();
		read_chunk_size_ =  settings.WindowSize() * frame_size_;
		read_buffer_ =  new byte[read_chunk_size_];
		process_buffer_ = new byte[read_chunk_size_];
		ring_buffer_ = new RingBuffer(read_chunk_size_* buffer_count);
		settings_ = settings;
		out_ = out;
	}



	public void RegisterAudioReceiver(Channel channel, AudioReceiver receiver)
	{
		if (receivers_.get(channel) == null)
		{
			receivers_.put(channel, new Vector<AudioReceiver>());
		}
		receivers_.get(channel).add(receiver);
	}

	private float  convertFromAr(byte[] b, int start, int end)
	{
		
		ByteBuffer buf =  ByteBuffer.wrap(b, start, end - start + 1);
		buf.order(settings_.IsBigEndian()? ByteOrder.BIG_ENDIAN :  ByteOrder.LITTLE_ENDIAN);
		return (float) buf.getShort() / Short.MAX_VALUE;
		//final float res = 	Math.max((float) buf.getShort() / Short.MAX_VALUE - Config.Instance().KillGate(), 0);   ;
		//return (res - Config.Instance().KillGate() <= 0) ? 0 : res;
	} 

	private void process(byte[] buf, int len)
	{
		float[] left = new  float[settings_.WindowSize()];
		float[] right = new  float[settings_.WindowSize()];
		int j = 0;
		
		float max_left = 0;
		float  max_right = 0;
		
		Vector<AudioReceiver> left_recv = receivers_.get(Channel.LEFT_CHANNEL);
		Vector<AudioReceiver> right_recv = receivers_.get(Channel.RIGHT_CHANNEL);
		
		for (int i = 0; i < len - frame_size_; i+=frame_size_, j++)
		{
			if (settings_.Channels() == 2)
			{
				if (left_recv != null)
				{
					
					left[j] = convertFromAr(buf, i, i + 1);
						
				}
				
				if (right_recv != null)
				{
					right[j] = convertFromAr(buf, i + 2, i + 3);
				}
			}
			else
			{	
				left[j] = right[j] = convertFromAr(buf, i, i + 1);
			}
			//Dbg.Info("%02X %02X = %f",buf[i],buf[i+1],left[j]);
			
			max_left = Math.max(max_left, left[j]);
			max_right = Math.max(max_right, right[j]);
		}
		
		for (int i =j; i< settings_.WindowSize() ; ++i)
		{			
			left[i] = right[i] = 0;
		}

		if (left_recv != null)
		{
			
			for (int i = 0; i < left_recv.size(); ++i)
			{
				left_recv.get(i).OnSamplesReceived(max_left  > Common.Config.Instance().KillGate() ? left : null);
			}
		}

		if (right_recv != null)
		{
			for (int i = 0; i < right_recv.size(); ++i)
			{
				right_recv.get(i).OnSamplesReceived(max_right > Common.Config.Instance().KillGate() ? right : null);
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
