package Common;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.sound.sampled.AudioInputStream;

public class Source {

	
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
	
	
	private AudioInputStream stream_;
	
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
	private int channels_;
	private double scale_;
	
	public Source(AudioInputStream stream) throws Exception
	{
		if (stream.getFormat().getChannels() > 2) throw new SourceException(SourceException.ErrorType.CHANNELS);
		if (stream.getFormat().getSampleRate() != Config.Instance().SampleRate()) throw new SourceException(SourceException.ErrorType.SAMPLE_RATE);
		if (stream.getFormat().getSampleSizeInBits() != 16) throw new SourceException(SourceException.ErrorType.SAMPLE_SIZE);

		stream_ = stream;
		frameSize_ = stream_.getFormat().getFrameSize();
		channels_ = stream_.getFormat().getChannels();
		scale_ = 1.0 / ((double) stream.getFormat().getChannels() * (1 << (stream.getFormat().getSampleSizeInBits() - 1)));
		
	}
	
	
	Map<Channel, Vector<AudioReceiver>> receivers_ = new TreeMap<Channel, Vector<AudioReceiver>>();
	
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
		 buf.order(stream_.getFormat().isBigEndian() ? ByteOrder.BIG_ENDIAN :  ByteOrder.LITTLE_ENDIAN);
		 return (double)buf.getShort() * scale_;
	}

	public Boolean Read()
	{
		byte[] b = new byte[frameSize_ * Config.Instance().WindowSize()];	
		int c;
		try
		{
			c = stream_.read(b);
			Utils.Dbg("%d received:%d",System.currentTimeMillis(),c);
			
			if (c ==-1)
			{
				return false;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		
		double[] left = new double[c / frameSize_];
		double[] right = new double[c / frameSize_];
	
		
		for (int i = 0, j = 0; i < c; i+=frameSize_, ++j)
		{
			if (channels_ == 2)
			{
				left[j] = convertFromAr(b, i, i + 1);
				right[j] = convertFromAr(b, i + 2,i + 3);
			}
			else
			{
				left[j] = right[j] = convertFromAr(b, i,i+1);
			}
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
		return true;
	}
	
	public AudioInputStream Stream()
	{
		return stream_;
	}
	
}
