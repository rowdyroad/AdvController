package Streamer;

import Common.SourceParser;
import Common.Utils;
import Common.JSON.JSONException;
import Common.JSON.JSONObject;

public class Config {

	static private Config instance_ = null;
	static public Config Instance()
	{
		if (instance_ == null)
		{
			instance_ = new Config();
		}		
		return instance_;
	}
	
	class Channel
	{
		String key_;
		int min_frequency_;
		int max_frequency_;
		
		public Channel(String key, int min, int  max)
		{
			key_ = key;
			min_frequency_ = min;
			max_frequency_ = max;
		}
		public Channel(JSONObject json)
		{
			try {
				key_ = json.getString("key");
				min_frequency_ = json.getInt("min_frequency");
				max_frequency_ = json.getInt("max_frequency");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public String Key()
		{
			return key_;
		}
		public int MinFrequency()
		{
			return min_frequency_;
		}
		public int MaxFrequency()
		{
			return max_frequency_;
		}
	}
	
	
	private String result_program_ = new String();
	private String storage_;
	private SourceParser source_;
	private Channel left_ = null;
	private Channel right_ = null;
	private boolean ignore_empty_stream_ = false;	
	private float overlapped_coef_ = 1.0f;
	private int ignored_errors_ = 0; 
	private float  equals_ = 1.0f;

	private Config()
	{
		try
		{
			result_program_ = Common.Config.Instance().Get("result_program", "");
			source_ = new SourceParser(Common.Config.Instance().Get("source", ""));
			storage_ = Common.Config.Instance().Get("storage", "");
			ignore_empty_stream_ = Common.Config.Instance().GetBool("ignore_empty_stream",ignore_empty_stream_);
			overlapped_coef_ =  (float) Common.Config.Instance().GetDouble("overlapped_coef",overlapped_coef_);
			try
			{
				left_ = new Channel(Common.Config.Instance().JSON().getJSONObject("channels").getJSONObject("left"));
			}
			catch (Exception e)
			{
				left_ = null;
			}
			
			try
			{
				right_ = new Channel(Common.Config.Instance().JSON().getJSONObject("channels").getJSONObject("right"));
			}
			catch (Exception e)
			{
				right_ = null;
			}
			ignored_errors_ = Common.Config.Instance().GetInt("ignored_errors",ignored_errors_);
			equals_ =  (float) Common.Config.Instance().GetDouble("equals",equals_);			
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
