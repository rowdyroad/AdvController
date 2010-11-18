package Streamer;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import com.sun.corba.se.impl.javax.rmi.CORBA.Util;

import Common.Config;
import Common.FingerPrint;
import Common.Settings;
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
	
	private List<FingerPrint> fingerPrints_ = null;
	private Resulter resulter_ = null;
	private int window_size_;
	private int overlapped_length_;
	private Settings settings_;
	
	public Comparer(List<FingerPrint> fingerPrints, Resulter resulter, Settings settings)
	{
		fingerPrints_ = fingerPrints;
		resulter_ = resulter;
		window_size_ = settings.WindowSize();
		overlapped_length_ = settings.OverlappedLength();
		settings_ = settings;
		
		for (FingerPrint fp :fingerPrints_)
		{
			sign.put(fp, new Signature());
		}
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
		int[] maxes;
		List<Period> periods = new LinkedList<Period>();
		
		public FW(FingerPrint fp)
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
	LinkedList<Period> cache_ = new LinkedList<Period>();
	LinkedList<Period> periods_ = new  LinkedList<Period>();
	
	class Signature
	{
		Integer max = 0;
		Integer offset = 0;

	}
	
	Map<FingerPrint, Signature> sign = new TreeMap<FingerPrint, Signature>();
	
	
	long timestamp =  System.currentTimeMillis();
	
	int k = 0;
	int t = 0;
	@Override
	public boolean OnReceived(Frequency[] frequency, long timeoffset) 
	{		
		
		if (frequency != null && frequency.length > 0 && frequency[0].level.compareTo(new BigDecimal(1)) == 1)
		{	
	
			for (FingerPrint fp: fingerPrints_)
			{
				Signature signature = sign.get(fp);
				List<Period> pl = fp.Exists(k, frequency);
			
				
				if (pl != null)
				{
					Utils.Dbg("C:%d",pl.size());
				}
				
				if (pl ==null || pl.size()>=9) 
				{
					k++;
				}

			}
		}
		
		time_+=timeoffset;
		
		/*Vector<FW> ignore = new Vector<FW>();		
		if (frequency != null && frequency.length > 0 && frequency[0].level.compareTo(new BigDecimal(1)) == 1)
		{					
			Utils.Dbg("time:%d", time_);
			time_+=timeoffset;

			for (int j = 0; j < fingerPrints_.size(); ++j) 
			{
				FingerPrint fp = fingerPrints_.get(j);
				List<Period> pv = fp.Exists(0, frequency);
				LinkedList<FW> list = waiters.get(fp);
				if ( list == null)
				{
					list = new LinkedList<FW>();
					waiters.put(fp, list);
				}

				Utils.Dbg("A%d", pv.size());
				FW f = new FW(fp);
				f.periods.addAll(pv);
				list.add(f);
				ignore.add(f);

				Collections.sort(list, new Comparator<FW>() {
					@Override
					public int compare(FW arg0, FW arg1) {
						return -new Integer(arg0.periods.size()).compareTo(new Integer(arg1.periods.size()));
					}
				});

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
					Utils.Dbg("%d remove from time over",fw.id);
					it.remove();
					continue;
				}

				int index = fw.time / overlapped_length_;

				//	Utils.Dbg("Index: %d",index);	
				List<Period> pv = null;

				try
				{
					pv = fp.Exists(index, frequency);
					if (pv == null)
					{
						continue;
					}
				}
				catch (IndexOutOfBoundsException e)
				{
					Utils.Dbg("%d index out of bounds :%d",fw.id, index);
					it.remove();
					continue;
				}
				//Utils.Dbg("Periods length: %d", pv.size());

				int c = 0;
				for (Period p: pv)
				{
					if (!fw.periods.contains(p)) 
					{
						fw.periods.add(p);
						++fw.pcount;
						++c;
						//Utils.Dbg("%d - %d %f", pv.size(), fw.periods.size(), (double) fw.periods.size() / fp.Count());
					}
					++fw.count;
				}

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


			for (FW fw: list)
			{
				Utils.Dbg("%d[%d] - %d", fw.id,  fw.time / settings_.WindowSize(), fw.periods.size());
			}
			Utils.Dbg("");

			int ps =  list.getFirst().periods.size();
			double ev = (double)ps / fp.Count();
			//Utils.Dbg("%s - /%d %f  %d/%d [%d]", fp.Id(), list.getFirst().id, ev, ps, fp.Count(),  list.getFirst().pcount);		
			if (ev >= 1)
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
