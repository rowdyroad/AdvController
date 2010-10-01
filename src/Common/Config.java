package Common;
import java.io.FileInputStream;
import java.util.*;

public class Config {
	
	private int sample_rate_ = 44100;
	private int window_size_ = 4096;
	private int overlapped_coef_ = 4;
	private int levels_count_= 5;
	private int min_frequency_ = 0;
	private int max_frequency_ = 20050;
	
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
		      levels_count_ = Integer.valueOf(p.getProperty("levels_count","5"));
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
	
	public int LevelsCount()
	{
		return levels_count_;
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
