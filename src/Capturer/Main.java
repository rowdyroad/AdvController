package Capturer;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import Common.Args;
import Common.Source;
import Common.Utils;

public class Main {

	private static void usage()
	{
		Utils.Dbg("Usage: %s\n\t-c <config_file>\n\t-f <min_frequency>\n\t-F <max_frequency> \n\t-p <wav_file>,<result_file>,<promo_id>,...\n-b <buffer_count>", Main.class.getName());
		System.exit(1);		
	}
	
	public static void main (String a [])  {

		if (a.length  == 0)
		{
			usage();
		}
		
		Args args = new Args(a);
		int min_freq = args.GetInt("f",-1);
		int max_freq = args.GetInt("F",-1);
		int buffer_count = args.GetInt("b", 100);
		String p = args.Get("p","");
		if (min_freq == -1 || max_freq == -1 || p.isEmpty())
		{
			usage();
		}
	
		String[]  promos = p.split(",");
		if (promos.length % 3 != 0)
		{
			usage();
		}
		
		try
		{
			Utils.Dbg("Min Frequency: %d  MaxFrequency: %d",min_freq,max_freq);
			Utils.Dbg("Buffer Count: %d",buffer_count);
			Utils.Dbg("Files to convert: %d", promos.length / 3);
			for (int i = 0; i < promos.length; i+=3)
			{
				long time = System.currentTimeMillis();
				String wav_file = promos[i];
				String result_file = promos[i+1];
				String promo_id = promos[i+2];
				Utils.Dbg("Filename: %s\nResult file:%s\nPromoID: %s", wav_file, result_file, promo_id);	
				Capturer capt = new Capturer(wav_file,  promo_id, min_freq, max_freq,buffer_count);
				capt.Process().Serialize(result_file);
				Utils.Dbg("Time to work: %d ms", System.currentTimeMillis() - time);
			}
		}
		catch (Source.SourceException e)
		{
			Utils.Dbg("Incorrect file format: %s\n",e);
			System.exit(2);
		}
		catch (IOException e)
		{
			Utils.Dbg("Couldn't open wav_file: %s\n", e.getMessage()  + e.getStackTrace());
			System.exit(3);
		}
		catch (UnsupportedAudioFileException e)
		{
			Utils.Dbg("Incorrect file format:  %s\n", e.getMessage());
			System.exit(4);
		}
		catch (Exception e)
		{
			Utils.Dbg("Undefined exception: %s\n",e.getMessage());
			e.printStackTrace();
			System.exit(5);
		}
	}
}