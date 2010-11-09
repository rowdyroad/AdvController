package Streamer;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.Map.Entry;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;


import Common.Config;
import Common.FingerPrint;
import Common.Gistogram;
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
		Gistogram gistogram_ = new Gistogram();
		
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
	private Vector<FingerPrintWrapper> waiters_ = new Vector<FingerPrintWrapper>();
	private List<FingerPrintWaiter> fpw = new LinkedList<FingerPrintWaiter>();
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
		int diff = 0;
		int id;
		List<Period> periods = new LinkedList<Period>();
		
		public FW()
		{
			this.id = ++gid;
		}
	}
	
	int time_ = 0;
	
	List<Period> periods = new LinkedList<Period>();
	Map<FingerPrint, Integer> counts = new TreeMap<FingerPrint, Integer>();
	
	Map<FingerPrint, LinkedList<FW>> waiters = new TreeMap<FingerPrint,LinkedList<FW>>();
	
	@Override
	public boolean OnReceived(Frequency[] frequency, long timeoffset) {
		if (frequency.length == 0)
			return true;

		for (int i = 0; i < fingerPrints_.size(); ++i) {
			FingerPrint fp = fingerPrints_.get(i);
			Vector<Period> pv = fp.Exists(0, frequency);
			if (pv.size() > 0) {
				LinkedList<FW> list = waiters.get(fp);
				
				if ( list == null)
				{
					list = new LinkedList<FW>();
					waiters.put(fp, list);
				}
				
				Utils.Dbg("Add to waiter: %s %d", fp.Id(), list.size());
					
				FW f = new FW();
				f.periods.addAll(pv);
				list.add(f);
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
				fw.time += timeoffset;
				Vector<Period> pv = fp.Exists(fw.time, frequency);
				
				for (int j = 0; j < pv.size(); ++j) {
					Period p = pv.get(j);
					if (!fw.periods.contains(p)) {
						fw.periods.add(p);
						//Utils.Dbg("%d - %d %f", pv.size(), fw.periods.size(), (double) fw.periods.size() / fp.Count());
					}
				}
				//Utils.Dbg("%d - %d/%d [%d]", fw.id, fw.time, fw.changedtime, fw.periods.size());
			}
			
			Collections.sort(list, new Comparator<FW>() {
				@Override
				public int compare(FW arg0, FW arg1) {
					Integer as = new Integer(arg0.periods.size());
					Integer af = new Integer(arg1.periods.size());
					return -as.compareTo(af);
				}
			});
			
			
			double ev = (! list.isEmpty()) ? (double) list.getFirst().periods.size() / fp.Count() : 0;
			Utils.Dbg("%s - %f", fp.Id(),  ev);	
			
			if (ev >= 0.9)
			{
				if (resulter_ != null)
				{
					if (resulter_.OnFound(fp.Id(), System.currentTimeMillis() / 1000, ev))
					{
						list.clear();
						break;
					}
				}
			}

		}
		

		
		
	
		

		time_ += timeoffset;
		return true;
	}

	@Override
	public void OnError() {
		// TODO Auto-generated method stub
		
	} 
	
 
	
	
}
