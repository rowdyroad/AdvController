package Common;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.Collections;

import com.sun.org.apache.xpath.internal.Arg;

public class FingerPrint implements Serializable,Comparable<FingerPrint> {

	private static final long serialVersionUID = 8559602924873961446L;

	public Map<Integer, LinkedList<Period>> totals_  = new TreeMap<Integer, LinkedList<Period>>();;


	static public class Period implements Serializable
	{
		private static final long serialVersionUID = -107614039539312946L;
		public Frequency frequency;
		public Long begin = new Long(0);
		public Long end = new Long(0);
		public Integer count = 1;		
		public Period(Frequency frequency, Long begin)
		{
			this.frequency = frequency;
			this.begin =  begin;
		}
	}
	
	
	public class PeriodByFrequency implements Comparator<Period>,Serializable
	{
		private static final long serialVersionUID = 7687850436377389469L;

		@Override
		public int compare(Period arg0, Period arg1) {
			return arg0.frequency.frequency.compareTo(arg1.frequency.frequency);
		}		
	}
	
	public class PeriodByLevel implements Comparator<Period>,Serializable
	{
		private static final long serialVersionUID = 7617850436377389469L;

		@Override
		public int compare(Period arg0, Period arg1) {
			return -arg0.frequency.level.compareTo(arg1.frequency.level);
		}		
	}
	
	public class PeriodByTime implements Comparator<Period>,Serializable
	{
		private static final long serialVersionUID = 6981785399314304156L;

		@Override
		public int compare(Period o1, Period o2) {
			return o1.begin.compareTo(o2.begin);
		}
	}
	
	public class PeriodByCount implements Comparator<Period>,Serializable
	{
		private static final long serialVersionUID = 8723042262877079969L;
		@Override
		public int compare(Period o1, Period o2) {
			return -o1.count.compareTo(o2.count);
		}
	}
	
	private String id_;
	private long time_;
	private LinkedList<LinkedList<Period>> periods_  =  new LinkedList<LinkedList<Period>>();
	private int window_size_;
	private int levels_count_;
	
	public long Time()
	{
		return time_;
	}
	public FingerPrint(String id, long time, int windowSize, int levelsCount)
	{
		id_ = id; 
		time_ = time;
		window_size_ = windowSize;
		levels_count_ = levelsCount;
	}

	public String Id() { return id_; }
	
	public int LevelsCount()
	{
		return levels_count_;
	}

	public List<Period> Exists(int index, Frequency[] frequency)
	{
		LinkedList<Period> list = periods_.get(index);
		if (list == null) 
		{
			return null;
		}

		List<Period> ret = new LinkedList<Period>();
		for (Period p : list)
		{
			for (int i =0;i < frequency.length; ++i)
			{
				if (Math.abs(frequency[i].frequency - p.frequency.frequency) <= 10)
				{
					if (!ret.contains(p))
					{
						ret.add(p);
					}
				}
			}
		}
		return ret; 
	}
		
	private int totalPeriods_ = 0;
	private double maxLevel = 0;
	private double minLevel = 0;
	
	public boolean Add(LinkedList<Period> periods)
	{
		if (periods.isEmpty() || periods.getFirst().frequency.level.doubleValue() < minLevel) 
		{
			periods_.add(null);	
			return false;
		}
		totalPeriods_+=periods.size();
		maxLevel = Math.max(maxLevel, periods.getFirst().frequency.level.doubleValue());
		minLevel = Math.max(minLevel, periods.getLast().frequency.level.doubleValue());
		periods_.add(periods);
		return true;
	}

	public int Count()
	{
		int i = 0;
		for(LinkedList<Period> p : periods_)
		{
			if (p == null) continue;
			i+=p.size();
		}
		return i;
	}
	
	public int Frames()
	{
		return periods_.size();
	}
	
	public void Serialize(String filename) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(filename);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(this);
		oos.flush();
		oos.close();
	}
	 
	public void ThinOut()
	{ 
		Iterator<LinkedList<Period>> it = periods_.iterator();
		while (it.hasNext())
		{
			LinkedList<Period> list  = it.next();
			if (list == null) continue;
			if (list.getFirst().frequency.level.doubleValue() < minLevel)
			{
				periods_.set(periods_.indexOf(list),null);
			}
		}
		while (periods_.getFirst() == null)
		{
			periods_.removeFirst();
		}
		while (periods_.getLast() == null)
		{
			periods_.removeLast();
		}
	 }
	 
	@Override
	public String toString()
	{
		String str = new String();
		int i = 0;
		for (LinkedList<Period> list: periods_)
		{
			str+=String.format("%d:\n", i++); 
			if (list == null) 
			{
				str+="\tnull\n\n";
				continue;
			}
			for (Period p: list)
			{
				str+=String.format("\t%d  [%d - %d] %f\n", p.frequency.frequency, p.begin, p.end, p.frequency.level);
			}
			str+="\n";
		}
		return str;
	}
	
	static public FingerPrint Deserialize(String filename) throws IOException, ClassNotFoundException
	{
		FileInputStream fis = new FileInputStream(filename);
		ObjectInputStream oin = new ObjectInputStream(fis);
		FingerPrint fp =  (FingerPrint)oin.readObject();
		return fp;
	}

	@Override
	public int compareTo(FingerPrint arg0) {
	
		return id_.compareTo(arg0.id_);
	}

}
