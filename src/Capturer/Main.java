package Capturer;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import Common.Source;
import Common.Utils;

public class Main {

	public static void main (String args [])  {

		if (args.length < 2)
		{
			Utils.Dbg("Usage: %s [<config_file>] <wav_file> <promo_id>", Main.class.getName());
			System.exit(1);
		}
		
	
		int begin = 0;
		
		if (args.length %  2 != 0)
		{
			begin = 1;
			Common.Config.Filename = args[0];
		}

		try
		{
			Utils.Dbg("Files to convert: %d", args.length / 2);
			
			for (int i = begin; i < args.length; i+=2)
			{
				String wav_file = args[i];
				String promo_id = args[i+1];
				Utils.Dbg("Filename:%s PromoID:%s", wav_file, promo_id);	
				Capturer capt = new Capturer(wav_file,  promo_id);
				capt.Process().Serialize(Config.Instance().Storage()+promo_id);
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