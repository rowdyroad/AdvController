package Capturer;
import java.io.File;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;
import javax.sound.sampled.AudioSystem;

import com.sun.jndi.url.iiopname.iiopnameURLContextFactory;

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
	
	public Capturer(String filename, String id) throws Exception
	{
		source_ = new Source(AudioSystem.getAudioInputStream(new File(filename)));
		source_.RegisterAudioReceiver(Channel.LEFT_CHANNEL, new Frequencier(this));
		fp_ = new FingerPrint(id);
	}
	
	public FingerPrint Process()
	{
		while (source_.Read()) {  };
		addToFingerPrint();
		fp_.ThinOut();	
		Utils.Dbg(fp_);
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
		for (int i = 0; i < frequency.length; ++i)
		{
			Integer fr = frequency[i].frequency;
			Period p = counts_.get(fr);
			if (p == null)
			{
				p = fp_.new Period(fr, (int) time_);
				counts_.put(fr, p);
			}
			else
			{
				++p.count;
			}
			
			p.end =(int) (time_ + timeoffset);
		}
		
		if (time_ > 0 && time_ % Config.Instance().WindowSize() == 0)
		{
			addToFingerPrint();
		}
		
		time_ += timeoffset;
		return true;
	}

	@Override
	public void OnError() {

	}

}
