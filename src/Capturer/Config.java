package Capturer;

import java.awt.datatransfer.StringSelection;
import java.io.File;

import Common.Utils;
public class Config  
{
	private String storage_;

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
		storage_ = Common.Config.Instance().GetProperty("storage","");
		if (!storage_.isEmpty() && ! storage_.endsWith(System.getProperty("file.separator")))
		{
			storage_ += System.getProperty("file.separator");
		}
	}
	
	
	
	public String Storage()
	{
		return storage_;
	}
}
