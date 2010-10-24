package Capturer;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

public class Main {

	public static void main (String args []) throws Exception {
		
		if (args.length < 2)
		{
			System.out.printf("Usage: %s <wav_file> <promo_id>", Main.class.getName());
			System.exit(1);
		}

		try
		{
			Capturer capt = new Capturer(args[0], Integer.decode(args[1]), null);
			capt.Process();
		}
		catch (NumberFormatException e)
		{
			System.err.printf("Incorrect promo_id: %s\n", e.getMessage());
			System.exit(2);
		}
		catch (IOException e)
		{
			System.err.printf("Couldn't open wav_file: %s\n", e.getMessage());
			System.exit(3);
		}
		catch (UnsupportedAudioFileException e)
		{
			System.err.printf("Incorrect file format:  %s\n", e.getMessage());
			System.exit(4);
		}
		
		
	}
}