package Streamer;


import java.io.InputStream;

import javax.sound.sampled.AudioFormat;

import Common.Dbg;
import Common.Frequencier;
import Common.Settings;
import Common.Source;
import Common.Source.Channel;
import Common.SourceParser;
import Common.Utils;


public class Streamer
{
	private Source source_;
	private Settings settings_;
	private Loader loader_;

	public Streamer() throws Exception
	{
	
		settings_ = new Settings(Config.Instance().Source().Format());
		loader_ = new Loader(Config.Instance().Storage());
		InputStream stream =Config.Instance().Source().Stream();
		
		if (stream ==null)
		{
			throw new Exception(String.format("Incorrect source %s", Config.Instance().Source().Source()));
		}
		
		if (!Config.Instance().IgnoreEmptyStream() && stream.available() == 0)
		{
			throw new Exception("Nothing to read at the start of streamer");
		}

		

		source_ = new Source(stream, settings_, Common.Config.Instance().BufferCount());			

	}

	public void Process()
	{
		if (Config.Instance().LeftChannel() != null)
		{
			Dbg.Info("Add left channel [%s]",Config.Instance().LeftChannel().Key());
			Summator sm = new Summator(settings_,new ResultSubmiter(Config.Instance().LeftChannel().Key()));		
			loader_.AddProcessor(Config.Instance().LeftChannel().Key(), sm);
			source_.RegisterAudioReceiver(Channel.LEFT_CHANNEL, new Frequencier(sm, settings_,Math.round (settings_.WindowSize() * Config.Instance().OverlappedCoef()),Config.Instance().LeftChannel().MinFrequency(),Config.Instance().LeftChannel().MaxFrequency()));
		}
		
		if (Config.Instance().RightChannel() != null)
		{
			Dbg.Info("Add right channel [%s]",Config.Instance().RightChannel().Key());
			Summator sm = new Summator(settings_,new ResultSubmiter(Config.Instance().RightChannel().Key()));		
			loader_.AddProcessor(Config.Instance().RightChannel().Key(), sm);
			source_.RegisterAudioReceiver(Channel.LEFT_CHANNEL, new Frequencier(sm, settings_,Math.round (settings_.WindowSize() * Config.Instance().OverlappedCoef()),Config.Instance().RightChannel().MinFrequency(),Config.Instance().RightChannel().MaxFrequency()));
		}

		Dbg.Info("Listening...");		
		loader_.Process();
		source_.Process();

	}
}