package Capturer;

public class Config  
{
	private String path_;
	
	public Config()
	{		
		path_ = Common.Config.Instance().GetProperty("path_","");
	}
	
	public String Path()
	{
		return path_;
	}

}
