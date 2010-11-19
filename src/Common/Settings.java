package Common;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public class Settings {
	
	private int deltaFrequency = 8;
	private int overlapped_length_;
	private int process_start_;
	private int process_stop_;
	private int window_size_;
	private int sample_rate_;
	private int channels_;
	private int sample_size_;
	private int fft_window_;
	private boolean is_big_endian_;
	
	

	public Settings(AudioFormat format)
	{
		sample_rate_ = (int)format.getSampleRate();
		channels_ = format.getChannels();
		sample_size_ = format.getSampleSizeInBits() / 8;
		is_big_endian_ = format.isBigEndian();
		window_size_ = Utils.GreaterBinary(deltaFrequency * sample_rate_ / (channels_ * Common.Config.Instance().FrequencyStep()));
		
		fft_window_ = Utils.GreaterBinary(sample_rate_ / (channels_ * Common.Config.Instance().FrequencyStep()));
		process_start_ = (int)Math.round((double)Common.Config.Instance().MinFrequency() * window_size_ / sample_rate_);
		process_stop_ = (int)Math.round((double)Common.Config.Instance().MaxFrequency() * window_size_ / sample_rate_);
		overlapped_length_ = window_size_ / Common.Config.Instance().OverlappedCoef();
		Utils.Dbg(this);
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
		return sample_rate_;
	}
	public Integer OverlappedLength()
	{
		return overlapped_length_;
	}
	
	public Integer ProcessStart()
	{
		return process_start_;
	}
	
	public Integer ProcessStop()
	{
		return process_stop_;
	}
	
	public Integer WindowSize()
	{
		return window_size_;
	}
	public Integer FFTWindowSize()
	{
		return fft_window_;
	}
	
	@Override
	public String toString()
	{
		return String.format("SampleRate:%d\nChannels:%d\nSampleSize:%d\nIsBigEndian:%b\nWindowSize:%d\nProcessStart:%d\nProcessStop:%d\nOverlappedLength:%d\n",
									   sample_rate_ ,channels_,sample_size_,is_big_endian_ ,window_size_,process_start_ ,process_stop_ ,overlapped_length_);
		
	}

}
