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
	private Config()
	{
		try
		{
			external_program_ = Common.Config.Instance().GetProperty("external_program", "");
			promos_path_ = Common.Config.Instance().GetProperty("promos_path", ".");
			promos_path_ = (promos_path_.isEmpty()) ? "./" : Utils.CompletePath(promos_path_);
			fingerprint_equivalency_ =  Double.parseDouble(Common.Config.Instance().GetProperty("fingerprint_equivalency", "0.9"));
		} 
		catch (Exception e)
		{
			
		}
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
