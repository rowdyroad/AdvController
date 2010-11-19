package Capturer;
import java.io.File;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
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
import Common.FingerPrint.Period;
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
	}

	public FingerPrint Process()
	{
		while (source_.Read()) {
			Utils.Dbg("Process: %d%% completed", time_ * 100 / fp_.Time());
		};
		Utils.Dbg("Process is over");
		fp_.ThinOut();	
		Utils.Dbg(fp_);
		return fp_;
	}

	private LinkedList<Period> periods_ = new  LinkedList<Period>();
	
	@Override
	public boolean OnReceived(Common.Frequency[] frequency, long timeoffset) 
	{
		
		Utils.Dbg("timeoffset:%d", time_);
		if (frequency != null)
		{
			for (int i = 0; i < frequency.length; ++i)
			{
			
				Period p = new Period(frequency[i], time_);
				boolean found = false;
				for (Period p1: periods_)
				{
					if (p1.frequency.frequency.compareTo(p.frequency.frequency) == 0)
					{
						p1.frequency.level = Math.max(p.frequency.level, p1.frequency.level);
						found = true;
						break;
					}				
				}			
				if (! found)
				{
					periods_.add(p);
				}
			}
		}
		
		if (time_ % settings_.WindowSize() == 0)
		{
			
			Collections.sort(periods_, new Comparator<Period>() {
				@Override
				public int compare(Period arg0, Period arg1) {
					return - arg0.frequency.level.compareTo(arg1.frequency.level);
				}});
						
			while (periods_.size() > Common.Config.Instance().LevelsCount())
			{
				periods_.removeLast();
			}
			fp_.Add(periods_);
			periods_ = new  LinkedList<Period>();
		}
		
		time_ += timeoffset;
		return true;
	}

	@Override
	public void OnError() {

	}

}
