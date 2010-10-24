package Common;
import java.io.FileInputStream;
import java.util.*;

public class Config {
	
	private int sample_rate_ = 44100;
	private int window_size_ = 4096;
	private int overlapped_coef_ = 3;
	private double level_limit_ = 0.1;
	private int min_frequency_ = 20;
	private int max_frequency_ = 20000;
	
	private static Config instance_ = null;
	
	private boolean loaded_ = false;
	
	private Properties properties_;
	
	static public Config Instance()
	{
		if (instance_ == null)
		{
			instance_ = new Config();		
		}
		return instance_;
	}
	
	public boolean IsLoaded()
	{
		return loaded_;
	}
	
	
	public void Load(String filename)
	{
		  try{
			  
			  loaded_ = true;
		      Properties p = new Properties();
		      properties_ = p;
		      p.load(new FileInputStream(filename));
		      
		      sample_rate_ = Integer.decode( p.getProperty("sample_rate", "44100"));
		      window_size_ = Integer.decode(p.getProperty("window_size", "4096"));
		      overlapped_coef_ = Integer.decode(p.getProperty("overlapped_coef","4"));
		      level_limit_ = Double.valueOf(p.getProperty("level_limit","0.1"));
		      
		      min_frequency_ = Integer.decode(p.getProperty("min_frequency","20"));
		      max_frequency_ = Integer.decode(p.getProperty("max_frequency","20000"));
		  }
		 catch (Exception e) 
		 {
		    	
		  }
	}
	
	public int SampleRate()
	{
		return sample_rate_;
	}
	
	public int WindowSize()
	{
		return window_size_;
	}
	
	public int OverlappedCoef()
	{
		return overlapped_coef_;
	}
	
	public double LevelLimit()
	{
		return level_limit_;
	}
	
	public int MinFrequency()
	{
		return min_frequency_;
	}
	public int MaxFrequency()
	{
		return max_frequency_;
	}
	
	public String GetProperty(String name, String def)
	{
		return properties_.getProperty(name,def);
	}
}
