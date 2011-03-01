package Streamer;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import Calculation.MFCCComparer;
import Common.Dbg;
import Common.FingerPrint;
import Common.Settings;
import Common.Frequencier.Catcher;

public class Summator implements Catcher, Loader.Processor {
	
	private class FingerPrintWrapper
	{
		float last = 0;
		long maxed_time;
		boolean has_max = false;
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

	static int fw_id_ = 0;
	static float frame_equals_ = 0.1f;
	private class FrameWaiter
	{
		long offset_begin;
		long  offset_end;
		long maxed_time;
		float last = 0;
		int index;
		float total;
		long  timestamp;
		int id;
		int errors = Config.Instance().IgnoredErrors(); 
		float equip;
		float total_equip;
		boolean removed = false;
		FingerPrintWrapper fpw;
		
		public FrameWaiter(FingerPrintWrapper fpw, long time, int index, float equip)
		{
			Set(fpw,time,index,equip);
		}
		
		private long PrevTime(long time)
		{
			return (long) (time  + ( 1.0f - Config.Instance().OverlappedCoef())*  settings_.WindowSize());		
		}
		
		private long NextTime(long time)
		{
			return (long) (time  + ( 1.0f + Config.Instance().OverlappedCoef())*  settings_.WindowSize());
		}
		
		public void Set(FingerPrintWrapper fpw, long time, int index, float equip)
		{
			id=++fw_id_;
			this.fpw = fpw;
			this.offset_begin = PrevTime(time);
			this.offset_end = NextTime(time); 
			this.index = index;
			this.total = equip;
			this.timestamp = System.currentTimeMillis();
			this.equip =  equip;
			this.total_equip = this.total / this.fpw.fp.Frames();
		}
		
		public void Next(long time, float equip)
		{
			
			++index;
			this.total+=equip;
			this.last  = 0;
			this.offset_begin = PrevTime(time);
			this.offset_end = NextTime(time); 
			this.equip = total / index;						
			this.total_equip = this.total / this.fpw.fp.Frames();
		}
		
	}
	//static private int fw_id_ = 0;

	private DTW dtw_ ;
	private Settings settings_ = null;
	private long time_  =  0; 
	private List<FrameWaiter> waiters_ = new LinkedList<FrameWaiter>();	
	private List<FingerPrintWrapper> fingerPrints_ = new CopyOnWriteArrayList<FingerPrintWrapper>();
	private Resulter resulter_;
	private List<FrameWaiter> removes_ = new LinkedList<FrameWaiter>();
	
	public Summator(Settings settings, Resulter resulter)
	{
		settings_ = settings;
		resulter_ = resulter;
		dtw_  = new DTW((int) (2.0f / Config.Instance().OverlappedCoef() - 1));		
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
	
	
	private void  submitResult(FrameWaiter fw)
	{
		Dbg.Debug("next: %s[%d][%d] - %f %d %d",fw.fpw.fp.Id(), fw.id, fw.index, fw.fpw.last, fw.fpw.maxed_time, time_);
		fw.Next(fw.maxed_time, fw.last);				
		if (fw.index >= fw.fpw.fp.Frames() *  Config.Instance().Equals())
		{
			Dbg.Debug("!!! res: %s[%d][%d]  %f",fw.fpw.fp.Id(),fw.id, fw.index, fw.total_equip);			
			resulter_.OnFound(fw.fpw.fp.Id(), fw.timestamp, System.currentTimeMillis(),  fw.total_equip);						
				
				Iterator<FrameWaiter> it = waiters_.iterator();
				while (it.hasNext())
				{				
					final FrameWaiter fwz = it.next();
					if (fwz.equip < fw.equip)
					{
						fwz.removed = true;
						removes_.add(fwz);
					}
				}
				fw.removed = true;
				removes_.add(fw);			
		}
	}
	
	private void DbgMFCC(float[][] mfcc)
	{
		String str = "";
		for (int j = 0; j < mfcc.length; ++ j)
		{
			str+="";
			for (int k = 0; k <  mfcc[j].length; ++k)
			{
				str+=String.format("%.03f\t",  mfcc[j][k]);
			}
			str+="\n";
		}		
		Dbg.Debug(str);
	}
	
	public boolean OnReceived(float[][] mfcc, long timeoffset) 
	{		
		int exists =  waiters_.size();
		Collections.shuffle(fingerPrints_);	
		for (FingerPrintWrapper fpw: fingerPrints_)
		{
			final float x = dtw_.measure(fpw.fp.Get(0), mfcc);	
			if (x > frame_equals_)
			{
					if (x < fpw.last)
					{
							if (fpw.has_max)
							{
								fpw.has_max = false;
								FrameWaiter fw = new FrameWaiter(fpw, fpw.maxed_time, 1,fpw.last);																
								Dbg.Debug("new: %s[%d] - %f/%f %d %d",fpw.fp.Id(),fw.id, fpw.last, x, fpw.maxed_time, time_);
								waiters_.add(fw);
							}
					}
					else
					{
							fpw.has_max = true;
							fpw.maxed_time = time_;
					}
					fpw.last = x;
			}
		}
				
		Iterator<FrameWaiter> it = waiters_.iterator();
		int i = 0;
		while (it.hasNext())
		{
			if (i++ >= exists) break;
			
			FrameWaiter fw = it.next();				
			if (fw.removed || time_ < fw.offset_begin)
			{
				continue;
			}
			
			if (time_ > fw.offset_end)
			{
					if (fw.last > frame_equals_)
					{
						submitResult(fw);
					}
					else
					{
						Dbg.Debug("rem: %s[%d][%d] - %f [%d  - %d]",fw.fpw.fp.Id(), fw.id, fw.index,  fw.last, fw.offset_begin, fw.offset_end);
						fw.removed = true;
						removes_.add(fw);									
					}
					continue;
			}
			
			final float x = dtw_.measure(fw.fpw.fp.Get(fw.index), mfcc);
			if (x > fw.last)
			{
				fw.last = x;
				fw.maxed_time = time_;
			}			
		}
		waiters_.removeAll(removes_);
		Collections.shuffle(waiters_);
		removes_.clear();
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
