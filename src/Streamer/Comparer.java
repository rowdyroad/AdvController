package Streamer;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import com.sun.corba.se.impl.javax.rmi.CORBA.Util;

import Common.FingerPrint;
import Common.Utils;
import Common.FingerPrint.Period;
import Common.Frequencier.Catcher;
import Common.Frequency;

public class Comparer implements Catcher{
	
	public interface Resulter
	{
		public boolean OnFound(String id, long timestamp, double equivalency);
	}
	static class FingerPrintWrapper
	{
		static public int identity = 0;
		FingerPrint fingerPrint_;
		int position_ = 0;
		long time_ = 0;
		long equivalency = 0;
		
		long diff = 0;
		int id;
		
		public FingerPrintWrapper(FingerPrint fingerPrint, int position)
		{
			fingerPrint_ = fingerPrint;
			position_ = position;
			equivalency = 1;
			id = ++identity;			
		}
		@Override
		public  boolean equals(Object o)
		{
			return ((FingerPrintWrapper)o).fingerPrint_.Id() == fingerPrint_.Id();
		}
	}
	class FingerPrintWaiter
	{
		int pos;
		int time;
		FingerPrint fp;
		
		public FingerPrintWaiter(FingerPrint fp)
		{
			this.fp = fp;
			pos = 1;
			time = 0;
		}
	}
	
	private Vector<FingerPrint> fingerPrints_ = null;
	private Resulter resulter_ = null;
	
	public Comparer(Vector<FingerPrint> fingerPrints, Resulter resulter)
	{
		fingerPrints_ = fingerPrints;
		resulter_ = resulter;
	}
	
	public static class FW
	{
		static private int gid = 0;
		int time = 0;
		int changedtime = 0;
		int count = 1;
		int pcount = 0;
		int diff = 0;
		int id;
		double level;
		List<Period> periods = new LinkedList<Period>();
		
		public FW()
		{
			this.id = ++gid;
		}
		@Override
		public  boolean equals(Object o)
		{
			return ((FW)o).id == id;
		}
	}
	
	int time_ = 0;
	
	List<Period> periods = new LinkedList<Period>();
	Map<FingerPrint, Integer> counts = new TreeMap<FingerPrint, Integer>();
	Map<FingerPrint, LinkedList<FW>> waiters = new TreeMap<FingerPrint,LinkedList<FW>>();

	long ztime_ = 0;
	
	LinkedList<Period> cache_ = new LinkedList<Period>();
	
	@Override
	public boolean OnReceived(Frequency[] frequency, long timeoffset) 
	{		
			
		if (frequency!=null)
		{
			for (int i = 0; i < fingerPrints_.size(); ++i) 
			{
				FingerPrint fp = fingerPrints_.get(i);
				LinkedList<Period> lp = fp.vector_.get(time_ / Common.Config.Instance().WindowSize());
				if (lp == null) continue;
				for (Period p: lp)
				{
					if (cache_.contains(p)) 
					{
						Utils.Dbg("exists");
						continue;
					}
					
					for (int j = 0; j < frequency.length; ++j)
					{
						if (Math.abs(p.frequency.frequency.doubleValue() - frequency[i].frequency.doubleValue())<=4)
						{
							cache_.add(p);
						}
					}
				}
			}
			Utils.Dbg("Size:%d", cache_.size());
			time_+=timeoffset;
		}
		


	

		
		/*Vector<FW> ignore = new Vector<FW>();
	
		if (frequency != null)
		{		
			for (int i = 0; i < fingerPrints_.size(); ++i) {
				FingerPrint fp = fingerPrints_.get(i);
				Vector<Period> pv = fp.Exists(0, frequency);
				if (pv.size() > 0) 
				{
					LinkedList<FW> list = waiters.get(fp);
					if ( list == null)
					{
						list = new LinkedList<FW>();
						waiters.put(fp, list);
					}
					FW f = new FW();
					f.periods.addAll(pv);
					//Utils.Dbg("Add to waiter: %d",f.id);
					list.add(f);
					ignore.add(f);
				}
			}
		}

		for (Entry<FingerPrint, LinkedList<FW>> item: waiters.entrySet())
		{			
			FingerPrint fp = item.getKey();
			LinkedList<FW> list = item.getValue();
			Iterator<FW> it = list.iterator();
			while (it.hasNext())
			{
				FW fw = it.next();
				if (ignore.contains(fw)) continue;
				
				fw.time += timeoffset;
				
				
				if (fw.time > fp.Time())
				{
					it.remove();
					continue;
				}
					
				if (frequency != null)
				{
					Utils.Dbg("%d",fw.time);
					Utils.DbgFrq(frequency);
					
					
					Vector<Period> pv = fp.Exists(fw.time, frequency);
					
					int c = 0;
					for (int j = 0; j < pv.size(); ++j) {
						Period p = pv.get(j);
						if (!fw.periods.contains(p)) {
							fw.periods.add(p);
							++fw.pcount;
							++c;
							//Utils.Dbg("%d - %d %f", pv.size(), fw.periods.size(), (double) fw.periods.size() / fp.Count());
						}
						++fw.count;
						
						
					}
					
			//		Utils.Dbg("added:%d",c);
				}
				//Utils.Dbg("%d - %d/%d [%d]", fw.id, fw.time, fw.changedtime, fw.periods.size());
			}
			
			if (list.isEmpty() || frequency == null) continue;

			Collections.sort(list, new Comparator<FW>() {
					@Override
					public int compare(FW arg0, FW arg1) {
						return -new Integer(arg0.periods.size()).compareTo(new Integer(arg1.periods.size()));
					}
				});
		
			while (list.size() > Common.Config.Instance().OverlappedCoef())
			{
				list.removeLast();
			}
	
			int ps =  list.getFirst().periods.size();
			double ev = (double)ps / fp.Count();
			Utils.Dbg("%s - /%d %f  %d/%d [%d]", fp.Id(), list.getFirst().id, ev, ps, fp.Count(),  list.getFirst().pcount);		
			if (ev >= Config.Instance().FingerPrintEquivalency())
			{
				if (resulter_ != null)
				{
					if (resulter_.OnFound(fp.Id(), System.currentTimeMillis() / 1000, ev))
					{
						waiters.clear();
						break;
					}
				}
			}

		}*/
		return true;
	}

	@Override
	public void OnError() {
		// TODO Auto-generated method stub
		
	} 
	
 
	
	
}
