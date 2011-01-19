package Capturer;
import java.io.File;
import java.util.Vector;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import Common.Dbg;
import Common.FingerPrint;
import Common.Settings;
import Common.Source;
import Common.Frequencier;
import Common.Utils;
import Common.Source.Channel;

public class Capturer implements Frequencier.Catcher
{
	private long time_ = 0;
	Source source_ = null;
	FingerPrint fp_;
	private Settings settings_;
	
	public Capturer(String filename, String id, int min_frequency, int max_frequency, int buffer_count) throws Exception
	{
		AudioInputStream stream = AudioSystem.getAudioInputStream(new File(filename));
		settings_ = new Settings(stream.getFormat());
		source_ = new Source(stream, settings_,buffer_count);
		if (Config.Instance().Channel() == "right")
		{
			source_.RegisterAudioReceiver(Channel.RIGHT_CHANNEL, new Frequencier(this,settings_,settings_.WindowSize(), min_frequency, max_frequency));
		}
		else
		{
			source_.RegisterAudioReceiver(Channel.LEFT_CHANNEL, new Frequencier(this,settings_,settings_.WindowSize(),min_frequency, max_frequency));		
		}
		
		fp_ = new FingerPrint(id);		
		Dbg.Info("FrameLength: %d",stream.getFrameLength());
	}

	public FingerPrint Process()
	{
		Dbg.Info("Working...");
		source_.Process();
		Dbg.Info("The job is done");
		fp_.ThinOut();	
		Dbg.Info(fp_);
		return fp_;
	}

	Vector<double[]> list = new Vector<double[]>();
	@Override
	public boolean OnReceived(float[][] frequency, long timeoffset) 
	{
		fp_.Add(frequency, time_);
		time_+=timeoffset;
		return true;
	}

	@Override
	public void OnError() {

	}
	
	public void OnIgnore(long timeoffset)
	{
		
	}

}

