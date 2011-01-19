package Streamer;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

import sun.security.util.Debug;

import Common.Dbg;
import Common.FingerPrint;
import Common.Settings;
import Common.Frequencier.Catcher;
import Common.Utils;

public class Summator implements Catcher, Loader.Processor {
	
	private class FingerPrintWrapper
	{
		float last = 0;
		LinkedList<FrameWaiter> waiters = new LinkedList<FrameWaiter>();
		FingerPrint fp;
		public FingerPrintWrapper(FingerPrint fp)
		{
			this.fp = fp;
		}
		@Override
		public boolean equals(Object obj)
		{
			return fp.equals(((FingerPrintWrapper)obj).fp);
		}
	}
	
	private class FrameWaiter
	{
		long time;
		long offset_begin;
		long  offset_end;
		long maxed_time;
		float last = 0;
		List<Float> lasts = new LinkedList<Float>();
		int index;
		float total;
		//int id;
		FingerPrintWrapper fpw;
		public FrameWaiter(FingerPrintWrapper fpw, long time, int index, float equip)
		{
			//id=++fw_id_;
			this.fpw = fpw;
			this.time = time;
			this.offset_begin = time  + settings_.WindowSize()  - settings_.WindowSize() / 8;
			this.offset_end = time + settings_.WindowSize()  +  settings_.WindowSize() / 8;
			this.index = index;
			this.total = equip;
			//Utils.Dbg("%d, offsets:%d  %d (%d) %d  %d",off, this.offset.get(0),this.offset.get(1),this.offset.get(2),this.offset.get(3),this.offset.get(4));
		}
		
		public FrameWaiter(FingerPrintWrapper fpw, long time, float equip)
		{
			this.fpw = fpw;
			this.time = time;
			this.offset_begin = time  + settings_.WindowSize()  - settings_.WindowSize() / 8;
			this.offset_end = time + settings_.WindowSize()  +  settings_.WindowSize() / 8;
			this.index = 1;
			this.total = equip;
		}
		
		public void Next(long time, float equip)
		{
			++index;
			this.total+=equip;
			last  = 0;
			this.offset_begin = time  + settings_.WindowSize()  - settings_.WindowSize() / 8;
			this.offset_end = time + settings_.WindowSize()  +  settings_.WindowSize() / 8;
		}
		
	}
	//static private int fw_id_ = 0;

	private DTW dtw_ ;
	private Settings settings_ = null;
	private long time_  =  0; 
	private LinkedList<FrameWaiter> waiters_ = new LinkedList<FrameWaiter>();	
	private List<FingerPrintWrapper> fingerPrints_ = new CopyOnWriteArrayList<FingerPrintWrapper>();
	private Resulter resulter_;
	
	public Summator(Settings settings, Resulter resulter)
	{
		settings_ = settings;
		resulter_ = resulter;
		dtw_  = new DTW(15);
	}

	@Override
	public void AddFingerPrint(FingerPrint fp)
	{
		fingerPrints_.add(new FingerPrintWrapper(fp));
	}

	@Override
	public void RemoveFingerPrint(FingerPrint fp) 
	{
		fingerPrints_.remove(new FingerPrintWrapper(fp));
	} 
	
	private List<FrameWaiter> removes_ = new LinkedList<FrameWaiter>();
	
	
	
	@Override
	public boolean OnReceived(float[][] mfcc, long timeoffset) 
	{
		FrameWaiter limit = null;		
		// Utils.Dbg("FingerPrints: %d Waiters:%d",fingerPrints_.size(), waiters_.size());
		
		LinkedList<FingerPrintWrapper>  maxs = new LinkedList<FingerPrintWrapper>();
		float max = 0;
		FingerPrintWrapper max_fpw = null;
		for (FingerPrintWrapper fpw: fingerPrints_)
		{
			float x = dtw_.measure(fpw.fp.Get(0), mfcc);	
			if ( x >  0.1 )
			{
				if ( x   <  fpw.last )
				{
					Dbg.Debug("%d | %s  add to waiters: %.03f", time_, fpw.fp.Id(), x);							
					FrameWaiter fw = new FrameWaiter(fpw, time_, 1, fpw.last);
					if ( limit == null )
					{
						limit = fw;
					}
					waiters_.add(fw);		
				}				
				fpw.last = x;
			}
		}

		/*if (max_fpw != null)
		{
			Dbg.Debug("%d | %s  add to waiters: %.03f", time_, max_fpw.fp.Id(), max_fpw.last);							
			
			FrameWaiter fw = new FrameWaiter(max_fpw.fp, time_, 1);
			waiters_.add(fw);	
		}*/
		
		removes_.clear();
		
		String dbg  = "";
		for (FrameWaiter fw: waiters_)
		{
			
			if ( fw == limit ) 
			{
				break;
			}
			
			if ( time_  > fw.time  + fw.fpw.fp.Time() ) 
			{				
				removes_.add(fw);
				continue;
			}
			
			if ( time_  >=  fw.offset_begin && time_ <= fw.offset_end )	
			{	
				float x = dtw_.measure(fw.fpw.fp.Get(fw.index), mfcc);
				
				if ( x > fw.last )
				{
					fw.maxed_time  = time_;
					fw.last = x;
				}	
			}
			else
			{
				if (time_  > fw.offset_end)
				{
				//	Dbg.Debug("%d |  %s MATCH:  index:%d max:%f / %f",time_, fw.fp.Id(), fw.index, fw.last,fw.total);		
					if (fw.last > 0.1)
					{		
						fw.Next(fw.maxed_time, fw.last);										
						if ((double)fw.index / fw.fpw.fp.Frames() >= Config.Instance().FingerPrintEquivalency())
						{

											
								if (resulter_ != null)
								{										
										resulter_.OnFound(fw.fpw.fp.Id(), System.currentTimeMillis() / 1000,fw.total / fw.index);
								}									
								
								removes_.add(fw);
						}
					}
					else
					{
						//Utils.Dbg("%d| %s[%d] NOTMATCH:  index:%d/%f", time_, fw.fp.Id(), 0,fw.index, fw.max_offset);
						removes_.add(fw);
					}
				}
			}
		}
				
		if (! waiters_.isEmpty())
		{
			for(FrameWaiter fw: waiters_)
			{
				dbg+=String.format("%s | %.03f [%.03f / %d] \n", fw.fpw.fp.Id(), fw.total * fw.index,fw.total, fw.index);			
			}
			Dbg.Debug(dbg);
		}
		waiters_.removeAll(removes_);
		time_+=timeoffset;
		return true;
	}

	@Override
	public void OnError() {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnIgnore(long timeoffset) {
		// TODO Auto-generated method stub
		
	}





}
