package Common;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.Collections;;

public class FingerPrint implements Serializable,Comparable<FingerPrint> {

	private static final long serialVersionUID = 8559602924873961446L;
	public class Record implements Serializable
	{
		private static final long serialVersionUID = 8201562766910455351L;
		public long timeoffset;
		public Gistogram gistogram;
		
		public Record(long timeoffset, Gistogram gistogram)
		{
			this.timeoffset = timeoffset;
			this.gistogram = gistogram;
		}
	};
	public Map<Integer, LinkedList<Period>> totals_  = new TreeMap<Integer, LinkedList<Period>>();;
	public List<Period> periodsByTime_;
	public List<Period> periodsByCount_;	

	public class Period implements Serializable
	{
		private static final long serialVersionUID = -107614039539312946L;
		
		public Integer frequency;
		public Integer begin;
		public Integer end = 0;
		public Integer count = 1;
		
		public Period(Integer frequency, Integer begin)
		{
			this.frequency = frequency;
			this.begin = begin;
		}
		
		/*@Override
		public boolean equals(Object obj)
		{
			Period p = (Period)obj;
			return 
					p.frequency.compareTo(frequency) == 0 
					&& p.begin.compareTo(begin) ==0
					&& p.end.compareTo(end) ==0;
				
		}*/

	}
	
	
	public class PeriodByFrequency implements Comparator<Period>,Serializable
	{
		private static final long serialVersionUID = 7687850436377389469L;

		@Override
		public int compare(Period arg0, Period arg1) {
			return arg0.frequency.compareTo(arg1.frequency);
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
	private long time_ = 0;
	private Vector<Record> vector_ = new Vector<Record>();
	private double min_ = Double.MAX_VALUE;
	private double max_ = Double.MIN_VALUE;

	private int count_ = 0;
	
	
	public FingerPrint(String id)
	{
		id_ = id; 
		periodsByTime_ = new LinkedList<Period>();
		periodsByCount_ = new LinkedList<Period>();
	}

	public String Id() { return id_; }
	
	public double MinFrequency()
	{
		return min_;
	}
	
	public long Time()
	{
		return time_;
	}
	
	public Vector<Period> Exists(long time, Frequency[] frequency)
	{
		Vector<Integer> fv = new Vector<Integer>();
		fv.setSize(frequency.length);
		String str =new String(String.format("%d\t", time));
		for (int i =0; i < frequency.length; ++i)
		{
			str+=String.format("%d \t", frequency[i].frequency);
			fv.set(i, frequency[i].frequency);
		}
	//	Utils.Dbg(str);
		Vector<Period> vector = new Vector<Period>();
		
		for (Period p : periodsByTime_)
		{
			if (p.begin <= time && time <= p.end)
			{
			//	Utils.Dbg("%d [%d - %d]", p.frequency, p.begin, p.end);
				for (int i = 0; i < fv.size(); ++i)
				{
					if (fv.get(i).compareTo(p.frequency) == 0)
					{
				//		Utils.Dbg("Compared:%d", p.frequency);
						vector.add(p);
						fv.removeElementAt(i);
						break;
					}
				}
			}
		}
		
		return vector; 
	}
	
	public double MaxFrequency()
	{
		return max_;
	}
 
	public int Size()
	{
		return vector_.size();
	}
	
	private int totalPeriods_ = 0;
	private int totalCounts_ = 0;
	
	public Period Put(Period period)
	{
		++totalPeriods_;
		totalCounts_ += period.count;
		
		periodsByTime_.add(period);
		Collections.sort(periodsByTime_, new PeriodByTime());
		periodsByCount_.add(period);
		Collections.sort(periodsByCount_, new PeriodByCount());
		LinkedList<Period> vp = totals_.get(period.frequency);
		
		if (vp == null)
		{
			vp = new LinkedList<Period>();
			vp.add(period);
			++count_;
			totals_.put(period.frequency, vp);
		}
		else
		{
			Period lp = vp.getLast();
			if (period.begin - lp.end  < Config.Instance().WindowSize())
			{
				lp.end = period.end;
				lp.count += period.count;
			}
			else
			{
				vp.add(period);
				++count_;
			}
		}
		return period;
	}
	
	public Record Get(int i)
	{
		try
		{
			return vector_.get(i);
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	public Vector<Record> Items()
	{
		return vector_;
	}
	
	public int Count()
	{
		return periodsByTime_.size();
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
 		periodsByTime_.clear();
 		int M = (int) Math.ceil((double)totalCounts_ / totalPeriods_);
 		Utils.Dbg("%d",M);
 		{
	 		Iterator<Entry<Integer, LinkedList<Period>>> it = totals_.entrySet().iterator();
			while (it.hasNext())
			{
				Entry<Integer, LinkedList<Period>>c = it.next();
				LinkedList<Period> ps= c.getValue();
				Iterator<Period> pit = ps.iterator();
				while (pit.hasNext())
				{
					Period p = pit.next();			
					if (p.count <= M )
					{
						pit.remove();
					}
					else
					{
							p.begin =  (int)Math.floor((double)p.begin  / Config.Instance().WindowSize()) * Config.Instance().WindowSize();
							p.end = (int)Math.ceil((double)p.end  / Config.Instance().WindowSize()) * Config.Instance().WindowSize();
							periodsByTime_.add(p);
					}
				}
				
				if (ps.size() == 0)
				{
					it.remove();
				}
			}
 		 }
		
		if (! periodsByTime_.isEmpty())
		{
			Collections.sort(periodsByTime_, new PeriodByTime());
			Period fp = periodsByTime_.get(0);
			if (fp.begin > 0)
			{
				Iterator<Period> it = periodsByTime_.iterator();
				while (it.hasNext())
				{
					Period p = it.next();
					p.begin -= fp.begin;
					p.end   -= fp.begin;
				}
			}
		}
	 }
	 
	@Override
	public String toString()
	{
		String str = new String();
			{
			Iterator<Entry<Integer, LinkedList<Period>>> it  =totals_.entrySet().iterator();
			while (it.hasNext())
			{
				Entry<Integer, LinkedList<Period>>c = it.next();		
				Integer fr = c.getKey();
				LinkedList<Period> ps= c.getValue();
				
				Iterator<Period> pit = ps.iterator();
				
				str+=String.format("FREQ: %d\n", fr);
				
				while (pit.hasNext())
				{
					Period p = pit.next();
					str+=String.format("\t%d - %d  [%d]\n", p.begin, p.end, p.count);
				}
			}
			str+="\n";
		}
		
		{
			Iterator<Period> it = periodsByTime_.iterator();
			
			while (it.hasNext())
			{
				Period p = it.next();
				str+=String.format("%d  [%d - %d] [%d]\n", p.frequency, p.begin, p.end, p.count);
			}
			
		}
		str+="\n";
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
