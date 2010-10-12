import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import javax.sound.sampled.AudioInputStream;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import analys.FFT;
import analys.MFCC;

public class Frequencier {

	public interface Catcher
	{
		public boolean OnReceived(Frequency[] frequency, long timeoffset);
		public void OnError();
	}
	
	public class Frequency
	{
		Double  frequency;
		Double level;
		
		public Frequency(Double frequency, Double level)
		{
			this.frequency = frequency;
			this.level = level;
		}		
	}
	static private int WINDOW_SIZE = 4096;
	static private int OVERLAPPED_COEF = 3; 
	private AudioInputStream stream_;
	private FFT fft_;	
	private double [] buf_ = new double[WINDOW_SIZE];
	private int index_ = 0;
	private Catcher catcher_ = null;
	

	private double scale_;
	private int frameSize_;
	public Frequencier(AudioInputStream stream, Catcher catcher)
	{
		catcher_ = catcher;
		stream_ = stream;
		fft_ = new FFT(FFT.FFT_NORMALIZED_POWER, WINDOW_SIZE, FFT.WND_HAMMING);
		
		frameSize_ = stream.getFormat().getFrameSize();
		scale_ = 1.0 / ((double) stream.getFormat().getChannels() * (1 << (stream.getFormat().getSampleSizeInBits() - 1)));
		
		Utils.ShowStreamInfo(stream_);
	}
	

	private double[] read() throws Exception
	{
		byte[] b = new byte[frameSize_];
		while ( index_ <  WINDOW_SIZE)
		{		
				int c = stream_.read(b);
				if (c == -1)
				{
					if (index_ == 0) 
					{
						throw new Exception();
					}
					else
					{
						break;
					}
				}		
			short db =(short) (b[3] << 8 | b[2]);
			buf_[index_++] = db * scale_;			
		}	
		
		double [] ret = new double[WINDOW_SIZE];
		System.arraycopy(buf_, 0, ret, 0, WINDOW_SIZE);
		Arrays.fill(buf_, 0);
		System.arraycopy(ret, WINDOW_SIZE / OVERLAPPED_COEF, buf_, 0, WINDOW_SIZE  - WINDOW_SIZE / OVERLAPPED_COEF);
		index_ = (index_ < WINDOW_SIZE)  ?  0  :  WINDOW_SIZE  - WINDOW_SIZE / OVERLAPPED_COEF;
		return ret;
	}
	

	public void process()
	{
		int time = 0;
		Frequency[] last =null;
		
		try 
		{		
			while (true)
			{
				Frequency[] data = iterate();
			//	if (last == null && data[0].frequency == 0) continue;
				
				if (data!=null && !Arrays.equals(data,last))
				{
					
					if (! catcher_.OnReceived(data, time))
					{
						break;	
					}
					time = 0;
				}
				
				last = data;
				time += (int)( WINDOW_SIZE * 1000 / stream_.getFormat().getSampleRate() / OVERLAPPED_COEF );
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			catcher_.OnError();
			
		}
	}
	
	class DescComparator implements Comparator<Double>
	{

		@Override
		public int compare(Double arg0, Double arg1) {
			if (arg0 < arg1) return 1;
			if (arg0 > arg1) return -1;
			return 0;
		}
		
	}
	
	private Frequency[]  iterate() throws Exception
	{
		double[] data = read();
		fft_.transform(data, null);

		Frequency[] ret = new Frequency[data.length];
		SortedMap<Double, Double> map = new TreeMap<Double, Double>(new DescComparator());
	
		double  srps = stream_.getFormat().getSampleRate()  / WINDOW_SIZE;
		int start =  (int) (20 / srps);
		int stop = (int) (20000 / srps);
		for (int i= start; i <stop; ++i)
		{
			map.put(data[i], (double)i *srps);
		}
		
		Iterator<Entry<Double, Double>> it = map.entrySet().iterator();
		
		
	   double  d = 0;
	   
	  double  limit =map.firstKey() * 0.2;
	   int i=0;
	   while (it.hasNext())
	   {
			 Entry<Double, Double> kvp = it.next();
			 if (kvp.getKey() <= limit) break;
			 Utils.Dbg("%.03f / %f",kvp.getValue(), kvp.getKey());
	  		 ret[i++] = new Frequency(kvp.getValue(), kvp.getKey());
	   }
	   
	   Utils.Dbg("");
	   	   
		/*for (int i = 0; i < ret.length && it.hasNext(); ++i)
		{
			Entry<Double, Double> kvp = it.next();
			ret[i] = new Frequency(kvp.getValue(), kvp.getKey());
			System.out.printf("%.03f / %.03f\t", ret[i].frequency, ret[i].level);
		}
		
		for (int i  = 0; i < ret.length - 1; ++i)
		{
			d = ret[i].frequency*(ret[i].level - ret[i+1].level);
		}
		
		System.out.printf(" -  %.03f\n", d);*/
		
		
		return ret;
	}
}
