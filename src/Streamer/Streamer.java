package Streamer;

import java.util.Arrays;
import java.util.HashMap;

import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import Capturer.Capturer;
import Common.Config;
import Common.FingerPrint;
import Common.Frequencier;
import Common.Source;
import Common.Source.Channel;
import Common.Utils;




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
	
	
	
	
	
	private AudioInputStream stream_;
	private Frequencier freq_;
	private Vector<FingerPrint> fingerPrints_ = new Vector<FingerPrint>();
	private long time_ = 0;
	Vector<Listener> listeners_= new Vector<Listener>();
	
	
	
	HashMap<Capturer, Integer> indexes_ = new HashMap<Capturer, Integer>();
	private double minFrequency_ = 999999999;
	private double maxFrequency_ = 0;

	Source source_;
	
	public Streamer() throws Exception
	{
		AudioFormat format =  new AudioFormat(Config.Instance().SampleRate(),16,2, true, false);
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		TargetDataLine line = (TargetDataLine)AudioSystem.getLine(info);
		line.open(format);
		line.start(); 
		source_ = new Source(new AudioInputStream(line));
	}
	
	public void AddFingerPrint(FingerPrint fp)
	{
		if (fp.Size() == 0) return;
		fingerPrints_.add(fp);
	}
	
	public void Process()
	{
		source_.RegisterAudioReceiver(Channel.LEFT_CHANNEL,new Frequencier(new Comparer(fingerPrints_, new ResultSubmiter())));
		source_.RegisterAudioReceiver(Channel.RIGHT_CHANNEL,new Frequencier(new Comparer(fingerPrints_, new ResultSubmiter())));
		while (source_.Read())
		{
			
		}
	}
	
	
	public long Time()
	{
		return time_;
	}
	
	public boolean compare(double[] src, double[] dst, double diff)
	{
		double k  = 0;
		k = k / src.length;
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

	private Common.Frequency[] last_ = null;
	
	private void processCapturers(Common.Frequency[] frequency)
	{
		if (last_!=null && Arrays.equals(last_, frequency)) return;
		
		last_ = frequency;
		for (int i =0; i <fingerPrints_.size(); ++i)
		{
			 FingerPrint fp = fingerPrints_.get(i);			
			 for (int j =0; j <fp.Size() ; ++j)
			 {
			
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
	public boolean OnReceived(Common.Frequency[] frequency, long timeoffset) 
	{
		time_+=timeoffset;
	//	Utils.Dbg("frequency: %.03f - %d\t%d",frequency, time_, System.currentTimeMillis() -  ts);
	//	processCapturers(frequency);
		
		
		
		
		return true;
	}

	@Override
	public void OnError() {
		// TODO Auto-generated method stub
		System.out.printf("end");
	}

}