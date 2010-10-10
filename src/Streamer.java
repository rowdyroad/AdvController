import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Streamer implements Frequencier.Catcher 
{
	class Listener
	{
		Capturer capturer;
		long time = 0;
		int equivalence = 1;
		int index;
		int id;
		long timestamp = System.currentTimeMillis();
		public Listener(int id, Capturer capturer, int index, long time)
		{
			this.id = id;
			this.capturer = capturer;
			this.index = index;
			this.time  = time;
		}
	}
	
	private int listenerId = 0;
	private AudioInputStream stream_;
	private Frequencier freq_;
	private Vector<Capturer> capturers_ = new Vector<Capturer>();
	private long time_ = 0;
	Vector<Listener> listeners_= new Vector<Listener>();
	
	HashMap<Capturer, Integer> indexes_ = new HashMap<Capturer, Integer>();
	private double minFrequency_ = 999999999;
	private double maxFrequency_ = 0;
	public Streamer() throws LineUnavailableException
	{
		AudioFormat format =  new AudioFormat(44100,16,2, true, false);
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		TargetDataLine line = (TargetDataLine)AudioSystem.getLine(info);
		line.open(format);
		line.start(); 
		stream_  =  new AudioInputStream(line);
		freq_ = new Frequencier(stream_, this);
	}
	
	public void AddCapturer(Capturer capt)
	{
		capt.Process();
		
		if (capt.Size() == 0) return;
		minFrequency_ = Math.min(capt.MinFrequency() * 0.9, minFrequency_);
		maxFrequency_ = Math.max(capt.MaxFrequency() * 1.1, maxFrequency_) ;
		
		Utils.Dbg("Length:%d", capt.Time());
		capturers_.add(capt);
	}
	
	public void Process()
	{
		System.out.printf("start at %s\n",Utils.Time(System.currentTimeMillis()));
		freq_.process();
	}
	
	
	public long Time()
	{
		return time_;
		
	}
	
	public boolean compare(double[] src, double[] dst, double diff)
	{
		double k  = 0;
		
		for (int i=0; i < src.length; ++i)
		{
			double z = src[i] - dst[i];
			k+=z*z;
		}
		
		Utils.Dbg(k/src.length);
		
		return true;
		
	}
	public  boolean compare(long src, long dst,  long diff)
	{		 
			return Math.abs(src - dst) < diff;
	}
	
	public  boolean compare(double  src, double dst,  double diff)
	{		 
			return Math.abs(src - dst) < diff;
	}
	
	
	private boolean ignoreFreq(double frequency, Capturer capturer)
	{
		return  (frequency <capturer.MinFrequency() * 0.9 || frequency > capturer.MaxFrequency()*1.1); 
	}
	
	private double[] last_ = null;
	
	private void processCapturers(double[] frequency)
	{
		if (last_!=null && Arrays.equals(last_, frequency)) return;
		
		last_ = frequency;
		for (int i =0; i <capturers_.size(); ++i)
		{
			 Capturer cpt = capturers_.get(i);			 
			 for (int j =0; j <cpt.Size() / 10; ++j)
			 {
				if (compare(cpt.Get(j).frequency, frequency, 4))
				{
						 listenerId++;
						 listeners_.add(new Listener(listenerId, cpt, j, -cpt.Get(j).timeoffset));
				}		
			 }
		}
	}
	
/*	private void processListeners(double[] frequency, long timeoffset)
	{
	for (int i = 0; i <listeners_.size(); ++i)
		{
			Listener lst = listeners_.get(i);
			Capturer cpt = lst.capturer;

			lst.time += timeoffset;
			
			if (lst.time > cpt.Time() * 1.1)
			{
				cpt.NotifyResult((double)lst.equivalence  / cpt.Size(), lst.timestamp);
				Utils.Dbg("%d Remove from listeners", lst.id);
				listeners_.remove(i--);
				continue;
			}
			
			if (lst.index > 0 && cpt.Get(lst.index-1).frequency == frequency)
			{
				continue;
			}
			
		
			Utils.Dbg("%d  %d\t%.03f", lst.id,  lst.time, frequency);
			 
			 for (int j = lst.index;  j <cpt.Size(); ++j)
			 {
				if (compare(lst.time, cpt.Get(j).timeoffset,64))
				{
					if (compare(frequency, cpt.Get(j).frequency, 	4))
					{
						lst.index = j+1;
						lst.equivalence++;
						Utils.Dbg("%d Equie: %d   %d - %d / %.03f - %.03f : %d", lst.id, lst.equivalence,lst.time, cpt.Get(j).timeoffset, frequency, cpt.Get(j).frequency, j);
						if (lst.index>= cpt.Size())
						{			
							cpt.NotifyResult((double)lst.equivalence  / cpt.Size(), lst.timestamp);
							Utils.Dbg("%d Remove from listeners", lst.id);
							listeners_.remove(i--);
						}
						break;
					}
				}
			 }
			 

		}
	}*/
	
	long  ts = System.currentTimeMillis();
	@Override
	public boolean OnReceived(double[] frequency, long timeoffset) 
	{
		time_+=timeoffset;
		Utils.Dbg("frequency: %.03f - %d\t%d",frequency, time_, System.currentTimeMillis() -  ts);
		processCapturers(frequency);
		
		
		return true;
	}

	@Override
	public void OnError() {
		// TODO Auto-generated method stub
		System.out.printf("end");
	}

}