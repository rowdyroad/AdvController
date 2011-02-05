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
	private double fingerprint_equivalency_ = 1 ;
	private String promos_path_;
	private String source_;
	private int sample_rate_ = 44100;
	private int channels_ = 2;
	private int left_min_frequency_ = 20;
	private int left_max_frequency_ = 20000;
	private int right_min_frequency_ = 20;
	private int right_max_frequency_ = 20000;
	private String left_key_;
	private String right_key_;
	private boolean ignore_empty_stream_ = false;	
	private float frame_equip_ = 0.1f;
	private int overlapped_coef_ = 1;
	private Config()
	{
		try
		{
			external_program_ = Common.Config.Instance().GetProperty("ep", "");
			sample_rate_ = Integer.parseInt(Common.Config.Instance().GetProperty("sr", Integer.toString(sample_rate_)));
			channels_ = Integer.parseInt(Common.Config.Instance().GetProperty("cc", Integer.toString(channels_)));			
			source_ = Common.Config.Instance().GetProperty("s","soundcard");
			promos_path_ = Common.Config.Instance().GetProperty("st", ".");
			promos_path_ = (promos_path_.isEmpty()) ? "./" : Utils.CompletePath(promos_path_);
			fingerprint_equivalency_ =  Double.parseDouble(Common.Config.Instance().GetProperty("e", Double.toString(fingerprint_equivalency_)));
			left_min_frequency_ = Common.Config.Instance().GetProperty("lf", left_min_frequency_);
			left_max_frequency_ = Common.Config.Instance().GetProperty("LF", left_max_frequency_);
			right_min_frequency_ = Common.Config.Instance().GetProperty("rf",right_min_frequency_);
			right_max_frequency_ = Common.Config.Instance().GetProperty("RF",right_max_frequency_);
			left_key_ = Common.Config.Instance().GetProperty("lk","");
			right_key_ = Common.Config.Instance().GetProperty("rk","");
			ignore_empty_stream_ = Boolean.parseBoolean(Common.Config.Instance().GetProperty("i", Boolean.toString(ignore_empty_stream_)));
			frame_equip_ = Float.parseFloat(Common.Config.Instance().GetProperty("fe", Float.toString(frame_equip_)));
			overlapped_coef_ =  Common.Config.Instance().GetProperty("oc",overlapped_coef_);
		} 
		catch (Exception e)
		{
			
		}
	}
	
	public String LeftKey()
	{
		return left_key_;
	}
	
	public String RightKey()
	{
		return right_key_;
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
	public float FrameEquip()
	{
		return frame_equip_;
	}
	
	public int OverlappedCoef()
	{
		return overlapped_coef_;
	}
}
