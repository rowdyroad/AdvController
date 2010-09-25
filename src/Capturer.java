import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Capturer implements Frequencier.Catcher 
{

	public class Record
	{
		long timeoffset;
		double frequency;
		
		public Record(long timeoffset, double frequency)
		{
			this.timeoffset = timeoffset;
			this.frequency = frequency;
		}
	};
	
	public interface  Resulter
	{
		public void OnResult(Capturer id, double equivalence, long timestamp); 
	}
	
	private AudioInputStream stream_;
	private Frequencier freq_;
	private Vector<Record> vector_ = new Vector<Record>();
	private long time_ = 0;
	private double min_ = 999999999;
	private double max_ = 0;
	private Object id_ ;
	
	private Resulter resulter_ = null;
	
	
	public Object GetId()
	{
		return id_;
	}
	
	
	public Capturer(String filename, Object id, Resulter resulter) throws UnsupportedAudioFileException, IOException
	{
		File soundFile = new File(filename);
		stream_ = AudioSystem.getAudioInputStream(soundFile);
		
		freq_ = new Frequencier(stream_, this);
		id_ = id;
		resulter_ = resulter;
	}
	
	public Capturer(String filename) throws UnsupportedAudioFileException, IOException
	{
		this(filename, null, null);
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
		freq_.process();
	}
	
	public long Time()
	{
		return time_;
	}
	
	public Vector<Record> Items()
	{
		return vector_;
	}
	
	private double last_ = -1;
	@Override
	public boolean OnReceived(double frequency, long timeoffset) 
	{

		if (frequency > max_) { max_ = frequency; }
		if (frequency < min_) { min_ = frequency; }
		if (last_ != frequency)
		{
			System.out.printf("CPT: %d - %.05f\n", time_ + timeoffset, frequency);
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
