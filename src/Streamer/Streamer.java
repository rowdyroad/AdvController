package Streamer;


import java.io.InputStream;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

import Common.FingerPrint;
import Common.Frequencier;
import Common.Source;
import Common.Source.Channel;
import Common.Utils;

public class Streamer
{
	private Vector<FingerPrint> fingerPrints_ = new Vector<FingerPrint>();	
	Source source_;
	public Streamer() throws Exception
	{
		try
		{
			AudioFormat format =  new AudioFormat(Common.Config.Instance().SampleRate(),16,2, true, false);
			InputStream stream = null;
			if (Config.Instance().Source() == "soundcard")
			{
				DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
				TargetDataLine line = (TargetDataLine)AudioSystem.getLine(info);
				line.open(format);
				line.start();
				stream = new AudioInputStream(line);
			}
			else
			{
				stream = System.in;
				Utils.Dbg("stdin");
				Utils.Dbg(stream);
			}
			if (stream ==null)
			{
				throw new Exception(String.format("Incorrect source %s", Config.Instance().Source()));
			}
			source_ = new Source(stream, format.getChannels(), format.getSampleSizeInBits(), format.isBigEndian());			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void AddFingerPrint(FingerPrint fp)
	{
		fingerPrints_.add(fp);
	}
	
	public int Count()
	{
		return fingerPrints_.size();
	}
	
	public void Process()
	{
		source_.RegisterAudioReceiver(Channel.LEFT_CHANNEL,new Frequencier(new Comparer(fingerPrints_, new ResultSubmiter())));
	//	source_.RegisterAudioReceiver(Channel.RIGHT_CHANNEL,new Frequencier(new Comparer(fingerPrints_, new ResultSubmiter())));
		Utils.Dbg("listening...");
		while (source_.Read())
		{
			
		}
	}
}