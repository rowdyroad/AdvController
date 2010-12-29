package Common;
import java.io.FileInputStream;
import java.util.*;

public class Config {
	private int frequency_step_ = 10;
	private int overlapped_coef_ = 4;
	private int levels_count_= 20;
	private int min_frequency_ = 20;
	private int max_frequency_ = 10000;
	private double noise_gate_ = 0.0005;
	private static Config instance_ = null;
	public static String Filename = "soas.ini";
	private Properties properties_;
	
	private int buffer_count_ = 500;
	
	static public Config Instance()
	{
		if (instance_ == null)
		{
			instance_ = new Config(Config.Filename);		
		}
		
		return instance_;
	}
	
	private Config(String filename)
	{
		Load(filename);
	}
	
	private void Load(String filename)
	{
		  try{
		      Properties p = new Properties();
		      properties_ = p;
		      p.load(new FileInputStream(filename));
		      frequency_step_ = Integer.decode(p.getProperty("frequency_step",String.valueOf(frequency_step_)));
		      overlapped_coef_ = Integer.decode(p.getProperty("overlapped_coef",String.valueOf(overlapped_coef_)));
		      levels_count_ = Integer.valueOf(p.getProperty("levels_count",String.valueOf(levels_count_)));
		      buffer_count_ = Integer.valueOf(p.getProperty("buffer_count",String.valueOf(buffer_count_)));
		      min_frequency_ = Integer.decode(p.getProperty("min_frequency",String.valueOf(min_frequency_)));
		      max_frequency_ = Integer.decode(p.getProperty("max_frequency",String.valueOf(max_frequency_)));
			  noise_gate_ = Double.parseDouble(p.getProperty("noise_gate",String.valueOf(noise_gate_)));
		  }
		 catch (Exception e) 
		 {
		
		  }
	}
	
	public int BufferCount()
	{
		return buffer_count_;
		
	}
	public double NoiseGate()
	{
		return noise_gate_;
	}

	
	public int FrequencyStep()
	{
		return frequency_step_;
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
		return (properties_!=null) ? properties_.getProperty(name,def) : def;
	}
}
