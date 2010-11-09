package Common;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

public class Gistogram implements Serializable
{
	private static final long serialVersionUID = -5803859693235708023L;

	private Vector<Frequency> data_ = new Vector<Frequency>();
	
	public Gistogram()
	{
	}
	
	private int count_ = 0;
	
	public int Count()
	{
		return count_;
	}

	public int Size()
	{
		return data_.size();
	}
	
	public Vector<Frequency> Data()
	{
		return data_;
	}
	
	public boolean IsEmpty()
	{
		return data_.isEmpty();
	}
	
	public BigDecimal getMaxLevel(Frequency[] frequency)
	{
		BigDecimal max = new BigDecimal(0);
		for (int i =0; i < frequency.length; ++i)
		{
			if (frequency[i].level.compareTo(max) == 1)
			{
				max = frequency[i].level;
			}
		}
		return max;
	}
	
	public void Merge(Frequency [] frequency)
	{
		BigDecimal max = getMaxLevel(frequency);
		if (data_.size() > 0 && data_.lastElement().level.compareTo(max) == 1) return;
	
		for (int i =0 ; i < frequency.length; ++i)
		{
			boolean found = false;
			int j;
			for (j = 0; j < data_.size(); ++j)
			{
				if (data_.get(j).frequency.compareTo(frequency[i].frequency) == 0)
				{
					found = true;
					break;
				}
			}
			
			if (found)
			{
				if (data_.get(j).level.compareTo(frequency[i].level) == -1)
				{
					data_.set(j, frequency[i]);
				}
			}
			else
			{
				boolean added = false;
				for (int k = 0; k < data_.size(); ++k)
				{
					if (data_.get(k).level.compareTo(frequency[i].level) == -1)
					{
						data_.indexOf(frequency, k);
						added = true;
						break;
					}
				}
				
				if (!added)
				{
					data_.add(frequency[i]);
				}
				
				
			}
		}
	}
	
	public void Append(Frequency[] frequency)
	{
		++count_;
		for (int i = 0; i < frequency.length; ++i)
		{
			boolean found = false;
			int j = 0;					
			for (j = 0; j < data_.size(); ++j)
			{
					
					if (data_.get(j).frequency.equals(frequency[i].frequency))
					{
						data_.get(j).level = data_.get(j).level.add(frequency[i].level);
						found = true;
						break;
					}
			}
			
			if (! found)
			{		
				data_.add(new Frequency(frequency[i].frequency, frequency[i].level));
				j = data_.size() - 1;
			}
			
			
			while (j > 0 && data_.get(j - 1).level.compareTo(data_.get(j).level) == -1 )
			{
				Collections.swap(data_, j - 1, j);
				--j;
			}
		}
	}

	@Override
	public String toString()
	{
		String str = new String();
		
		for (int i =0 ;i < Math.min(data_.size(), Config.Instance().LevelsCount()); ++i)
		{
			str+=String.format("%d\t%.09f\n", data_.get(i).frequency,  data_.get(i).level);
		}
		str+="---\n";
		return str;
	}

	public void Deduct(Frequency[] frequency)
	{	
		--count_;
		for (int i = 0; i < frequency.length; ++i)
		{
			boolean found = false;
			int j = 0;
			for (j = 0; j < data_.size(); ++j)
			{
					if (data_.get(j).frequency.equals(frequency[i].frequency))
					{
						data_.get(j).level = data_.get(j).level.subtract(frequency[i].level);
						found = true;
						break;
					}
			}
			
			if (! found)
			{
				continue;
			}
			
			if (data_.get(j).level.compareTo(new BigDecimal(0)) == 0)
			{
				data_.remove(j);
				continue;
			}
			
			
			while (j < data_.size() - 1 && data_.get(j + 1).level.compareTo(data_.get(j).level) == 1)
			{
				Collections.swap(data_, j + 1, j);
				++j;
			}
		}
	}

	public static boolean Compare(Gistogram a, Gistogram b, int size)
	{
		
		size = Math.min(size, Math.min(a.Size(), b.Size()));
		Vector<Frequency> 	av = new Vector<Frequency>(a.data_);
		av.setSize(size);
		Vector <Frequency> bv = new Vector<Frequency>(b.data_);
		bv.setSize(size);
		//Utils.Dbg("A\t\tB");
		
		int k = 0;
		for (int i =0 ;i < bv.size(); ++i)
		{
			//Utils.Dbg("%d\t\t%d", av.get(i).frequency,bv.get(i).frequency);
			for (int j = 0; j < av.size(); ++j)
			{
				if (bv.get(i).frequency.compareTo(av.get(j).frequency) == 0)
				{
					
					++k;
					break;
				}
			}
		}
		
		return k == bv.size();

		/*int c = 0;
		Iterator<Double> k1 = a.keySet().iterator();
		Iterator<Double> k2 = b.keySet().iterator();

		while (k1.hasNext() || k2.hasNext())
		{
			Utils.Dbg("%f\t%f", k1.hasNext()  ?  k1.next() : 0, k2.hasNext() ? k2.next() : 0 );
		}

		Iterator<Double> k = b.keySet().iterator();

		while (k.hasNext())
		{
			double frequency = k.next();
			if (a.containsKey(frequency))
			{			
				c++;
			}	
		}
		return (double) c /  b.size();*/
		
	}

}
