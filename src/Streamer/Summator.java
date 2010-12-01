package Streamer;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import Common.FingerPrint;
import Common.Settings;
import Common.Utils;
import Common.Frequencier.Catcher;

public class Summator implements Catcher{
	
	private class FingerPrintWrapper
	{
		boolean maxed = false;
		double last = 0;
		FingerPrint fp;
		public FingerPrintWrapper(FingerPrint fp)
		{
			this.fp = fp;
		}
	}
	
	private class FrameWaiter
	{
		long time;
		Vector<Long> offset = new Vector<Long>();		
		long offset_begin;
		long  offset_end;
		long maxed_time;
		double max_offset = 0;
		int offset_index = 0;
		int index = 1;
		int id;
		FingerPrint fp;
		public FrameWaiter(FingerPrint fp, long time)
		{
			id=++fw_id_;
			this.fp = fp;
			this.time = time;
			this.offset_begin = time  + settings_.WindowSize()  - settings_.WindowSize() / 4;
			this.offset_end = time + settings_.WindowSize()  +  settings_.WindowSize() / 4;
			//Utils.Dbg("%d, offsets:%d  %d (%d) %d  %d",off, this.offset.get(0),this.offset.get(1),this.offset.get(2),this.offset.get(3),this.offset.get(4));
		}
		
		public void Next(long time)
		{
			++index;
			max_offset  = 0;
			this.offset_begin = time  +settings_.WindowSize()  - settings_.WindowSize() / 4;
			this.offset_end = time + settings_.WindowSize()  +  settings_.WindowSize() / 4;
		}
		
	}
	static private int fw_id_ = 0;

	private DTW dtw_ = new DTW();
	private Settings settings_ = null;
	private long time_  =  0; 
	private LinkedList<FrameWaiter> waiters_ = new LinkedList<FrameWaiter>();	
	private List<FingerPrintWrapper> fingerPrints_ = new LinkedList<FingerPrintWrapper>();
	private Resulter resulter_;
	
	public Summator(Settings settings, Resulter resulter)
	{
		settings_ = settings;
		resulter_ = resulter;
	}
	
	public void AddFingerPrint(FingerPrint fp)
	{
		fingerPrints_.add(new FingerPrintWrapper(fp));
	}
		
	@Override
	public boolean OnReceived(double[][] mfcc, long timeoffset) 
	{
		FrameWaiter limit = null;		
		for (FingerPrintWrapper fpw: fingerPrints_)
		{
			double x = dtw_.measure(fpw.fp.Get(0), mfcc);
			if ( x > 0.1 )
			{
				double diff = x - fpw.last;
				//Utils.Dbg("%d| add to waiter:%s/%f/%f/%f",time_,time_ % settings_.WindowSize(), fpw.fp.Id(),x,diff,max_);
				if (diff  <  0)
				{
					if (!fpw.maxed)
					{
						fpw.maxed = true;
						FrameWaiter fw = new FrameWaiter(fpw.fp, time_);
				//		Utils.Dbg("%d| ADD: %s[%d]  %d / %f",time_, fpw.fp.Id(),fw.id, offset, fpw.last);
						if (limit == null)
						{
							limit = fw;
						}
						fpw.maxed = false;
						fpw.last = 0;
						waiters_.add(fw);						
					}
				}
				else
				{
					fpw.maxed = false;
				}
				fpw.last = x;
			}
		}
		
		List<FrameWaiter> removes = new LinkedList<FrameWaiter>();
		
		Utils.Dbg("%d",waiters_.size());
		for (FrameWaiter fw: waiters_)
		{
			if (fw == limit) 
			{
				break;
			}
			
			if (time_ > fw.time  + fw.fp.Time()) 
			{				
				removes.add(fw);
				continue;
			}
			
			if (time_ >= fw.offset_begin && time_<=fw.offset_end)
			{	
				double x = dtw_.measure(fw.fp.Get(fw.index), mfcc);
				//Utils.Dbg("%d | %s[%d]  offset_begin:%d offset_end:%d /%f", time_, fw.fp.Id(), fw.id,fw.offset_begin, fw.offset_end, x);
				if (x > fw.max_offset)
				{
					fw.maxed_time  = time_;
					fw.max_offset = x;
				}	
			}
			else
			{
				if (time_ > fw.offset_end)
				{
					//Utils.Dbg("%d |  MATCH: %s[%d] index:%d max:%f",time_, fw.fp.Id(), fw.id, fw.index, fw.max_offset);		
					if (fw.max_offset > 0.1)
					{		
						fw.Next(fw.maxed_time);
						if ((double)fw.index / fw.fp.Frames() > 0.8)
						{
							if (resulter_.OnFound(fw.fp.Id(), System.currentTimeMillis() / 1000))
							{
								removes.addAll(waiters_);
								Runtime.getRuntime().gc();
								break;
							}
						}
					}
					else
					{
						//Utils.Dbg("%d| NOTMATCH: %s[%d]  index:%d/%f", time_, fw.fp.Id(), fw.id,fw.index, fw.max_offset);
						removes.add(fw);
					}
				}
			}
		}
		
		waiters_.removeAll(removes);
		time_+=timeoffset;
		
		/*
		mfcc_.add(mfcc);
		time_+=timeoffset;
		
		if (time_ < settings_.WindowSize() / 4) return true;
		
		
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
		
		
			
		mfcc_.remove(0);*/
		return true;
	}

	@Override
	public void OnError() {
		// TODO Auto-generated method stub

	} 




}
