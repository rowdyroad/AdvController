package Capturer;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.sound.sampled.UnsupportedAudioFileException;

import Common.Args;
import Common.Dbg;
import Common.Source;
import Common.Utils;

public class Main {

	private static void usage()
	{
		Dbg.Info("Usage: %s\n-c <config_file>\n-C <channel>\n-f <min_frequency>\n-F <max_frequency> \n-p <wav_file>,<result_file>,<promo_id>,...\n-b <buffer_count>", Main.class.getName());
		System.exit(1);		
	}
	
	
	public static void main (String  args[])  {

		if (args.length  == 0)
		{
			usage();
		}		
		Args.Init(args);
		final String config  = Args.Instance().Get("c", "config.ini");
		if (!config.isEmpty())			
			Common.Config.Init(config, "capturer");		
		Dbg.LogLevel = Common.Config.Instance().LogLevel();		
		final String promo = Args.Instance().Get("p", "");
		final int min_frequency = Args.Instance().Get("f", 20);
		final int max_frequency = Args.Instance().Get("F", 20000);
		final String channel  = Args.Instance().Get("C","left");
		final int kg = Args.Instance().Get("k",Integer.MIN_VALUE);		
		final float kill_gate =  (kg == Integer.MIN_VALUE) ? Float.NEGATIVE_INFINITY :  (float)Math.pow(10,  kg / 20 );  
		
		if (promo.isEmpty())
		{
			usage();
		}
	
		String[]  promos = promo.split(",");		
		if (promos.length == 2)
		{
			File src_dir = new File(promos[0]);
			File dst_dir = new File(promos[1]);
			
			if (!src_dir.isDirectory() || !dst_dir.isDirectory())
			{
				usage();
			}
			
			Map<String,String> map = new TreeMap<String,String>();
			
			String[] src_files = src_dir.list();			
			for (String src_file : src_files)
			{
				if (! src_file.endsWith(".wav")) continue;				
				map.put(src_file.substring(0, src_file.length() - 4), src_file);				
			}
			
			promos = new String[map.size() * 3];
			int i = 0;
			for (Entry<String,String> kvp : map.entrySet())
			{
				promos[i] = src_dir.getPath()+File.separator + kvp.getValue();
				promos[i+1] = dst_dir.getPath() + File.separator + kvp.getKey();
				promos[i+2] = kvp.getKey();
				i+=3;
			}
		}
	
		if (promos.length % 3 != 0)
		{
			usage();
		}
		
		try
		{
			Dbg.Info("Min Frequency: %d\nMaxFrequency: %d\nBuffer Count: %d\nFiles to convert: %d",
								min_frequency,
								max_frequency,
								Common.Config.Instance().BufferCount(),
								promos.length / 3);
			
			for (int i = 0; i < promos.length; i+=3)
			{
				long time = System.currentTimeMillis();
				String wav_file = promos[i];
				String result_file = promos[i+1];
				String promo_id = promos[i+2];
				Dbg.Info("Filename: %s\nResult file:%s\nPromoID: %s", wav_file, result_file, promo_id);	
				Capturer capt = new Capturer(wav_file,  promo_id, channel, min_frequency, max_frequency, kill_gate, Common.Config.Instance().BufferCount());
				capt.Process().Serialize(result_file);
				Dbg.Info("Time to work: %d ms", System.currentTimeMillis() - time);
			}
			
			System.exit(0);
		}
		catch (Source.SourceException e)
		{
			Dbg.Error("Incorrect file format: %s\n",e);
			System.exit(2);
		}
		catch (IOException e)
		{
			Dbg.Error("Couldn't open wav_file: %s\n", e.getMessage()  + e.getStackTrace());
			System.exit(3);
		}
		catch (UnsupportedAudioFileException e)
		{
			Dbg.Error("Incorrect file format:  %s\n", e.getMessage());
			System.exit(4);
		}
		catch (Exception e)
		{
			Dbg.Error("Undefined exception: %s\n",e.getMessage());
			e.printStackTrace();
			System.exit(5);
		}
	}
} 
