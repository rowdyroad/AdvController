package Capturer;
import java.io.File;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import Common.Config;
import Common.FingerPrint;
import Common.Frequency;
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
	
	public Capturer(String filename, String id) throws Exception
	{
		AudioInputStream stream = AudioSystem.getAudioInputStream(new File(filename));
		settings_ = new Settings(stream.getFormat());
		
		source_ = new Source(stream, settings_);
		source_.RegisterAudioReceiver(Channel.LEFT_CHANNEL, new Frequencier(this,settings_));																					
		fp_ = new FingerPrint(id,stream.getFrameLength(), settings_.WindowSize(), Common.Config.Instance().LevelsCount());		
		Utils.Dbg("FrameLength:%d",stream.getFrameLength());
	}

	public FingerPrint Process()
	{
		source_.Process();
		Utils.Dbg("Process is over");
		fp_.ThinOut();	
		Utils.Dbg(fp_);
		return fp_;
	}

	private LinkedList<Frequency>  frequency_ = new  LinkedList<Frequency>();
	
	@Override
	public boolean OnReceived(List<Frequency> frequency, long timeoffset) 
	{
		if (frequency != null)
		{
			Frequency.Merge(frequency_, frequency);
		}
		
		if (time_ % settings_.WindowSize() == 0)
		{
			Utils.DbgFrq(frequency_);
			fp_.Add(frequency_);
			 frequency_ = new  LinkedList<Frequency>();
		}
		
		time_ += timeoffset;
		return true;
	}

	@Override
	public void OnError() {

	}

}
