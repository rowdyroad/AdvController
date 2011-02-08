package Streamer;

import java.util.ArrayList;
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
		long maxed_time;
		boolean has_max = false;
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

	static int fw_id_ = 0;
	
	private class FrameWaiter
	{
		long time;
		long offset_begin;
		long  offset_end;
		long maxed_time;
		long next_time;
		float last = 0;
		List<Float> lasts = new LinkedList<Float>();
		int index;
		float total;
		long  timestamp;
		boolean has_max = false;
		int id;
		int errors = Config.Instance().IgnoredErrors();
		float equip;
		float total_equip;
		FingerPrintWrapper fpw;
		
		public FrameWaiter(FingerPrintWrapper fpw, long time, int index, float equip)
		{
			id=++fw_id_;
			this.fpw = fpw;
			this.time = time;
			this.next_time = time + settings_.WindowSize();
			this.offset_begin = (long) (time  + ( 1.0f - Config.Instance().OverlappedCoef()*2)*  settings_.WindowSize());
			this.offset_end = (long) (time  + ( 1.0f + Config.Instance().OverlappedCoef()*2)*  settings_.WindowSize());
			this.index = index;
			this.total = equip;
			this.timestamp = System.currentTimeMillis();
			this.equip =  equip;
			this.total_equip = this.total / this.fpw.fp.Frames();
			//Utils.Dbg("%d, offsets:%d  %d (%d) %d  %d",off, this.offset.get(0),this.offset.get(1),this.offset.get(2),this.offset.get(3),this.offset.get(4));
		}
		
		public void Next(long time, float equip)
		{
			++index;
			this.total+=equip;
			last  = 0;
			this.offset_begin = (long) (time  + ( 1.0f - Config.Instance().OverlappedCoef()*2)*  settings_.WindowSize());
			this.offset_end = (long) (time  + ( 1.0f + Config.Instance().OverlappedCoef()*2)*  settings_.WindowSize());
			this.next_time = time + settings_.WindowSize();
			this.equip = total / index;						
			this.total_equip = this.total / this.fpw.fp.Frames();
		}
		
	}
	//static private int fw_id_ = 0;

	private DTW dtw_ ;
	private Settings settings_ = null;
	private long time_  =  0; 
	private ArrayList<FrameWaiter> waiters_ = new ArrayList<FrameWaiter>();	
	private List<FingerPrintWrapper> fingerPrints_ = new CopyOnWriteArrayList<FingerPrintWrapper>();
	private Resulter resulter_;
	
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
	
	private List<FrameWaiter> removes_ = new LinkedList<FrameWaiter>();
	
	private void DbgMFCC(float[][] mfcc)
	{
		String str = new String();
					
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
	
	float[][] last_mfcc;	
	
	FrameWaiter max_fw = null;
	
	private void  submitResult(FrameWaiter fw)
	{
		Dbg.Debug("next: %s[%d][%d] - %f %d %d",fw.fpw.fp.Id(), fw.id, fw.index, fw.fpw.last, fw.fpw.maxed_time, time_);
		fw.Next(fw.maxed_time, fw.last);				
		if (fw.index >= fw.fpw.fp.Frames() *  Config.Instance().Equals())
		{
			Dbg.Info("!!! res[%d]: %s[%d]  %f",fw.index,fw.fpw.fp.Id(),fw.id, fw.total_equip);			
			resulter_.OnFound(fw.fpw.fp.Id(), fw.timestamp, System.currentTimeMillis(),  fw.total_equip);						
				for (int i = 0;  i < waiters_.size(); ++i)
				{
					final FrameWaiter fwz = waiters_.get(i);
					if (fwz.equip < fw.equip)
					{
						removes_.add(fwz);
					}
				}
				removes_.add(fw);			
		}
	}
		
	public boolean OnReceived(float[][] mfcc, long timeoffset) 
	{		
		int news = 0;
		for (FingerPrintWrapper fpw: fingerPrints_)
		{
			final float x = dtw_.measure(fpw.fp.Get(0), mfcc);
			if (x > 0.1f)
			{
					if (x < fpw.last)
					{
							if (fpw.has_max)
							{
								Dbg.Debug("new: %s - %f/%f %d %d",fpw.fp.Id(), fpw.last, x, fpw.maxed_time, time_);
								fpw.has_max = false;
								FrameWaiter fw = new FrameWaiter(fpw, fpw.maxed_time, 1,fpw.last);			
								waiters_.add(fw);
								news++;
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
		removes_.clear();	
		for (int i = 0; i < waiters_.size() - news; ++i)
		{
			FrameWaiter fw = waiters_.get(i);
			if (time_ < fw.offset_begin)
			{
				continue;
			}
			
			if (time_ > fw.offset_end)
			{
				if (fw.has_max && fw.last > 0.1f)
				{
					submitResult(fw);
				}
				else
				{
					waiters_.remove(fw);
					--i;
				}
				continue;
			}
						
			
			final float x = dtw_.measure(fw.fpw.fp.Get(fw.index), mfcc);				
			if (x < fw.last)
			{						
				if (fw.has_max)
				{										
						if (fw.last > 0.1f)
						{							
							submitResult(fw);
							fw.has_max = false;
						}
						else
						{
							removes_.add(fw);
							continue;
						}						
				}
			}
			else
			{	
				fw.maxed_time =  time_;
				fw.has_max = true;
			}
			
			fw.last = x;
		}
	
		
	//	Dbg.Info("%f",total_ / totals_);
	/*	FingerPrintWrapper max_fpw = null;
		float max = 0;
		
		for (FingerPrintWrapper fpw: fingerPrints_)
		{			
			final float x = dtw_.measure(fpw.fp.Get(0), mfcc);
			if (x > 0.1f && x > max)
			{
				max = x;
				max_fpw = fpw;
			}
		}
		
		if (max_fpw !=null)
		{
			FrameWaiter fw = new FrameWaiter(max_fpw, time_, 1, max);		
			Dbg.Debug("new: %s[%d] %f", max_fpw.fp.Id(), fw.id,  max);
			if (max_fw == null)
			{
				max_fw = fw;			
			}
			else
			{
				if (max_fw.total_equip < fw.total_equip)
				{
					max_fw = fw;
				}
			}			
			waiters_.add(fw);
		}
		
		removes_.clear();
		
		for (int i = 0; i < waiters_.size() - 1; ++i)
		{
			FrameWaiter fw = waiters_.get(i);		
			if (time_  ==  fw.next_time)
			{
				final float x = dtw_.measure(fw.fpw.fp.Get(fw.index), mfcc);
				if (x > 0.1f)
				{
					Dbg.Debug("next[%d]: %s[%d] %f\t%f %f",fw.index,fw.fpw.fp.Id(),fw.id, x,fw.equip, fw.total_equip);				
				}
				else
				{
						if (--fw.errors < 0)
						{
							Dbg.Debug("rem[%d]: %s[%d] %f - %f",fw.index,fw.fpw.fp.Id(),fw.id, x, fw.total_equip);				
							removes_.add(fw);
							continue;
						}
						else
						{
							Dbg.Debug("error[%d]: %s[%d] - %d",fw.index, fw.fpw.fp.Id(),fw.id, fw.errors);
						}				
				}
				
				fw.Next(time_,x);				
				if (max_fw == null)
				{
					max_fw = fw;			
				}
				else
				{
					if (max_fw.total_equip < fw.total_equip)
					{
						max_fw = fw;
					}
				}	
				
				if (fw.index >= fw.fpw.fp.Frames() * Config.Instance().Equals())
				{					
					if (max_fw == fw)
					{
						Dbg.Info("!!! res[%d]: %s[%d]  %f",fw.index,fw.fpw.fp.Id(),fw.id, fw.total_equip);						
						resulter_.OnFound(fw.fpw.fp.Id(), fw.timestamp, System.currentTimeMillis(),  fw.total_equip);
						max_fw = null;
						waiters_.clear();
						removes_.clear();
						break;
					}
					
					removes_.add(fw);
				}				
			}
			else
			{
					if (time_ > fw.next_time)
					{
						removes_.add(fw);
					}
			}
		}		*/
		waiters_.removeAll(removes_);
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
