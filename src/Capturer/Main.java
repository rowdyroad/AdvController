package Capturer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteOrder;

import javax.sound.sampled.UnsupportedAudioFileException;

import Common.Utils;

public class Main {

	public static void main (String args []) throws Exception {

	
		if (args.length < 2)
		{
			System.out.printf("Usage: %s <wav_file> <promo_id>", Main.class.getName());
			System.exit(1);
		}

		try
		{
			Capturer capt = new Capturer(args[0],  args[1]);
			capt.Process().Serialize(args[1]);			 
		}
		catch (IOException e)
		{
			System.err.printf("Couldn't open wav_file: %s\n", e.getMessage()  + e.getStackTrace());
			
			for (int i=0;i< e.getStackTrace().length; ++i)
			{
				Utils.Dbg(e.getStackTrace()[i]);
			}
			System.exit(3);
		}
		catch (UnsupportedAudioFileException e)
		{
			System.err.printf("Incorrect file format:  %s\n", e.getMessage());
			System.exit(4);
		}
		
		
	}
}