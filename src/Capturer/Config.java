package Capturer;
import Common.Utils;

public class Config  
{
	private String storage_;
	private String channel_;
	

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
		storage_ = Utils.CompletePath(Common.Config.Instance().GetProperty("storage",""));
		channel_ = Utils.CompletePath(Common.Config.Instance().GetProperty("channel","left"));
	}
	
	public String Storage()
	{
		return storage_;
	}
	
	public String Channel()
	{
		return channel_;
	}
}
