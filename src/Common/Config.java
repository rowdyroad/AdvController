package Common;

import java.io.File;
import java.io.IOException;

import Common.JSON.JSONException;
import Common.JSON.JSONObject;

public  class Config {
	
	
	private int log_level_ = Dbg.Error | Dbg.Warning | Dbg.Info;
	private static Config instance_ = null;
	public static Args Arguments = null;	
	private int buffer_count_ = 100;
	private int fft_window_size_ = 8192;
	private static String config_filename_ = null;
	private static String config_group_ = null;
	static public Config Instance()
	{
		if (instance_ == null)
		{
			instance_ = new Config(config_filename_, config_group_);		
		}		
		return instance_;
	}
	
	static public void Init(String filename, String group)
	{
		config_filename_ = filename;
		config_group_ = group;
	}	
	private JSONObject json_;
	
	public JSONObject JSON()
	{
		return json_;
	}
	
	private Config(String filename, String group)
	{
		if (filename == null || group == null)
		{
			return;
		}
		
		try 
		{
			json_ = new JSONObject(new File(filename)).getJSONObject(group);
		}
		catch (JSONException e)
		{
			Dbg.Warn("Couldn't load config filename '%s'. Load defaults.\n(%s)",filename,e.getMessage());		
		}
		catch (Exception e)
		{
			Dbg.Warn("Couldn't load config filename '%s'. Load defaults.\n(%s)",filename,e.getMessage());					
		}
		
		if (json_ == null)
		{
			return;
		}
		
		try
		{
			  buffer_count_ = GetInt("buffer_count",buffer_count_);
			  Dbg.LogLevel = log_level_ = GetInt("log_level", log_level_); 
			  fft_window_size_ = GetInt("fft_window_size",fft_window_size_);
			  Dbg.Debug("Buffer Count: %d\nLog Level: %d\nFFT Window Size: %d\n",buffer_count_,log_level_, fft_window_size_);			  			
		}
		 catch (Exception e) 
		 {
		
		  }
	}

	public int BufferCount()
	{
		return buffer_count_;
		
	}
	
	public int LogLevel()
	{
		return log_level_;
	}
	
	public int FFTWindowSize()
	{
		return fft_window_size_;
	}
	
	public String Get(String name, String def)
	{
		try
		{
			return json_.getString(name);
		}
		catch (Exception  e)
		{
			return def;
		}
	}
	
	public int GetInt(String name, Integer def)
	{
		try
		{
			return json_.getInt(name);
		}
		catch (Exception e)
		{
			return def;
		}
	}
	
	public boolean GetBool(String name, boolean def)
	{
		try
		{
			return json_.getBoolean(name);
		}
		catch (Exception e)
		{
			return def;
		}
	}
	
	public double GetDouble(String name, double def)
	{
		try
		{
			return json_.getDouble(name);
		}
		catch (Exception e)
		{
			return def;
		}
	}
	
	
	
	
}

