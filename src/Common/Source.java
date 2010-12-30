package Common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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
	public Source(InputStream stream, Settings settings, int buffer_count)
	{
		stream_ = stream;
		frame_size_ = settings.Channels() * settings.SampleSize();
		read_chunk_size_ =  settings.WindowSize() * frame_size_;
		read_buffer_ =  new byte[read_chunk_size_];
		process_buffer_ = new byte[read_chunk_size_];
		ring_buffer_ = new RingBuffer(read_chunk_size_* buffer_count);
		settings_ = settings;
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
		return ( float)buf.getShort() / Short.MAX_VALUE;
	} 

	private void process(byte[] buf, int len)
	{
		 float[] left = new  float[settings_.WindowSize()];
		 float[] right = new  float[settings_.WindowSize()];
		int j = 0;	
		for (int i = 0; i < len - frame_size_; i+=frame_size_, j++)
		{
			if (settings_.Channels() == 2)
			{
				left[j] = convertFromAr(buf, i, i + 1);
				right[j] = convertFromAr(buf, i + 2, i + 3);
			}
			else
			{
				left[j] = right[j] = convertFromAr(buf, i, i + 1);
			}
		}
		
		for (int i =j; i< settings_.WindowSize() ; ++i)
		{
			left[i] = right[i] = 0;
		}

		Vector<AudioReceiver> left_recv = receivers_.get(Channel.LEFT_CHANNEL);
		Vector<AudioReceiver> right_recv = receivers_.get(Channel.RIGHT_CHANNEL);

		if (left_recv != null)
		{
			for (int i = 0; i < left_recv.size(); ++i)
			{
				left_recv.get(i).OnSamplesReceived(left);
			}
		}

		if (right_recv != null)
		{
			for (int i = 0; i < right_recv.size(); ++i)
			{
				right_recv.get(i).OnSamplesReceived(right);
			}
		}
	}

	int readed = 0;
	public  void Process()
	{
		if (receivers_.isEmpty()) 
		{
			Utils.Dbg("There aren't any receivers has been registered");
			return;
		}
		
		thread_ = new Thread(this);
		thread_.start();

		while (true)
		{
				synchronized(this)
				{
					if (thread_.isAlive())
					{
						try {
							wait();
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
					Utils.Dbg("Read:%d / %d", ring_buffer_.getAvailable(),read_chunk_size_);
					ring_buffer_.get(process_buffer_,0,read_chunk_size_);		
					Utils.Dbg("Mod:%d",ring_buffer_.getAvailable());
					process(process_buffer_, read_chunk_size_);			
				}
				
				/*while (! buffer_.isEmpty())
				{				
					Buffer buf = buffer_.get(0);
					buffer_.remove(0);
					if (buf == null)
					{
						if (cache_len_> 0) 
						{
							process(cache_, cache_len_);
						}
						return;	
					}
			
					int ret = buf.length;
					byte[] b = buf.buffer;
					
					readed+=ret;
					if (ret < readChunkSize_ || cache_len_  > 0)
					{
						int len2cache = Math.min(readChunkSize_ - cache_len_, ret);
						System.arraycopy(b, 0, cache_, cache_len_, len2cache);
						cache_len_+= len2cache;
						if (cache_len_ == readChunkSize_)
						{
							process(cache_,cache_len_);
							if (len2cache < ret)
							{
								cache_len_ = ret - len2cache;
								System.arraycopy(b,len2cache, cache_,0, cache_len_);
							}
							else
							{
								continue;
							}
						}
					}
					else
					{
						process(b,ret);
						continue;
					}
				}*/
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
					//Utils.Dbg("Read from in:%d",ret);
					if (ret <= 0)
					{
						Utils.Dbg("Nothing to read");
						synchronized(this)
						{
							notify();
							break;
						}
					}
					if (ret > 0 )
					{							
						ring_buffer_.put(read_buffer_, 0, ret);
						if (ring_buffer_.getAvailable() >= read_chunk_size_  || ring_buffer_.putAvailable() < read_chunk_size_ )
						{
							Utils.Dbg("Write: %d / %d",ring_buffer_.getAvailable(), read_chunk_size_);
							synchronized(this)
							{
								notify();
								Thread.yield();
							}
						}
					}
				} 
			catch (IOException e) 
			{
				Utils.Dbg("EXCEPTION:%s",e.getMessage());
				e.printStackTrace();
				synchronized(this)
				{
					notify();
					break;
				}
			}
		}
	}

}
