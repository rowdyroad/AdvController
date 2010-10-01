package Capturer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import Common.Config;
import Common.FingerPrint;
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
		
		Iterator<Entry<Double, Integer>> it = r.entrySet().iterator();		
	
		while (it.hasNext() )
		{
			   Entry<Double, Integer> kvp = it.next();
			   Utils.Dbg("%f - %d", kvp.getKey(), kvp.getValue() );
		}
		
		return fp_;
	}
	
	public long Time()
	{
		return time_;
	}
	private Common.Frequency[] last_ =null;
	private SortedMap<Double, Integer> r = new TreeMap<Double, Integer>();
	
	@Override
	public boolean OnReceived(Common.Frequency[] frequency, long timeoffset) 
	{		
		if (last_ == null || !Arrays.equals(last_, frequency))
		{
			fp_.Put(time_+ timeoffset, frequency);
			last_ = frequency;
		}
		time_ += timeoffset;
		return true;
	}
	

	@Override
	public void OnError() {

	}

}
