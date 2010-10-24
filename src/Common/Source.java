package Common;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.sound.sampled.AudioInputStream;

public class Source {

	private AudioInputStream stream_;
	
	
	public interface AudioReceiver
	{
		public void OnSampleReceived(double db);
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
		if (stream.getFormat().getChannels() > 2) throw new Exception();
		if (stream.getFormat().getSampleRate() != Config.Instance().SampleRate()) throw new Exception();
		if (stream.getFormat().getSampleSizeInBits() != 16) throw new Exception();

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
		
		 return (double)( (int)(stream_.getFormat().isBigEndian() ? b[end] << 8 | b[start] : b[start] << 8 | b[end]) * scale_);
	}
	public Boolean Read()
	{
		byte[] b = new byte[frameSize_];	
		int c;
		try
		{
			c = stream_.read(b);
			
			if (c ==-1)
			{
				return false;
			}
		}
		catch (IOException e)
		{
			return false;
		}
		
		double left = 0, right = 0;
		
		if (channels_ == 2)
		{
			left = convertFromAr(b, 0,1);
			right = convertFromAr(b, 2,3);
		}
		else
		{
			left = right = convertFromAr(b, 0,1);
		}
		
		Vector<AudioReceiver> left_recv = receivers_.get(Channel.LEFT_CHANNEL);
		Vector<AudioReceiver> right_recv = receivers_.get(Channel.RIGHT_CHANNEL);
		
		if (left_recv!=null)
		{
			for (int i=0; i < left_recv.size(); ++i)
			{
				left_recv.get(i).OnSampleReceived(left);
			}
		}

		if (right_recv!=null)
		{
			for (int i=0; i < right_recv.size(); ++i)
			{
				right_recv.get(i).OnSampleReceived(right);
			}
		}
		return true;
	}
	
	public AudioInputStream Stream()
	{
		return stream_;
	}
	
}
