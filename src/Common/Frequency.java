package Common;

import java.io.Serializable;
import java.math.BigDecimal;

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
		return  (f.frequency - frequency == 0);
	}
	
	
}