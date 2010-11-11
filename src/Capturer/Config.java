package Capturer;
import Common.Utils;

public class Config  
{
	private String storage_ = new String();

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
	}
	
	public String Storage()
	{
		return storage_;
	}
}
