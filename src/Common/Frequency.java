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
	public BigDecimal level;
	
	public Frequency(Integer frequency, BigDecimal level)
	{ 
		this.frequency = frequency;
		this.level = level;
	}
	
	
}