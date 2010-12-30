package Common;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class FingerPrint implements Serializable,Comparable<FingerPrint> {

	private static final long serialVersionUID = 8559602924873961446L;
	
	private String id_;
	private ArrayList<float[][]> mfcc_ = new ArrayList<float[][]>();
	private ArrayList<Float> means_ = new ArrayList<Float>();
	private ArrayList<Long> times_ = new ArrayList<Long>();
	private double sum_ = 0;
	private double mean_ = 0;
	
	public FingerPrint(String id)
	{
		id_ = id; 
	}
	
	public String Id() { return id_; }
	
	public float[][] Get(int index)
	{
		return mfcc_.get(index);
	}

	public boolean Add(float[][] mfcc, long time)
	{
		float v_mean = 0;
		for (int j = 0; j < mfcc.length; ++j)
		{
			float[] data = mfcc[j];
			float m = 0;
			for (int k = 0; k < data.length; ++k)
			{
				m+=data[k]*data[k];
			}
			m = (float) Math.sqrt(m);
			v_mean+=m;
		}
		v_mean = v_mean / mfcc.length;
		sum_ +=v_mean;
		means_.add(v_mean);
		mfcc_.add(mfcc);
		times_.add(time);
		return true;
	}
	
	public int Frames()
	{
		return mfcc_.size();
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
		mean_ = sum_ / mfcc_.size();		
		while (means_.get(0) < mean_ )
		{
			means_.remove(0);
			mfcc_.remove(0);
			times_.remove(0);
		}
		while (means_.get(means_.size() - 1) < mean_)
		{
			means_.remove(means_.size() - 1);
			mfcc_.remove(mfcc_.size() - 1);
			times_.remove(times_.size()-1);
		}
	}
	
	public long Time()
	{
		return times_.get(times_.size()-1) -  times_.get(0);
	}
	public double Mean(int index)
	{
		return means_.get(index);
	}
	
	public double Mean()
	{
		return sum_ / mfcc_.size();
	}
	 
	@Override
	public String toString()
	{
		String str = new String();
		
		for (int i = 0 ; i < mfcc_.size(); ++i)
		{
			str+=String.format("%d[%d]:\n", i,times_.get(i));
			
			for (int j = 0; j < mfcc_.get(i).length; ++ j)
			{
				for (int k = 0; k <  mfcc_.get(i)[j].length; ++k)
				{
					str+=String.format("%f ",  mfcc_.get(i)[j][k]);
				}
				str+="\n";
			}
		}
		str+="\n";
			
		return str;
	}
	
	static public FingerPrint Deserialize(File  file) throws IOException, ClassNotFoundException
	{
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream oin = new ObjectInputStream(fis);
		FingerPrint fp =  (FingerPrint)oin.readObject();
		return fp;
		
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
