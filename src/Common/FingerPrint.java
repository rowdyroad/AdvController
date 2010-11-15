package Common;


import java.io.FileInputStream;
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
import java.util.TreeMap;
import java.util.Vector;
import java.util.Collections;;

public class FingerPrint implements Serializable,Comparable<FingerPrint> {

	private static final long serialVersionUID = 8559602924873961446L;

	public Map<Integer, LinkedList<Period>> totals_  = new TreeMap<Integer, LinkedList<Period>>();;
	public List<Period> periodsByTime_;
	public List<Period> periodsByCount_;	

	public class Period implements Serializable
	{
		private static final long serialVersionUID = -107614039539312946L;
		
		public Frequency frequency;
		public Long begin;
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
	private int count_ = 0;
	private long time_;
	
	public long Time()
	{
		return time_;
	}
	public FingerPrint(String id, long time)
	{
		id_ = id; 
		time_ = time;
		periodsByTime_ = new LinkedList<Period>();
		periodsByCount_ = new LinkedList<Period>();
	}

	public String Id() { return id_; }
	
	
	public Vector<Period> Exists(long time, Frequency[] frequency)
	{
		Vector<Period> vector = new Vector<Period>();
		for (int i = 0; i < frequency.length; ++i)
		{				
			for (Period p : periodsByTime_)
			{
				if (p.begin <= time && time <= p.end)
				{
						if (Math.abs(frequency[i].frequency - p.frequency.frequency) <= 4)
						{
							//Utils.Dbg("Compared %d[%d]  %d/[%d-%d]" ,frequency[i].frequency, p.frequency, time, p.begin,p.end );
							vector.add(p);
							break;
						}
					}
				}
		}
		
		//Utils.Dbg("Compared %d", vector.size());
		/*fv.setSize(frequency.length);
		for (int i =0; i < frequency.length; ++i)
		{
			fv.set(i, frequency[i].frequency);
		}
		Vector<Period> vector = new Vector<Period>();
		
		for (Period p : periodsByTime_)
		{
			if (p.begin <= time && time <= p.end)
			{
			//	Utils.Dbg("%d [%d - %d]", p.frequency, p.begin, p.end);
				for (int i = 0; i < fv.size(); ++i)
				{
					if (Math.abs(fv.get(i) - p.frequency) <= 4)
					{
						Utils.Dbg("Compared %d[%d]  %d/[%d-%d]" ,fv.get(i), p.frequency, time, p.begin,p.end );
						vector.add(p);
						fv.removeElementAt(i);
						break;
					}
				}
			}
		}*/
		
		return vector; 
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
		LinkedList<Period> vp = totals_.get(period.frequency.frequency);
		
		if (vp == null)
		{
			vp = new LinkedList<Period>();
			vp.add(period);
			++count_;
			totals_.put(period.frequency.frequency, vp);
		}
		else
		{
		/*	Period lp = vp.getLast();
			if (period.begin - lp.end  < Config.Instance().WindowSize() )
			{
				lp.end = period.end;
				lp.count += period.count;
			}
			else
			{*/
				vp.add(period);
				++count_;
		//	}
		}
		return period;
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
	 
	 public Vector<LinkedList<Period>> vector_;
	 
	 double max = 0;
	 double min = Double.MAX_VALUE;
	 
	 public void Set(Vector<LinkedList<Period>> vector)
	 {
		 vector_ = vector;
		 
	 }
	 
 	 public void ThinOut()
 	 { 
 		 
 		 
 		/*for (Period p : periodsByTime_)
 		{
 			max =Math.max(max, p.frequency.level.doubleValue());
			min = Math.min(min, p.frequency.level.doubleValue());
 		}
 		
 		Iterator<Period> it = periodsByTime_.iterator();
 		
 		double mean = (max + min) / 2;
 		Utils.Dbg("mean:%f  max:%f min:%f",mean,max,min);
 		while (it.hasNext())
 		{
 			Period p = it.next();
 			
 			if (p.frequency.level.doubleValue() < mean)
 			{
 				it.remove();
 			}
 		}*/
 		
 		for (Period p : periodsByTime_)
 		{
 			Utils.Dbg("%d [%d - %d] - %f", p.frequency.frequency, p.begin, p.end, p.frequency.level);
 		}
 		
 		 
 		/*periodsByTime_.clear();
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
					
					max =Math.max(max, p.frequency.level.doubleValue());
					min = Math.min(min, p.frequency.level.doubleValue());
					
					
					if (p.count <= M )
					{
						pit.remove();
					}
					else
					{
						//	p.begin =  new Long((long)Math.floor((double)p.begin  / Config.Instance().WindowSize()) * Config.Instance().WindowSize());
						//	p.end = new Long((long)Math.ceil((double)p.end  / Config.Instance().WindowSize()) * Config.Instance().WindowSize());
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
		
		Utils.Dbg("Max:%f Min:%f", max, min);
		*/
		
		
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
					str+=String.format("\t%d - %d  [%d] %f\n", p.begin, p.end, p.count,p.frequency.level);
				}
			}
			str+="\n";
		}
		
		{
			Iterator<Period> it = periodsByTime_.iterator();
			
			while (it.hasNext())
			{
				Period p = it.next();
				str+=String.format("%d  [%d - %d] [%d] %f\n", p.frequency.frequency, p.begin, p.end, p.count,p.frequency.level);
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
