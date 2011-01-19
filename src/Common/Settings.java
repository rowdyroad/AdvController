package Common;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

public class Settings 
{
	private int sample_rate_;
	private int channels_;
	private int sample_size_;
	private int window_size_;
	private Encoding encoding_;
	private boolean is_big_endian_;
	
	public Settings(AudioFormat format)
	{
		channels_ = format.getChannels();
		sample_rate_ = (int)format.getSampleRate();
		encoding_ = format.getEncoding();
		sample_size_ = format.getSampleSizeInBits() / 8;
		is_big_endian_ = format.isBigEndian();
		window_size_ = Utils.GreaterBinary(sample_rate_ );
		Dbg.Info(this);
	}
	
	public Boolean IsBigEndian()
	{
		return is_big_endian_;
	}
	
	public Integer Channels()
	{
		return channels_;
	}
	
	public Integer SampleSize()
	{
		return sample_size_;
	}
	
	public Integer SampleRate()
	{
		return sample_rate_ ;
	}
	


	public Integer WindowSize()
	{
		return window_size_;
	}
	
	@Override
	public String toString()
	{
		return String.format("SampleRate:%d\nChannels:%d\nEcoding:%s\nSampleSize:%d\nIsBigEndian:%b\nWindowSize:%d\n",
									   sample_rate_ ,channels_,encoding_, sample_size_,is_big_endian_ ,window_size_);
		
	}

}
