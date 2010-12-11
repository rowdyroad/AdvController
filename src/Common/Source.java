package Common;

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
		public void OnSamplesReceived(double[] db);
	}


	public enum Channel
	{
		LEFT_CHANNEL,
		RIGHT_CHANNEL
	}

	private int frameSize_;
	private int readChunkSize_;
	private byte[] cache_;
	private int cache_len_ = 0;
	private Settings settings_;
	private Map<Channel, Vector<AudioReceiver>> receivers_ = new TreeMap<Channel, Vector<AudioReceiver>>();

	private class Buffer
	{
		byte[] buffer;
		int length;
		public Buffer(byte[] buffer, int length)
		{
			this.buffer = buffer;
			this.length = length;
		}
	}

	private List<Buffer> buffer_ = Collections.synchronizedList(new LinkedList<Buffer>());
	private Thread thread_;

	public Source(InputStream stream, Settings settings) throws Exception
	{
		stream_ = stream;
		frameSize_ = settings.Channels() * settings.SampleSize();
		readChunkSize_ =  settings.WindowSize() * frameSize_;
		cache_ = new byte[readChunkSize_];
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

	private double convertFromAr(byte[] b, int start, int end)
	{
		ByteBuffer buf =  ByteBuffer.wrap(b, start, end - start + 1);
		buf.order(settings_.IsBigEndian()? ByteOrder.BIG_ENDIAN :  ByteOrder.LITTLE_ENDIAN);
		return (double)buf.getShort() / Short.MAX_VALUE;
	} 

	private void process(byte[] buf, int len)
	{
		double[] left = new double[settings_.WindowSize()];
		double[] right = new double[settings_.WindowSize()];
		int j = 0;	
		for (int i = 0; i < len - frameSize_; i+=frameSize_, j++)
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
				}
				
				while (! buffer_.isEmpty())
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
			byte[] buf = new byte[readChunkSize_];
			int ret;
			try {
				ret = stream_.read(buf);
				if (ret == -1)
				{
					buffer_.add(null);
					synchronized(this)
					{
						notify();
					}
					return;
				}
				
				byte max = 0;
				for (int i = 0; i < ret; ++i)
				{
					max = (byte) Math.max(max, buf[i]);
				}
				buffer_.add(new Buffer(buf, ret));				
				synchronized(this)
				{
					notify();
				}
			} 
			catch (IOException e) 
			{
				buffer_.add(null);
				synchronized(this)
				{
					notify();
				}
				e.printStackTrace();

				return;
			}
		}
	}

}
