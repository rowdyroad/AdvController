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
	
	private String id_;
	private long time_;
	private LinkedList<LinkedList<Frequency>> frequencies_  =  new LinkedList<LinkedList<Frequency>>();
	private int levels_count_;
	
	public long Time()
	{
		return time_;
	}
	public FingerPrint(String id, long time, int windowSize, int levelsCount)
	{
		id_ = id; 
		time_ = time;
		levels_count_ = levelsCount;
	}

	public String Id() { return id_; }
	
	public int LevelsCount()
	{
		return levels_count_;
	}

	public int LevelsCount(int index)
	{
		return frequencies_.get(index).size();
	}
	
	public List<Frequency> Exists(int index, List<Frequency> frequency)
	{
		LinkedList<Frequency> list = frequencies_.get(index);
		if (list == null) 
		{
			return null;
		}

		List<Frequency> ret = new LinkedList<Frequency>();
		
		for (Frequency f : list)
		{
			if (frequency.contains(f))
			{
				ret.add(f);
			}
		}
		return ret; 
	}
	
	public List<double[]> d = new LinkedList<double[]>();
		
	private int totalPeriods_ = 0;
	private double maxLevel = 0;
	private double minLevel = 0;
	
	public boolean Add(List<Frequency> frequency)
	{
		if (frequency.isEmpty()) 
		{
			frequencies_.add(null);	
			return false;
		}
		totalPeriods_+=frequency.size();
		maxLevel = Math.max(maxLevel, ((LinkedList<Frequency>)frequency).getFirst().level);
		minLevel = Math.max(minLevel, ((LinkedList<Frequency>)frequency).getLast().level);
		frequencies_.add((LinkedList<Frequency>)frequency);
		return true;
	}

	public int Count()
	{
		int i = 0;
		for(LinkedList<Frequency> f : frequencies_)
		{
			if (f == null) continue;
			i+=f.size();
		}
		return i;
	}
	
	public int Frames()
	{
		return frequencies_.size();
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
		while (frequencies_.getFirst() == null || frequencies_.getFirst().getFirst().level <= minLevel)
		{
			frequencies_.removeFirst();
		}
		while (frequencies_.getLast() == null  || frequencies_.getLast().getFirst().level <= minLevel)
		{
			frequencies_.removeLast();
		}
	 }
	 
	@Override
	public String toString()
	{
		String str = new String();
		int i = 0;
		for (LinkedList<Frequency> list: frequencies_)
		{
			str+=String.format("%d:\n", i++); 
			if (list == null) 
			{
				str+="\tnull\n\n";
				continue;
			}
			for (Frequency f: list)
			{
				str+=String.format("\t%d  %f\n", f.frequency,f.level);
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
