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
	
	private String external_program_ = new String();;
	private double fingerprint_equivalency_ = 0.9 ;
	private String promos_path_;
	private String source_;
	private Config()
	{
		try
		{
			external_program_ = Common.Config.Instance().GetProperty("external_program", "");
			source_ = Common.Config.Instance().GetProperty("source","soundcard");
			promos_path_ = Common.Config.Instance().GetProperty("storage", ".");
			promos_path_ = (promos_path_.isEmpty()) ? "./" : Utils.CompletePath(promos_path_);
			fingerprint_equivalency_ =  Double.parseDouble(Common.Config.Instance().GetProperty("equality", "1"));
		} 
		catch (Exception e)
		{
			
		}
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
