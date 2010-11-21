package Streamer;


import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

import Common.FingerPrint;
import Common.Frequencier;
import Common.Settings;
import Common.Source;
import Common.Source.Channel;
import Common.Utils;


public class Streamer
{
	private List<FingerPrint> fingerPrints_ = new LinkedList<FingerPrint>();	
	private Source source_;
	private Settings settings_;
	
	public Streamer() throws Exception
	{
		AudioFormat format =  new AudioFormat(Config.Instance().SampleRate(),16,2, true, false);
		settings_ = new Settings(format);
		try
		{	
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
			}
			
			if (stream ==null)
			{
				throw new Exception(String.format("Incorrect source %s", Config.Instance().Source()));
			}
			
			source_ = new Source(stream, settings_);			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void AddFingerPrint(FingerPrint fp)
	{
		Utils.Dbg(fp);
		fingerPrints_.add(fp);
	}
	
	public int Count()
	{
		return fingerPrints_.size();
	}
	
	public void Process()
	{
		Summator sm = new Summator(settings_);
		Resulter resulter = new ResultSubmiter();
		for (FingerPrint fp: fingerPrints_)
		{
			sm.AddComparer(new Comparer(fp, settings_,resulter));
		}
		source_.RegisterAudioReceiver(Channel.LEFT_CHANNEL, new Frequencier(sm, settings_));
		Utils.Dbg("listening...");		
		source_.Process();
	}
}