package Common;

public  class Config {
	
	private int log_level_ = Dbg.Error | Dbg.Warning | Dbg.Info;
	private float noise_gate_ = 0;
	private float kill_gate_ = 0;
	private static Config instance_ = null;
	public static Args Arguments = null;	
	private int buffer_count_ = 100;
	private int fft_window_size_ = 8192;
	
	static public Config Instance()
	{
		if (instance_ == null)
		{
			instance_ = new Config(Arguments);		
		}
		
		return instance_;
	}
	
	private Config(Args arg)
	{
		Set(arg);
	}
	
	private void Set(Args  arg)
	{
		if (arg == null) return;
		try
		{
			      buffer_count_ = arg.Get("b",buffer_count_);
			  noise_gate_ = Float.parseFloat(arg.Get("ng",String.valueOf(noise_gate_)));
			  kill_gate_  =  Float.parseFloat(arg.Get("kg",String.valueOf(noise_gate_)));
			  log_level_ = arg.Get("L", log_level_);
			  if (noise_gate_ != 0)
			  {
				  noise_gate_ = (float) Math.pow(10,  noise_gate_ / 20 );  
			  }
			  
			  if (kill_gate_ != 0)
			  {
				  kill_gate_ = (float) Math.pow(10,  kill_gate_ / 20 );  
			  }			  
		  }
		 catch (Exception e) 
		 {
		
		  }
	}
	
	public int BufferCount()
	{
		return buffer_count_;
		
	}
	public float NoiseGate()
	{
		return noise_gate_;
	}
	public float KillGate()
	{
		return kill_gate_;
	}
	
	
	public int LogLevel()
	{
		return log_level_;
	}
	
	public int FFTWindowSize()
	{
		return fft_window_size_;
	}
	
	public String GetProperty(String name, String def)
	{
		return (Arguments !=null) ? Arguments.Get(name,def) : def;
	}
	
	public int GetProperty(String name, int def)
	{
		return (Arguments !=null) ? Arguments.Get(name,def) : def;
	}
}

