package Capturer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteOrder;

import javax.sound.sampled.UnsupportedAudioFileException;

import Common.Source;
import Common.Utils;

public class Main {

	public static void main (String args []) throws Exception {

	
		if (args.length < 2)
		{
			System.out.printf("Usage: %s <wav_file> <promo_id>", Main.class.getName());
			System.exit(1);
		}
		
		String wav_file, promo_id;
		
		if (args.length == 2)
		{
			wav_file = args[0];
			promo_id = args[1];
		}
		else
		{
			Common.Config.Filename = args[0];
			wav_file = args[1];
			promo_id = args[2];
		}

		try
		{
			Capturer capt = new Capturer(wav_file,  promo_id);
			capt.Process().Serialize(Config.Instance().Storage()+promo_id);			 
		}
		
		catch (Source.SourceException e)
		{
			System.out.printf("Incorrect file format: %s\n",e);
			System.exit(2);
		}
		catch (IOException e)
		{
			System.out.printf("Couldn't open wav_file: %s\n", e.getMessage()  + e.getStackTrace());
			System.exit(3);
		}
		catch (UnsupportedAudioFileException e)
		{
			System.out.printf("Incorrect file format:  %s\n", e.getMessage());
			System.exit(4);
		}
		catch (Exception e)
		{
			System.out.printf("Undefined exception: %s\n",e.getMessage());
			System.exit(5);
		}
		
		
	}
}