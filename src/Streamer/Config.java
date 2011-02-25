package Streamer;

import Common.Dbg;
import Common.SourceParser;
import Common.Utils;
import Common.JSON.JSONException;
import Common.JSON.JSONObject;

public class Config {

	public class Channel
	{
		private int min_frequency_ = 20;
		private int max_frequency_ = 20000;
		private float kill_gate_ = Float.NEGATIVE_INFINITY;
		private boolean exists_ = false;
		private String id_ = null;
		
		public Channel()
		{
			
		}		
		public Channel(JSONObject json)
		{
			exists_ = true;
			
			try
			{
				id_ = json.getString("id");
			}
			catch (JSONException e)
			{
				exists_ = false;
			}
			
			try
			{
				int kg   = json.getInt("kill_gate");
				kill_gate_ =  (float)Math.pow(10,  kg / 20 );  
			}
			catch (JSONException e)	{ }
			
			try
			{
				min_frequency_ = json.getInt("min_frequency");
			}
			catch (JSONException e)	{ }
			
			try
			{
				max_frequency_ = json.getInt("max_frequency");
			}
			catch (JSONException e)	{ }
			
			
		}
		
		public boolean IsExists()
		{
			return exists_;
		}
		
		public String Id()
		{
			return id_;
		}
		
		public int MinFrequency()
		{
			return min_frequency_;
		}
		public int MaxFrequency()
		{
			return max_frequency_;
		}
		
		public float KillGate()
		{
			return kill_gate_;
		}
	}

	
	static private Config instance_ = null;
	static public Config Instance()
	{
		if (instance_ == null)
		{
			instance_ = new Config();
		}		
		return instance_;
	}

	private String result_program_ = new String();
	private String storage_;
	private SourceParser source_;
	private boolean ignore_empty_stream_ = false;	
	private float overlapped_coef_ = 1.0f;
	private int ignored_errors_ = 0; 
	private float  equals_ = 1.0f;

	private Channel left_ = null;
	private Channel right_ = null;
	
	private Config()
	{
		try
		{
			result_program_ = Common.Config.Instance().Get("result_program", "");
			source_ = new SourceParser(Common.Config.Instance().Get("source", ""));
			storage_ = Common.Config.Instance().Get("storage", "");
			ignore_empty_stream_ = Common.Config.Instance().GetBool("ignore_empty_stream",ignore_empty_stream_);
			overlapped_coef_ =  (float) Common.Config.Instance().GetDouble("overlapped_coef",overlapped_coef_);
			ignored_errors_ = Common.Config.Instance().GetInt("ignored_errors",ignored_errors_);
			equals_ =  (float) Common.Config.Instance().GetDouble("equals",equals_);			
			
			String dbg = "";
			  try
			  {
					left_ = new Channel(Common.Config.Instance().JSON().getJSONObject("channels").getJSONObject("left"));
					dbg += String.format("Left Id: %s\nLeft Frequency: [%d - %d]\nLeft Kill Gate: %.05f\n",left_.Id(),left_.MinFrequency(), left_.MaxFrequency(), left_.KillGate());
			  }
			  catch (JSONException e)
			  {
				  	left_ = new Channel();
			  }				
			  try
			  {
				 right_ = new Channel(Common.Config.Instance().JSON().getJSONObject("channels").getJSONObject("right"));
				 dbg += String.format("Right Id: %s\nRight Frequency: [%d - %d]\nRight Kill Gate: %.05f\n",right_.Id(), right_.MinFrequency(), right_.MaxFrequency(), right_.KillGate());
			  }
			  catch (JSONException e)
			  {
				  right_ = new Channel();
			  }	
			  
			Dbg.Debug(dbg+"Source: %s\nStorage: %s\nIgnore Empty Stream: %s\nOverlapped Coefficient: %.05f\nIgnored Errors: %d\nEquals: %.05f\n",
					source_.Source(),
					storage_,
					ignore_empty_stream_,
					overlapped_coef_,
					ignored_errors_,
					equals_);			
		} 
		catch (Exception e)
		{
			
		}
	}
	
	
	
	public Channel LeftChannel()
	{
		return left_;
	}
	
	public Channel RightChannel()
	{
		return right_;
	}

	
	public boolean IgnoreEmptyStream()
	{
		return ignore_empty_stream_;
	}
	
	public SourceParser Source()
	{
		return source_;
	}
	
	public  String ResultProgram()
	{
		return result_program_;
	}
	
	public String Storage()
	{
		return storage_;
	}
	
	public float OverlappedCoef()
	{
		return overlapped_coef_;
	}
	
	public int IgnoredErrors()
	{
		return ignored_errors_;
	}
	public float Equals()
	{
		return equals_;
	}
}
