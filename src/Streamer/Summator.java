package Streamer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import com.sun.corba.se.impl.javax.rmi.CORBA.Util;

import Common.FingerPrint;
import Common.Settings;
import Common.Utils;
import Common.Frequencier.Catcher;
import Common.Frequency;

public class Summator implements Catcher{

	private List<Comparer> comparers_ = new LinkedList<Comparer>();
	private Settings settings_ = null;
	private Long time_ = new Long(0); 
	private LinkedList<Frequency[]> list = new LinkedList<Frequency[]>();
	private LinkedList<LinkedList<Frequency>> captures = new LinkedList<LinkedList<Frequency>>();
	private Writer wr;
	private List<FingerPrint> fingerPrints_ = new LinkedList<FingerPrint>();
	public Summator(Settings settings)
	{
		  try {
			wr = new BufferedWriter(new FileWriter(new File("log")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		settings_ = settings;
	}
	
	Vector<double[]> source = new Vector<double[]>();
	
	public void AddFingerPrint(FingerPrint fp)
	{
		fingerPrints_.add(fp);
	}

	private LinkedList<Frequency> MergeFrequency (Frequency[] frequency)
	{
		if (list.size() >= Common.Config.Instance().OverlappedCoef())
		{
			list.removeFirst();
		}
		list.add(frequency);
		
		if (list.size() < Common.Config.Instance().OverlappedCoef()) return null;
		double max = 0;
		for (Frequency[] f: list)
		{
			max = Math.max(max, f[0].level);
		}
		if (max < 1) return null;
		
		LinkedList<Frequency> pat  = new LinkedList<Frequency>();
		
		for (Frequency[] f: list)
		{
			for (int i = 0; i  < f.length; ++i)
			{
				int index = pat.indexOf(f[i]);
				if (index == -1)
				{
					pat.add(f[i]);
				}
				else
				{
					Double level = pat.get(index).level;
					pat.get(index).level = Math.max(f[i].level,level);
				}
			}
		}
		
		Collections.sort(pat, new Comparator<Frequency>() {
			@Override
			public int compare(Frequency arg0, Frequency arg1) {
				// TODO Auto-generated method stub
				return -arg0.level.compareTo(arg1.level);
			}});
		
		
		while (pat.size() > Common.Config.Instance().LevelsCount())
		{
			pat.removeLast();
		}

		return pat;
	}
	
	
	FingerPrint fp;
	long next =0;
	int total = 0;
	int max = 0;
	int index = 4;
	boolean started = false;
	LinkedList<LinkedList<Frequency>> fr = new LinkedList<LinkedList<Frequency>>();
	
	Vector<double[]> mfcc_ = new Vector<double[]>();
	
	DTW dtw_ = new DTW();
	
	
	class FrameWaiter
	{
		long time;
		int index;
		int totals;
		FingerPrint fp;
		public FrameWaiter(FingerPrint fp, long time)
		{
			this.fp = fp;
			this.time = time;
			this.index = 1;
			this.totals=  1;
		}
	}
	
	
	
	LinkedList<FrameWaiter> waiters_ = new LinkedList<FrameWaiter>();
	@Override
	public boolean OnReceived(double[] mfcc, long timeoffset) 
	{
		mfcc_.add(mfcc);
		time_+=timeoffset;
		
		if (time_ < settings_.WindowSize() / 2) return true;
	
		FrameWaiter limit = null;
		
		for (FingerPrint fp: fingerPrints_)
		{
			double x = dtw_.measure(fp.Get(0), mfcc_);
			if ( x > 0.1 )
			{
				FrameWaiter fw = new FrameWaiter(fp, time_);
				if (limit == null)
				{
					limit = fw;
				}
				Utils.Dbg("%d add to waiter:%s",time_, fp.Id());
				waiters_.add(fw);				
			}
		}
		
		List<FrameWaiter> removes = new LinkedList<FrameWaiter>();
		for (FrameWaiter fw: waiters_)
		{
			if (fw == limit) break;
			double x = dtw_.measure(fw.fp.Get(fw.index), mfcc_);
			if (x > 0.1)
			{
				fw.index++;
				fw.totals++;
				Utils.Dbg("%d/%d  compared:%s  index:%d totals:%d", time_, time_ - fw.time, fw.fp.Id(), fw.index,fw.totals);
				continue;
			}
			
			if (time_ > fw.time + fw.fp.Frames() * settings_.WindowSize()/2)
			{
				Utils.Dbg("%s add to removed", fw.fp.Id());
				removes.add(fw);
			}
		}
		
		
		waiters_.removeAll(removes);		
		mfcc_.remove(0);
		return true;
	}

	@Override
	public void OnError() {
		// TODO Auto-generated method stub

	} 




}
