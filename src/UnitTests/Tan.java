package UnitTests;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import Calculation.FFT;
import Common.Dbg;
import Common.Overlapper;
import Common.Source;

public class Tan   implements Source.AudioReceiver
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
	public Tan()
	{
		fft_ = new FFT(FFT.FFT_NORMALIZED_POWER,4096 ,FFT.WND_BLACKMAN_NUTTALL);
	}
	@Override
	public void OnSamplesReceived(float[] db) {
		// TODO Auto-generated method stub
		Dbg.Info("aaa");
			while (true)
			{
				float[] data = over.Overlapp(db);
				if (data == null) break;				
				fft_.transform(data, null);
				
				
				SortedSet<Freq> freqs = new TreeSet<Freq>();
				float sum = 0;
				for (int i  = 0; i < data.length/2; ++i)
				{
					sum+=data[i];
					freqs.add(new Freq(i, data[i]));
				}			
				
				Dbg.Info(sum);
				List<Freq> flist = new LinkedList<Freq>();
				int i = 0;
				for (Freq f : freqs)
				{
					flist.add(f);
					if (++i > 50) break;
				}
				Collections.sort(flist, new Comparator<Freq>() {
					@Override
					public int compare(Freq o1, Freq o2) {
						return o1.freq.compareTo(o2.freq);
					}});
				
				
				for (Freq f : flist)
				{
					Dbg.Info("%d - %f", f.freq, f.level);
				}
				
				Dbg.Info("");
			}
			
		

	}
	
	
	

}
