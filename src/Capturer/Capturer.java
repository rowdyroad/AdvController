package Capturer;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import Common.Source;
import Common.Frequencier;
import Common.Utils;
import Common.Source.Channel;


public class Capturer implements Frequencier.Catcher 
{
	public class Record
	{
		long timeoffset;
		Frequencier.Frequency[] frequency;
		
		public Record(long timeoffset, Frequencier.Frequency[] frequency)
		{
			this.timeoffset = timeoffset;
			this.frequency = frequency;
		}
	};
	
	public interface  Resulter
	{
		public void OnResult(Capturer id, double equivalence, long timestamp); 
	}

	private Vector<Record> vector_ = new Vector<Record>();
	private long time_ = 0;
	private double min_ = 999999999;
	private double max_ = 0;
	private Object id_ ;
	
	private Resulter resulter_ = null;
	private Source device_ = null;
	
	public Object GetId()
	{
		return id_;
	}
	
	Source source_ = null;
	
	public Capturer(String filename, Object id, Resulter resulter) throws Exception
	{
		source_ = new Source(AudioSystem.getAudioInputStream(new File(filename)));
		source_.RegisterAudioReceiver(Channel.LEFT_CHANNEL, new Frequencier(this));
		id_ = id;
		resulter_ = resulter;
	}
	
	public double MinFrequency()
	{
		return min_;
	}
	
	public double MaxFrequency()
	{
		return max_;
	}
	
	
	public int Size()
	{
		return vector_.size();
	}
	
	public Record Get(int i)
	{
		try
		{
			return vector_.get(i);
		}
		catch (Exception e)
		{
			return null;
		}
	}
	public void  NotifyResult(double equivalence, long timestamp)
	{
		if (resulter_ != null)
		{
			resulter_.OnResult(this, equivalence, timestamp);
		}
	}
	
	public void Process()
	{
		while (source_.Read()) {  };
	}
	
	public long Time()
	{
		return time_;
	}
	
	public Vector<Record> Items()
	{
		return vector_;
	}
	
	private Frequencier.Frequency[] last_ =null;
	@Override
	public boolean OnReceived(Frequencier.Frequency[] frequency, long timeoffset) 
	{
		if (last_ == null || !Arrays.equals(last_, frequency))
		{
			vector_.add(new Record(time_+ timeoffset, frequency));
			last_ = frequency;
		}
		time_ += timeoffset;
		return true;
	}

	@Override
	public void OnError() {

	}

}
