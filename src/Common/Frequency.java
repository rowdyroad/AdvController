package Common;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Frequency implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6626122023565571435L;
	public Integer  frequency;
	public Double level;
	
	public Frequency(Integer frequency, Double level)
	{ 
		this.frequency = frequency;
		this.level = level;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		Frequency f = (Frequency)obj;
		return  (Math.abs(f.frequency - frequency) <= 4);
	}
	
	static public void Merge(List<Frequency> a, List<Frequency> b)
	{
		for (Frequency fb: b)
		{
			int index = a.indexOf(fb);
			if (index == -1)
			{
				a.add(fb);
			}
			else
			{
				a.get(index).level = Math.max(a.get(index).level, fb.level);
			}
		}
		
		Collections.sort(a, new Comparator<Frequency>() {
			@Override
			public int compare(Frequency arg0, Frequency arg1) {
				return -arg0.level.compareTo(arg1.level);
			}});		
		
			while (a.size() > Common.Config.Instance().LevelsCount() )
			{
				((LinkedList<Frequency>)a).removeLast();
			}
			
	}
	
}