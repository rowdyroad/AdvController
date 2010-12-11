package Streamer;


import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

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
	

	
	private Map<String, LinkedList<FingerPrint>>  fingerPrints_ = new TreeMap<String, LinkedList<FingerPrint>>();	
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
	
	public void AddFingerPrint(String key, FingerPrint fp)
	{
		if (!fingerPrints_.containsKey(key))
		{
			fingerPrints_.put(key, new LinkedList<FingerPrint>());
		}
		fingerPrints_.get(key).add(fp);
	}
	
	public int Count()
	{
		int size = 0;
		
		for (Entry<String, LinkedList<FingerPrint>> kvp: fingerPrints_.entrySet())
		{
			size+=kvp.getValue().size();
		}
		return size;
	}
	
	public void Process()
	{
		String left_key = Common.Config.Instance().GetProperty("left_key", "");
		String right_key = Common.Config.Instance().GetProperty("right_key", "");
		
		
		LinkedList<FingerPrint> fingerPrints = fingerPrints_.get(left_key);
		if (fingerPrints != null)
		{
			Utils.Dbg("Process left channel [%s]", left_key);
			Summator sm = new Summator(settings_,new ResultSubmiter(left_key));
			for (FingerPrint fp: fingerPrints)
			{
				sm.AddFingerPrint(fp);
			}
			source_.RegisterAudioReceiver(Channel.LEFT_CHANNEL, new Frequencier(sm, settings_,4096));
		}
		
		 fingerPrints = fingerPrints_.get(right_key);
		if (fingerPrints != null)
		{
			Utils.Dbg("Process right channel [%s]", right_key);
			Summator sm = new Summator(settings_,new ResultSubmiter(right_key));
			for (FingerPrint fp: fingerPrints)
			{
				sm.AddFingerPrint(fp);
			}
			source_.RegisterAudioReceiver(Channel.RIGHT_CHANNEL, new Frequencier(sm, settings_,4096));
		}		
		
		Utils.Dbg("listening...");		
		source_.Process();
	}
}