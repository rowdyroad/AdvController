package Common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SourceParser {
	public static InputStream GetStream(String source, AudioFormat format)
	{
		
		if (source.equals("stdin"))
		{
			return System.in;
		}		
		
		if (source.equals("soundcard"))
		{
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			TargetDataLine line;
			try {
				line = (TargetDataLine)AudioSystem.getLine(info);
			} catch (LineUnavailableException e) {
				e.printStackTrace();
				return null;
			}
			
			try {
				line.open(format);
			} catch (LineUnavailableException e) {
				e.printStackTrace();
				return null;
			}
			line.start();
			return new AudioInputStream(line);
		}

		if (source.startsWith("file://"))
		{
			File file = new File(source.substring(7));
			if (! file.exists())
			{
				Dbg.Error("Source file not found");
				return null;
			}
			try {
				return AudioSystem.getAudioInputStream(file);
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		if (source.startsWith("program://"))
		{
				Process p;
				try {
					p = Runtime.getRuntime().exec(source.substring(10));
					return  p.getInputStream();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
		}
		return null;
	}
}
