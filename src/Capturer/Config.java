package Capturer;
import Common.Utils;

public class Config  
{
	private String storage_;
	private String channel_;
	
	private int min_frequency_ = 20;
	private int max_frequency_ = 20000;
	
	private String promos_;
	
	private static Config instance_ = null;
	
	static public Config Instance()
	{
			if (instance_ == null)
			{
				instance_ = new Config();
			}
			
			return instance_;
	}
	
	private Config()
	{		
		storage_ = Utils.CompletePath(Common.Config.Instance().GetProperty("s",""));
		channel_ = Utils.CompletePath(Common.Config.Instance().GetProperty("c","left"));
		min_frequency_ = Common.Config.Instance().GetProperty("f",min_frequency_);
		max_frequency_ = Common.Config.Instance().GetProperty("F", max_frequency_);
		promos_ = Common.Config.Instance().GetProperty("p","");
	}
	
	public int MinFrequency()
	{
		return min_frequency_;
	}
	
	public int MaxFrequency()
	{
		return max_frequency_;
	}
	
	public String Storage()
	{
		return storage_;
	}
	
	public String Promos()
	{
		return promos_;
	}
	
	public String Channel()
	{
		return channel_;
	}
}
