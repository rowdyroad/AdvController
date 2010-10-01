package Common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

public class FingerPrint implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8559602924873961446L;

	public class Record implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 8201562766910455351L;
		public long timeoffset;
		public Common.Frequency[] frequency;
		
		public Record(long timeoffset, Common.Frequency[] frequency)
		{
			this.timeoffset = timeoffset;
			this.frequency = frequency;
		}
	};
	private String id_;
	private Vector<Record> vector_ = new Vector<Record>();
	private double min_ = Double.MAX_VALUE;
	private double max_ = Double.MIN_VALUE;

	public FingerPrint(String id)
	{
		id_ = id;
	}

	public String Id() { return id_; }
	
	public double MinFrequency()
	{
		return min_;
	}
	
	public double MaxFrequency()
	{
		return max_;
	}
 
	public int Size()
	{
		return vector_.size();
	}
	
	public boolean Put(long timeoffset,  Frequency[] frequency)
	{
		return  vector_.add(new Record(timeoffset, frequency));
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
	
	 public void Serialize(String filename) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(filename);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(this);
		oos.flush();
		oos.close();
	}
	
	static public FingerPrint Deserialize(String filename) throws IOException, ClassNotFoundException
	{
		FileInputStream fis = new FileInputStream(filename);
		ObjectInputStream oin = new ObjectInputStream(fis);
		return (FingerPrint)oin.readObject();
	}

}
