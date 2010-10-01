package Common;

import java.io.Serializable;

public class Frequency implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6626122023565571435L;
	public Double  frequency;
	public Double level;
	
	public Frequency(Double frequency, Double level)
	{ 
		this.frequency = frequency;
		this.level = level;
	}		
}