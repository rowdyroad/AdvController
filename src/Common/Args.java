package Common;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Args {

		private Map<String,String> pairs_ = new TreeMap<String,String>();
		private List<String> items_ = new LinkedList<String>();
		static private String[] args = null;
		static private Args instance_ = null;
		
		public static void Init(String[] args)
		{
					Args.args = args;
		}
		
		public static Args Instance()
		{
			if (instance_ == null)
			{
				if (args == null)
					throw new IllegalArgumentException("Undefined arguments. Please init before");
				
				instance_ = new Args(args);
			}			
			return instance_;		
		}
		
		private Args(String[] args)
		{
			for (int i = 0; i < args.length;++i)
			{				
				if (i % 2 == 0 && args[i].startsWith("-") && i+1 < args.length)
				{
					pairs_.put(args[i].substring(1),args[++i]);
				}
				else
				{
					items_.add(args[i]);
				}
			}
		}	
		
		public Map<String,String> GetPairs()
		{
			return pairs_;
		}
		
		public String  Get(String key, String def)
		{
			return (pairs_.containsKey(key)) ? pairs_.get(key) : def;
		}
		
		public List<String> Items()
		{
			return items_;
		}

		public int  Get(String key, int def)
		{
			try
			{
				return (pairs_.containsKey(key)) ? Integer.valueOf(pairs_.get(key)) : def;
			}
			catch (NumberFormatException n)
			{
				return def;
			}
		}
		
		public double GetDouble(String key, double def)
		{
			try
			{
				return (pairs_.containsKey(key)) ? Double.valueOf(pairs_.get(key)) : def;
			} 
			catch (NumberFormatException e)
			{
				return def;
			}
		}
		
}
