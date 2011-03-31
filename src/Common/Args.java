package Common;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Args {

		private static Map<String,String> pairs_ = new TreeMap<String,String>();
		private List<String> items_ = new LinkedList<String>();
		static private String[] args_ = null;
		static private Args instance_ = null;
		static private Map<Character,String> s_pairs_ = new TreeMap<Character,String>();
		static private Map<String,String> l_pairs_ = new TreeMap<String,String>();
		static private List<String> idx_items_ = new LinkedList<String>();
		
		public static void Init(String[] args)
		{
					Args.args_ = args;
					for (int i = 0; i < args.length;++i)
					{
						if (args[i].startsWith("--") && args[i].length() > 3 && i+1 < args.length)
						{
							l_pairs_.put(args[i].substring(2),args[i+1]);
							++i;
							continue;
						}
						
						if (args[i].startsWith("-") && args[i].length() == 2 && i + 1 < args.length)
						{
							s_pairs_.put(args[i].charAt(1),args[i+1]);
							++i;
							continue;
						}						
						idx_items_.add(args[i]);
					}
		}

		private static String get(Character sname, String lname)
		{
			String res = s_pairs_.get(sname);
			if (res != null) return res;			
			res = l_pairs_.get(lname);
			if (res != null) return res;
			return null;
		}
		
		public static Integer Get(Character sname, String lname, Integer def)
		{			
			String res = get(sname,lname);			
			try
			{
				return (res == null) ? def : Integer.valueOf(res);
			}
			catch (NumberFormatException e)
			{
				return null;
			}
		}
		
		public static String  Get(char sname,String lname,  String def)
		{
			String res = get(sname,lname);
			return (res == null) ? def : res;			
		}
		
		public static Double Get(char sname, String lname, Double def)
		{
			String res = get(sname,lname);			
			try
			{
				return (res == null) ? def : Double.valueOf(res);
			}
			catch (NumberFormatException e)
			{
				return null;
			}
		}		
		
		public static LinkedList<String> IndexItems()
		{
			return (LinkedList<String>) idx_items_;
		}
		public static Args Instance()
		{
			if (instance_ == null)
			{
				if (args_ == null)
					throw new IllegalArgumentException("Undefined arguments. Please init before");
				
				instance_ = new Args(args_);
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
