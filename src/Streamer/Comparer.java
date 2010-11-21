package Streamer;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import Common.FingerPrint;
import Common.FingerPrint.Period;
import Common.Frequency;
import Common.Settings;
import Common.Utils;

public class Comparer {

	private FingerPrint fingerPrint_;
	private Settings settings_;
	
	
	
	public Comparer(FingerPrint fingerPrint, Settings settings, Resulter resulter)
	{
		fingerPrint_ = fingerPrint;
		settings_ = settings;
	}
	
	
	static class Wrapper
	{
		static private int gid = 0;
		int index;
		long time;
		long next;
		int idx;
		int id;
		int eq;
		public String toString()
		{
			return String.format("%d:   Time:%d   Next:%d   Index:%d   Idx:%d Total:%d",id, time,next,index,idx, eq);
		}
		
		public Wrapper(long time, int idx, int bufSize, int eq)
		{
			id = ++gid;
			this.time = time;
			this.next = time + bufSize;
			this.idx = idx;
			this.eq = eq;
			index = 1;
		}
		
	}
	
	LinkedList<Wrapper> wrps = new LinkedList<Wrapper>();
	
	
	public void OnReceived(LinkedList<LinkedList<Frequency>> captures, long time)
	{
		int max = 0;
		int k = 0;
		int index = 0;
		
		for (LinkedList<Frequency> fr: captures)
		{
			List<Period>p = fingerPrint_.Exists(0, fr);
			if (max < p.size())
			{
				max = p.size();
				index = k;
			}
			++k;
		}
		
		wrps.add(new Wrapper(time, index, settings_.WindowSize(), max));
		Utils.Dbg(wrps.getLast());
		
		for (Wrapper w: wrps)
		{
			if (w.next > time)
			{
				continue;
			}
			
			LinkedList<Frequency> fr = captures.get(w.idx);
			List<Period> p = fingerPrint_.Exists(w.index, fr);
			Utils.Dbg("%d: index:%d size:%d",w.id, w.index, p.size());
			
			w.next = time +  settings_.WindowSize();
			w.index++;
			w.eq+=p.size();	
		}
		
		Collections.sort(wrps, new Comparator<Wrapper>() {

			@Override
			public int compare(Wrapper arg0, Wrapper arg1) {
				// TODO Auto-generated method stub
				return -new Integer(arg0.eq).compareTo(new Integer(arg1.eq));
			}});
		
		int i =0;
		for (Wrapper w: wrps)
		{
			Utils.Dbg(w);
			if (++i > 10) break;
		}
		Utils.Dbg("");
		
	}
	
	
}
