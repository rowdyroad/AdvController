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
		AudioFormat format =  new AudioFormat(Config.Instance().SampleRate(),16, Config.Instance().Channels(), true, false);
		settings_ = new Settings(format);
		loader_ = new Loader(Config.Instance().PromosPath());

		InputStream stream = SourceParser.GetStream(Config.Instance().Source(), format);

		if (!Config.Instance().IgnoreEmptyStream() && stream.available() == 0)
		{
			throw new Exception("Nothing to read at the start of streamer");
		}

		if (stream ==null)
		{
			throw new Exception(String.format("Incorrect source %s", Config.Instance().Source()));
		}

		source_ = new Source(stream, settings_, Common.Config.Instance().BufferCount());			

	}

	public void Process()
	{
		if (! Config.Instance().LeftKey().isEmpty())
		{
			Dbg.Info("Add left channel [%s]", Config.Instance().LeftKey());
			Summator sm = new Summator(settings_,new ResultSubmiter(Config.Instance().LeftKey()));		
			loader_.AddProcessor(Config.Instance().LeftKey(), sm);
			source_.RegisterAudioReceiver(Channel.LEFT_CHANNEL, new Frequencier(sm, settings_,settings_.WindowSize() / Config.Instance().OverlappedCoef(),Config.Instance().LeftMinFrequency(),Config.Instance().LeftMaxFrequency()));
		}
		
		if (Config.Instance().Channels() > 1 && ! Config.Instance().RightKey().isEmpty())
		{
			Dbg.Info("Add right channel [	%s]", Config.Instance().RightKey());
			Summator sm = new Summator(settings_,new ResultSubmiter(Config.Instance().RightKey()));
			loader_.AddProcessor(Config.Instance().RightKey(), sm);
			source_.RegisterAudioReceiver(Channel.RIGHT_CHANNEL, new Frequencier(sm, settings_,settings_.WindowSize() / Config.Instance().OverlappedCoef(), Config.Instance().RightMinFrequency(),Config.Instance().RightMaxFrequency()));
		}		

		Dbg.Info("Listening...");		
		loader_.Process();
		source_.Process();

	}
}