package Common;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

public class Settings 
{
	private final int sample_rate_;
	private final int channels_;
	private final int sample_size_;
	private final int window_size_;
	private final Encoding encoding_;
	private final boolean is_big_endian_;
	
	public Settings(AudioFormat format)
	{
		channels_ = format.getChannels();
		sample_rate_ = (int)format.getSampleRate();
		encoding_ = format.getEncoding();
		sample_size_ = format.getSampleSizeInBits() / 8;
		is_big_endian_ = format.isBigEndian();
		window_size_ = Utils.GreaterBinary(sample_rate_ );	
		Dbg.Debug(this);
	}
	
	public final boolean IsBigEndian()
	{
		return is_big_endian_;
	}
	
	public final int Channels()
	{
		return channels_;
	}
	
	public final int SampleSize()
	{
		return sample_size_;
	}
	
	public final int SampleRate()
	{
		return sample_rate_ ;
	}
	
	public final int WindowSize()
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
