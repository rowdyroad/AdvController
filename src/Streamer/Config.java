package Streamer;

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
	
	private String external_program_;
	private double gistogram_equivalency_;
	private double fingerprint_equivalency_ ;
	private int unequal_window_;
	private String promos_path_;
	private Config()
	{
		external_program_ = Common.Config.Instance().GetProperty("external_program", "");
		promos_path_ = Common.Config.Instance().GetProperty("promos_path", "");
		if (!promos_path_.isEmpty() && ! promos_path_.endsWith(System.getProperty("file.separator")))
		{
			promos_path_ += System.getProperty("file.separator");
		}
		
		gistogram_equivalency_  = Double.parseDouble(Common.Config.Instance().GetProperty("gistogram_equivalency", "1"));
		fingerprint_equivalency_ =  Double.parseDouble(Common.Config.Instance().GetProperty("fingerprint_equivalency", "0.75"));
		unequal_window_ = Integer.parseInt(Common.Config.Instance().GetProperty("unequal_window", "4"));
	}
	
	public  String ExternalProgram()
	{
		return external_program_;
	}
	public String PromosPath()
	{
		return promos_path_;
	}
	
	public double GistogramEquivalency()
	{
		return gistogram_equivalency_;
	}
	public double FingerPrintEquivalency()
	{
		return fingerprint_equivalency_;
	}
	
	public double UnequalWindow()
	{
		return unequal_window_;
	}
	
}
