package UnitTests;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import Calculation.FFT;
import Common.Dbg;
import Common.Overlapper;
import Common.Source;

public class Limits  implements Source.AudioReceiver {
	void moveDown(float[] maxes, int[] freqs, int index	)
	{
		System.arraycopy(maxes, index, maxes, index + 1, maxes.length - (index + 1));
		System.arraycopy(freqs, index, freqs, index + 1, freqs.length - (index + 1));
	}
		
	int index = 0;

	Overlapper over = new Overlapper(4096, 2048);

	 public Limits()
	 {
			fft_ = new FFT(FFT.FFT_NORMALIZED_POWER,4096 ,FFT.WND_BLACKMAN_NUTTALL);
			
	 }
	private static FFT fft_;
	void moveUp(float[] maxes, int[] freqs, int index)
	{
		if (index == 0) return;		
		float max = maxes[index];
		int idx = index;
		for (int i = index - 1; i >= 0; --i )
		{
			if (max > maxes[i])
			{
				int f = freqs[i];
				float m = maxes[i];				
				freqs[i] = freqs[idx];
				maxes[i] = maxes[idx];			
				freqs[idx] = f;
				maxes[idx] = m;				
				idx = i;				
			}
			else
				break;
		}
	}
	
	void add(float[] maxes, int[] freqs, int freq, float max)
	{
		for (int i = 0; i < freqs.length; ++i)
		{
			if (freqs[i] == freq)
			{
				maxes[i]+=max;
				moveUp(maxes,freqs,i);
				return;
			}
		}
		
		for (int i =  0; i < maxes.length; ++i)
		{
				if (max > maxes[i])
				{
					moveDown(maxes,freqs,i);
					maxes[i] = max;
					freqs[i] = freq;
					return;
				}			
		}
	}
	
	 void print(float[] maxes, int[] freqs)
	{
		for (int i =0; i < maxes.length; ++i)
		{
			Dbg.Info("%d %f", freqs[i],maxes[i]);
		}
		Dbg.Info("");
	}
	 
	 void print(TreeMap<Integer, Integer> a)
	 {			 
		 String str = "";
		 for (Entry<Integer,Integer> kvp:  a.entrySet())
		 {
				str+=String.format("%04d[%03d] ",kvp.getKey(),kvp.getValue());
		 }
		 Dbg.Info(str);
	 }
	
	 LinkedList<TreeMap<Integer,Integer>> data_ = new LinkedList<TreeMap<Integer,Integer>>();
	
	 public int rms(TreeMap<Integer, Integer> a, TreeMap<Integer, Integer> b)
	 {
		 
		 int sum = 0;
		 
		 for (Entry<Integer, Integer> kvp : a.entrySet())
		 {
			 int a_w = kvp.getValue();			 
			 int b_w =  (b.containsKey(kvp.getKey())) ? b.get(kvp.getKey()) : 0;
			// Dbg.Info("%d | %d %d",kvp.getKey(), a_w, b_w);
			 sum += Math.round(Math.pow(a_w - b_w,2));			 
		 }
		 
		 for (Entry<Integer, Integer> kvp : b.entrySet())
		 {
			 if (!a.containsKey(kvp.getKey()))
			 {				 
				// Dbg.Info("%d | %d",kvp.getKey(), kvp.getValue());
				 sum+= Math.round(Math.pow(kvp.getValue(),2));
			 }
		 }		 
		 return (int) Math.round(Math.sqrt((float)sum / a.size()));//(int) Math.sqrt(sum / a.size());			
	 }
	 
	 public void OnReceived( int[] freqs,float[] maxes)
	 {
		 TreeMap<Integer,Integer> row = new TreeMap<Integer,Integer>();
		 float sum  = 0;		
		 for (int i =0; i < maxes.length; ++i)
		 {
			 sum+=maxes[i];
		 }		
		 for (int i = 0; i < freqs.length; ++i)
		 {
			 final int m  = Math.round(maxes[i] * 100 / sum);
			row.put(freqs[i], m);
		 }
		 print(row);
		 
		 data_.add(row);
	 }
	 
	
	public void OnSamplesReceived(float[] db) 
	{
		// TODO Auto-generated method stub
		
		while (true)
		{
			float [] data = over.Overlapp(db);
			if (data == null) break;
			fft_.transform(data, null);			
			int max = 0;
			float max_db = Float.NEGATIVE_INFINITY;
			float[] maxes = new float[20];
			int[] freqs = new int[20]; 
			for (int i = 20; i < 500; ++i)
			{				
				float  cell =(float) i / 2;
				int right  = (int) Math.ceil(cell);
				int left = (int) Math.floor(cell);
				float left_w,right_w;
				
				if (right!= left)
				{									
					left_w= data[i] * (1 -  Math.abs(cell - left));
					right_w = data[i] * (1 -  Math.abs(cell - right));
				}
				else
				{
					add(maxes,freqs, right*2, data[i]/2);
					--left;
					++right;
					left_w = data[i] / 4;
					right_w = data[i] / 4;				
				}
				
				add(maxes,freqs, left*2, left_w);
				add(maxes,freqs, right*2, right_w);
				
				//Dbg.Info("%d * %f  |  %d  * %f | %d * %f",left *5, left_w, i,data[i], right * 5, right_w);			
			}						
			OnReceived(freqs,maxes);						
			//Dbg.Info("%d | %.03f / %d",(max+1), max_db, );
		}		
	}	
	
	static  public void Process(Limits a, Limits b)
	{
		DTWFreq dtw = new DTWFreq();
		List<TreeMap<Integer, Integer>> i1 = new LinkedList<TreeMap<Integer, Integer>>();
		List<TreeMap<Integer, Integer>> i2 = new LinkedList<TreeMap<Integer, Integer>>();
		for (int i = 0; i < a.data_.size();  ++i)
		{						
				i1.add(a.data_.get(i));				
				if (i1.size() > 0 && i1.size() == 16)
				{					
					Dbg.Info("%d:",i / 16);
					for (int j = 0 ; j < b.data_.size(); ++j)			
					{
						i2.add(b.data_.get(j));
						
						if (i2.size() > 0 && i2.size()  % 16 == 0)
						{
							float  r = dtw.measure(i1, i2);			
							
								Dbg.Info("\t%f | %d",r, j / 32);
							i2.remove(0);							
						}
					}
					i1.clear();

					Dbg.Info("");
				}
				
				
				
		}
	}
}
