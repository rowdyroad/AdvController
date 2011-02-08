package Common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SourceParser 
{
	AudioFormat format_;
	String source_;
	InputStream stream_;
	
	public SourceParser(String source, AudioFormat format)
	{
		source_ = source;
		format_ = format;
		stream_ = getStream(source_,format_);
	}
	
	public SourceParser(String source)
	{
		source_ = source;
		format_ = new AudioFormat(44100, 16,2,true,false);
		stream_ = getStream(source_,format_);
	}
	
	public InputStream Stream()
	{
		return stream_;
	}
	public AudioFormat Format()
	{
		return format_;
	}
	
	public String Source()
	{
		return source_;
	}
	
	private  InputStream getStream(String source, AudioFormat format)
	{
		
		if (source.startsWith("stdin://"))
		{
			String[] d =  source.substring(8).split("\\.");
			final float sample_rate = Float.parseFloat(d[0]);
			final int channels  = Integer.parseInt(d[1]);
			final int bits_per_sample =16;// Integer.parseInt(d[2]);		
			final boolean signed = true;
			final boolean be = false; 			
			
			format = new AudioFormat(sample_rate, bits_per_sample,channels,signed,be);			
			return System.in;
		}
		
		if (source.startsWith("soundcard://"))
		{
			String[] d =  source.substring(12).split("\\.");
			final int sample_rate = Integer.parseInt(d[1]);
			final int channels  = Integer.parseInt(d[2]);
			final int bits_per_sample =16;// Integer.parseInt(d[2]);			
			final boolean signed = true;
			final boolean be = false;
			
			format = new AudioFormat(sample_rate, bits_per_sample,channels,signed,be);			
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
			Dbg.Info("Loading from %s", file.getPath());
			if (! file.exists())
			{
				Dbg.Error("Source file not found");
				return null;
			}
			try {			
				AudioInputStream st =  AudioSystem.getAudioInputStream(file);
				format_ = st.getFormat();
				return st;
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
