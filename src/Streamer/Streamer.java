package Streamer;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

import Common.Frequencier;
import Common.Settings;
import Common.Source;
import Common.Source.Channel;
import Common.Utils;


public class Streamer
{
	private Source source_;
	private Settings settings_;
	private Loader loader_;
	
	public Streamer() throws Exception
	{
		AudioFormat format =  new AudioFormat(Config.Instance().SampleRate(),16,2, true, false);
		settings_ = new Settings(format);
		loader_ = new Loader(Config.Instance().PromosPath());
		
		try
		{	
			InputStream stream = null;
			if (Config.Instance().Source().equals("soundcard"))
			{
				DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
				TargetDataLine line = (TargetDataLine)AudioSystem.getLine(info);
				line.open(format);
				line.start();
				stream = new AudioInputStream(line);
			}
			else
			if (Config.Instance().Source().startsWith("file://"))
			{
				File file = new File(Config.Instance().Source().substring(7));
				if (! file.exists())
				{
					Utils.Dbg("Source file not found");
				}
				else
				{
					stream = AudioSystem.getAudioInputStream(file);
				}
			}
			else
			if (Config.Instance().Source().equals("stdin"))
			{
				stream = System.in;
			}
			else
			if (Config.Instance().Source().startsWith("program://"))
			{
					Process p = Runtime.getRuntime().exec(Config.Instance().Source().substring(10));
					stream = p.getInputStream();
			};
			
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
	
	public void Process()
	{
		String left_key = Common.Config.Instance().GetProperty("left_key", "");
		String right_key = Common.Config.Instance().GetProperty("right_key", "");
		if (! left_key.isEmpty())
		{
			Utils.Dbg("Add left channel [%s]", left_key);
			Summator sm = new Summator(settings_,new ResultSubmiter(left_key));		
			loader_.AddProcessor(left_key, sm);
			source_.RegisterAudioReceiver(Channel.LEFT_CHANNEL, new Frequencier(sm, settings_,4096,Config.Instance().LeftMinFrequency(),Config.Instance().LeftMaxFrequency()));
		}
		
		if (! right_key.isEmpty())
		{
			Utils.Dbg("Add right channel [%s]", right_key);
			Summator sm = new Summator(settings_,new ResultSubmiter(right_key));
			loader_.AddProcessor(right_key, sm);
			source_.RegisterAudioReceiver(Channel.RIGHT_CHANNEL, new Frequencier(sm, settings_,4096,Config.Instance().RightMinFrequency(),Config.Instance().RightMaxFrequency()));
		}		
		
		Utils.Dbg("Listening...");		
		loader_.Process();
		source_.Process();
		
	}
}