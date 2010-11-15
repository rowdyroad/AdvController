package Capturer;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Vector;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import Common.Config;
import Common.FingerPrint;
import Common.FingerPrint.Period;
import Common.Source;
import Common.Frequencier;
import Common.Utils;
import Common.Source.Channel;


public class Capturer implements Frequencier.Catcher
{
	private long time_ = 0;
	Source source_ = null;
	FingerPrint fp_;
	
	Vector<LinkedList<Period>> vector_;
	
	public Capturer(String filename, String id) throws Exception
	{
		AudioInputStream stream = AudioSystem.getAudioInputStream(new File(filename));
		source_ = new Source(stream, stream.getFormat().getChannels(), stream.getFormat().getSampleSizeInBits(), stream.getFormat().isBigEndian());
		source_.RegisterAudioReceiver(Channel.LEFT_CHANNEL, new Frequencier(this));
		
		Utils.Dbg("SampleRate:%d\nChannels:%d\nBitPerSample:%d\nBigEndian:%b\nFrameLength:%d",
				(int)stream.getFormat().getSampleRate(), stream.getFormat().getChannels(), stream.getFormat().getSampleSizeInBits(), stream.getFormat().isBigEndian(), stream.getFrameLength());
		fp_ = new FingerPrint(id,stream.getFrameLength());		
		int size = (int)Math.ceil((double)stream.getFrameLength() / Config.Instance().WindowSize());
		vector_  = new Vector<LinkedList<Period>>();
		vector_.setSize(size);
	}
	
	public FingerPrint Process()
	{
		while (source_.Read()) {
			Utils.Dbg("Process: %d%% completed", time_ * 100 / fp_.Time());
		};
		addToFingerPrint();
		Utils.Dbg("Process is over");
		/*fp_.ThinOut();	
		Utils.Dbg(fp_);
		Utils.Dbg(fp_.Time());
		Utils.Dbg("Periods:%d",fp_.Count());
		*/
		int c =0;
		for (int i=0;i<vector_.size(); ++i)
		{
			Utils.Dbg("time:%d", i*Common.Config.Instance().WindowSize());
			
			LinkedList<Period> list = vector_.get(i);
			
			Collections.sort(list, new Comparator<Period>() {

				@Override
				public int compare(Period arg0, Period arg1) {
					return - arg0.frequency.level.compareTo(arg1.frequency.level);
				}});		
	
			for (Period p: vector_.get(i))
			{
					Utils.Dbg("\t%d %f",p.frequency.frequency,p.frequency.level);
					++c;
			}
		}
		Utils.Dbg("Count:%d",c);
		fp_.Set(vector_);
		return fp_;
	}
	
	public long Time()
	{
		return time_;
	}
	
	private SortedMap<Integer, Period> counts_ = new TreeMap<Integer,Period>();	
	
	private void addToFingerPrint()
	{
		Iterator<Entry<Integer,Period>> it = counts_.entrySet().iterator();
		while (it.hasNext())
		{
			Entry<Integer,Period> c = it.next();
			Period p =c.getValue();
			if  (p.end < time_ )
			{						
					fp_.Put(p);
					it.remove();
			}
		}
	}
	
	int frame = 0;

	
	
	@Override
	public boolean OnReceived(Common.Frequency[] frequency, long timeoffset) 
	{
		Utils.Dbg("buf:%time:%d",time_);
		if (frequency == null) 
		{
			time_+=timeoffset;
			return true;
		}

		int index = (int)time_ / Common.Config.Instance().WindowSize();

		LinkedList<Period> lp = vector_.get(index);
		if (lp == null)
		{
			lp = new LinkedList<Period>();
			vector_.set(index, lp);
		}

		for (int i = 0; i < frequency.length;++i)
		{
			boolean found = false;
			for (Period p:lp)
			{
				if (p.frequency.frequency.compareTo(frequency[i].frequency) == 0)
				{
					found = true;
					p.frequency.level.add(frequency[i].level);
					break;
				}
			}
			
			if (! found)
			{
				lp.add(fp_.new Period(frequency[i], time_));
			}
		}
		
		/*
		for (int i = 0; i < frequency.length; ++i)
		{
			Integer fr = frequency[i].frequency;
			Period p = counts_.get(fr);
			if (p == null)
			{
				p = fp_.new Period(frequency[i], time_);
				counts_.put(fr, p);
			}
			else
			{
				p.frequency.level = p.frequency.level.max(frequency[i].level);
				++p.count;
			}
			
			p.end =time_ + timeoffset;
		}
		
		if (time_ > 0 && time_ % Config.Instance().WindowSize() == 0)
		{
			addToFingerPrint();
		}
		*/
		time_ += timeoffset;
		return true;
	}

	@Override
	public void OnError() {

	}

}
