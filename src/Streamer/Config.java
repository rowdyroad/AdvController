package Streamer;

import Common.Utils;

public class Config {

	static private Config instance_ = null;
	static public Config Instance()
	{
		if (instance_ == null)
		{
			instance_ = new Config();
		}
		
		return instance_;
	}
	
	private String external_program_ = new String();
	private double fingerprint_equivalency_ = 0.6 ;
	private String promos_path_;
	private String source_;
	private int sample_rate_ = 44100;
	private int channels_ = 2;
	private int left_min_frequency_ = 20;
	private int left_max_frequency_ = 20000;
	private int right_min_frequency_ = 20;
	private int right_max_frequency_ = 20000;
	private boolean ignore_empty_stream_ = false;
	
	private Config()
	{
		try
		{
			external_program_ = Common.Config.Instance().GetProperty("external_program", "");
			sample_rate_ = Integer.parseInt(Common.Config.Instance().GetProperty("sample_rate", Integer.toString(sample_rate_)));
			channels_ = Integer.parseInt(Common.Config.Instance().GetProperty("channels", Integer.toString(channels_)));
			
			source_ = Common.Config.Instance().GetProperty("source","soundcard");
			promos_path_ = Common.Config.Instance().GetProperty("storage", ".");
			promos_path_ = (promos_path_.isEmpty()) ? "./" : Utils.CompletePath(promos_path_);
			fingerprint_equivalency_ =  Double.parseDouble(Common.Config.Instance().GetProperty("equivalency", Double.toString(fingerprint_equivalency_)));
			left_min_frequency_ = Integer.parseInt(Common.Config.Instance().GetProperty("left_min_frequency", Integer.toString(left_min_frequency_)));
			left_max_frequency_ = Integer.parseInt(Common.Config.Instance().GetProperty("left_max_frequency", Integer.toString(left_max_frequency_)));
			right_min_frequency_ = Integer.parseInt(Common.Config.Instance().GetProperty("right_min_frequency", Integer.toString(right_min_frequency_)));
			right_max_frequency_ = Integer.parseInt(Common.Config.Instance().GetProperty("right_max_frequency", Integer.toString(right_max_frequency_)));
			ignore_empty_stream_ = Boolean.parseBoolean(Common.Config.Instance().GetProperty("ignore_empty_stream", Boolean.toString(ignore_empty_stream_)));
		} 
		catch (Exception e)
		{
			
		}
	}
	
	public boolean IgnoreEmptyStream()
	{
		return ignore_empty_stream_;
	}
	public int Channels()
	{
		return channels_;
	}
	
	public int LeftMinFrequency()
	{
		return left_min_frequency_;
	}
	
	public int LeftMaxFrequency()
	{
		return left_max_frequency_;
	}

	public int RightMinFrequency()
	{
		return left_min_frequency_;
	}
	
	public int RightMaxFrequency()
	{
		return left_max_frequency_;
	}
	
	public int SampleRate()
	{
		return sample_rate_;
	}
	public String Source()
	{
		return source_;
	}
	public  String ExternalProgram()
	{
		return external_program_;
	}
	public String PromosPath()
	{
		return promos_path_;
	}
	public double FingerPrintEquivalency()
	{
		return fingerprint_equivalency_;
	}
	
}
