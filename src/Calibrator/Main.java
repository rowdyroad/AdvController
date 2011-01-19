package Calibrator;

import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import Streamer.Config;
import Streamer.Loader;
import Common.Args;
import Common.Dbg;
import Common.Settings;
import Common.Source;
import Common.SourceParser;

import Common.Source.Channel;

public class Main {

	/**
	 * @param args
	 * @throws LineUnavailableException 
	 */
	
	public static void main(String[] args)  throws Exception 
	{
		// TODO Auto-generated method stub
		
		Common.Config.Arguments = new Args(args);
		
		Dbg.LogLevel = Common.Config.Instance().LogLevel();
			
		AudioFormat format =  new AudioFormat(Config.Instance().SampleRate(),16, Config.Instance().Channels(), true, false);			
		InputStream stream = SourceParser.GetStream(Config.Instance().Source(), format);
		
		if (stream == null)
		{
			throw new Exception(String.format("Incorrect source %s", Config.Instance().Source()));
		}
		
		Source source = new Source(stream,new Settings(format),Common.Config.Instance().BufferCount());
		
		Calibrator calibrator =  ( Common.Config.Instance().GetProperty("t","volume").equals("volume")) 
																? new VolumeCalibrator(source)
																: new FrequencyCalibrator(source);
																
		source.RegisterAudioReceiver((Common.Config.Instance().GetProperty("c","left").equals("left")) 
																				? Channel.LEFT_CHANNEL 
																				: Channel.RIGHT_CHANNEL, calibrator);
		
		calibrator.Process();
	}

}
