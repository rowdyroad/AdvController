package Capturer;
import java.io.File;
import java.util.Vector;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import Common.Chunker;
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
	
	public Capturer(String filename, String id, String channel, int min_frequency, int max_frequency, float kill_gate,  int buffer_count) throws Exception
	{
		AudioInputStream stream = AudioSystem.getAudioInputStream(new File(filename));
		settings_ = new Settings(stream.getFormat());
		source_ = new Source(stream, settings_,buffer_count, kill_gate,kill_gate);		
		final int silent_time  = (int) (stream.getFormat().getSampleRate() / 2);
		final int total_time = (int) (stream.getFormat().getSampleRate()  * 60);
		
		if (channel == "right")
		{
			source_.RegisterAudioReceiver(Channel.RIGHT_CHANNEL, new Chunker(silent_time, total_time, kill_gate, new Frequencier(this,settings_,settings_.WindowSize(), min_frequency, max_frequency)));
		}
		else
		{
			source_.RegisterAudioReceiver(Channel.LEFT_CHANNEL,  new Chunker(silent_time, total_time,kill_gate,new Frequencier(this,settings_,settings_.WindowSize(),min_frequency, max_frequency)));		
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
		if (frequency!= null)
		{
			fp_.Add(frequency, time_);
			time_+=timeoffset;
		}
		return true;
	}

	@Override
	public void OnError() {

	}
	
	public void OnIgnore(long timeoffset)
	{
		
	}

}

