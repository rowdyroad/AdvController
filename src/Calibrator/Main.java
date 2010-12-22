package Calibrator;

import java.io.InputStream;

import javax.sound.sampled.AudioFormat;

import Streamer.Config;
import Common.Args;
import Common.Settings;
import Common.Source;
import Common.Utils;
import Common.Source.Channel;

public class Main {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Args  arguments = new Args(args);
			
		AudioFormat format =  new AudioFormat(Config.Instance().SampleRate(),16,2, true, false);			
		Source source = new Source(System.in,new Settings(format));
		
		Calibrator calibrator =  ( arguments.Get("t","volume").equals("volume")) 
																? new VolumeCalibrator(source)
																: new FrequencyCalibrator(source);
																
		source.RegisterAudioReceiver((arguments.Get("c","left").equals("left")) 
																				? Channel.RIGHT_CHANNEL 
																				: Channel.RIGHT_CHANNEL, calibrator);
		
		calibrator.Process();
	}

}
