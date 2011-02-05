package UnitTests;

import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import Calculation.FFT;
import Common.Dbg;
import Common.Overlapper;
import Common.Source;
import UnitTests.Tan.Freq;

public class Window implements Source.AudioReceiver
	{

		class Freq implements Comparable<Freq>
		{
			Integer freq;
			Float level;
			public int compareTo(Freq arg0)
			{
				return -level.compareTo(arg0.level); 
			}		
			
			public Freq(Integer freq, Float level)
			{
				this.freq  = freq;
				this.level = level;
			}
		}
		
		Overlapper over = new Overlapper(4096, 2048);

		FFT fft_;
		public Window()
		{
			fft_ = new FFT(FFT.FFT_NORMALIZED_POWER,4096 ,FFT.WND_BLACKMAN_NUTTALL);
		}
		
		Map<Integer, Float> data = new TreeMap<Integer,Float>();	
		
		@Override
		public void OnSamplesReceived(float[] db) {
			// TODO Auto-generated method stub
				
				Dbg.Info("first:%d",db.length);
							
				boolean has  = false;
				int z  = 0;
				int n = 0;
				float[] a = new float[4096];
				float sum  = 0;
				float last = 0;
				for (int i = 0;i < db.length; i+=4096)
				{					
					sum = 0;
					for (int j = 0; j < 4095; ++j)
					{
						a[j] = (float) (10 * Math.log(Math.sqrt(db[i+j]*db[i+j] + db[i+j+1]*db[i+j+1])));
						sum+=a[j];
					}
					
				
					Dbg.Info("%f",(last - sum) / 4096);

					last = sum;
					
						
					/*if (!Float.isInfinite(dbm))
					{		
						if (n > 0)
						{
							Dbg.Info("n:%d",n);
						}
						has = true;
						++z;
						n = 0;
					}	
					else
					{
						if (has)
						{
							Dbg.Info("k:%d",z);
							z = 0;
						}
						else
						{
							++n;
						}
						has = false;
					}*/
				}
			
			//	Map<Integer, Float> z = new TreeMap<Integer,Float>();
				
				/*	int m = 0;
				while (true)
				{
					float[] data = over.Overlapp(db);				
					if (data == null) break;				
					fft_.transform(data, null);
					
	
					float max = Float.MIN_VALUE;
					for (int i  = 5; i < data.length/2; ++i)
					{
							int c = i / 10;							
							float dleft = (float)(i % 10) / 10;					
							if (dleft == 0)
							{
								z.put(c - 1, ( (z.containsKey(c - 1)) ? z.get(c - 1) : 0) +  data[i] / 4);								
								z.put(c + 1, ( (z.containsKey(c + 1)) ? z.get(c + 1) : 0) +  data[i] / 4);
								z.put(c, ( (z.containsKey(c)) ? z.get(c ) : 0) +  data[i] / 2);								
							}
							else
							{
								float dright = (float)(10 - i % 10)  / 10;
								z.put(c, ( (z.containsKey(c)) ? z.get(c) : 0) +  data[i] * dleft );		
								z.put(c  + 1, ( (z.containsKey(c + 1)) ? z.get(c + 1) : 0) +  data[i]  * dright);		
								
							}
					}
					
					if (++m % 16 == 0)
					{
						SortedSet<Freq> freqs = new TreeSet<Freq>();
						for(Entry<Integer, Float> kvp : z.entrySet())
						{
								freqs.add(new Freq(kvp.getKey(), kvp.getValue()));
						}
						
						
						String str = "";
						for (Freq f  : freqs)
						{
							str+=String.format("%d  %f | ", f.freq, f.level);
						}
						z.clear();
						Dbg.Info(str);
						
					}
				}*/
				
		}
}
