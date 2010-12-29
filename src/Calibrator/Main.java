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
import Common.Settings;
import Common.Source;
import Common.Source.Channel;

public class Main {

	/**
	 * @param args
	 * @throws LineUnavailableException 
	 */
	
	public static void main(String[] args) throws LineUnavailableException {
		// TODO Auto-generated method stub
		
		Args  arguments = new Args(args);
			
		AudioFormat format =  new AudioFormat(Config.Instance().SampleRate(),16,2, true, false);			
		InputStream stream;
		if (arguments.Get("s","stdin") == "stdin")
		{
			stream = System.in;
		}
		else
		{
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			TargetDataLine line = (TargetDataLine)AudioSystem.getLine(info);
			line.open(format);
			line.start();
			stream = new AudioInputStream(line);
		}
		
		Source source = new Source(stream,new Settings(format));
		
		Calibrator calibrator =  ( arguments.Get("t","volume").equals("volume")) 
																? new VolumeCalibrator(source)
																: new FrequencyCalibrator(source);
																
		source.RegisterAudioReceiver((arguments.Get("c","left").equals("left")) 
																				? Channel.LEFT_CHANNEL 
																				: Channel.RIGHT_CHANNEL, calibrator);
		
		calibrator.Process();
	}

}
