package Capturer;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import Common.Args;
import Common.Dbg;
import Common.Source;
import Common.Utils;

public class Main {

	private static void usage()
	{
		Dbg.Info("Usage: %s\n-c <config_file>\n-f <min_frequency>\n-F <max_frequency> \n-p <wav_file>,<result_file>,<promo_id>,...\n-b <buffer_count>", Main.class.getName());
		System.exit(1);		
	}
	
	public static void main (String  args[])  {

		if (args.length  == 0)
		{
			usage();
		}
		
		Common.Config.Arguments = new Args(args);
		Dbg.LogLevel = Common.Config.Instance().LogLevel();
		
		if (Config.Instance().Promos().isEmpty())
		{
			usage();
		}
	
		String[]  promos = Config.Instance().Promos().split(",");
		if (promos.length % 3 != 0)
		{
			usage();
		}
		
		try
		{
			Dbg.Info("Min Frequency: %d\nMaxFrequency: %d\nBuffer Count: %d\nFiles to convert: %d",
								Config.Instance().MinFrequency(),
								Config.Instance().MaxFrequency(),
								Common.Config.Instance().BufferCount(),
								promos.length / 3);
			
			for (int i = 0; i < promos.length; i+=3)
			{
				long time = System.currentTimeMillis();
				String wav_file = promos[i];
				String result_file = promos[i+1];
				String promo_id = promos[i+2];
				Dbg.Info("Filename: %s\nResult file:%s\nPromoID: %s", wav_file, result_file, promo_id);	
				Capturer capt = new Capturer(wav_file,  promo_id, Config.Instance().MinFrequency(), Config.Instance().MaxFrequency(), Common.Config.Instance().BufferCount());
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