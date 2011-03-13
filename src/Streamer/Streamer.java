package Streamer;


import java.io.InputStream;

import javax.sound.sampled.AudioFormat;

import Common.Chunker;
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
		source_ = new Source(stream, settings_, Common.Config.Instance().BufferCount(), Config.Instance().LeftChannel().KillGate(),Config.Instance().RightChannel().KillGate());			
	}

	public void Process()
	{
		final int silent_time  = (int) (settings_.SampleRate() / 2);
		final int total_time = (int) (settings_.SampleRate() * 60);
		if (Config.Instance().LeftChannel().IsExists())
		{
			Dbg.Info("Add left channel [%s]",Config.Instance().LeftChannel().Id());
			Summator sm = new Summator(settings_,new ResultSubmiter(Config.Instance().LeftChannel().Id()));		
			loader_.AddProcessor(Config.Instance().LeftChannel().Id(), sm);
			source_.RegisterAudioReceiver(Channel.LEFT_CHANNEL, new Chunker(silent_time, total_time, Config.Instance().LeftChannel().KillGate(),new Frequencier(sm, settings_,Math.round (settings_.WindowSize() * Config.Instance().OverlappedCoef()),Config.Instance().LeftChannel().MinFrequency(),Config.Instance().LeftChannel().MaxFrequency())));
		}
		
		if (Config.Instance().RightChannel().IsExists())
		{
			Dbg.Info("Add right channel [%s]",Config.Instance().RightChannel().Id());
			Summator sm = new Summator(settings_,new ResultSubmiter(Config.Instance().RightChannel().Id()));		
			loader_.AddProcessor(Config.Instance().RightChannel().Id(), sm);
			source_.RegisterAudioReceiver(Channel.RIGHT_CHANNEL, new Chunker(silent_time, total_time,  Config.Instance().RightChannel().KillGate(),new Frequencier(sm, settings_, Math.round (settings_.WindowSize() * Config.Instance().OverlappedCoef()),Config.Instance().RightChannel().MinFrequency(), Config.Instance().RightChannel().MaxFrequency())));
		}

		Dbg.Info("Listening...");		
		loader_.Process();
		source_.Process();

	}
}