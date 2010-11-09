package Common;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

public class Frequencier implements Source.AudioReceiver {

	public interface Catcher
	{
		public boolean OnReceived(Frequency[] frequency, long timeoffset);
		public void OnError();
	}
	
	private FFT fft_;	
	private double [] buf_ = new double[Config.Instance().WindowSize()];
	private int index_ = 0;
	private Catcher catcher_ = null;
	private int overlapped_length_;
	
	public Frequencier(Catcher catcher)
	{
		catcher_ = catcher;
		fft_ = new FFT(FFT.FFT_MAGNITUDE, Config.Instance().WindowSize(), FFT.WND_HAMMING);
		overlapped_length_ =  Config.Instance().WindowSize() / Config.Instance().OverlappedCoef();
		
	}
	
	int time_ = 0;
	Frequency[] last_ =null;

	Map<Double, Double> gistogram = new HashMap<Double,Double>();
	
	@Override
	public void OnSampleReceived(double db) 
	{
		buf_[index_++] = db;
		
		if (index_ >= Config.Instance().WindowSize())
		{
			try
			{
				Frequency[] data = iterate(buf_);
				if (! catcher_.OnReceived(data, overlapped_length_))
				{
						return;	
				}
				
				index_ = Config.Instance().WindowSize()  - overlapped_length_;
				System.arraycopy(buf_, overlapped_length_, buf_, 0, index_);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return;
			}
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
	
	private Frequency[]  iterate(double[] data) throws Exception
	{
		fft_.transform(data, null);
		
		Frequency[] ret = new Frequency[Config.Instance().LevelsCount()];
		SortedMap<Double, Integer> map = new TreeMap<Double, Integer>(new DescComparator());
		
		double  srps = (double)Config.Instance().SampleRate() / Config.Instance().WindowSize();
		
		int start =  (int) (Config.Instance().MinFrequency() / srps);
		int stop = (int) (Config.Instance().MaxFrequency() / srps);
	
		for (int i = start; i <stop; ++i)
		{
				map.put(data[i], (int)(i *srps));
		}	
			
		if (map.firstKey() < 0.0005) return new Frequency[0];
		
		Iterator<Entry<Double, Integer>> it = map.entrySet().iterator();
		
	   int i = 0;
	   while (it.hasNext() && i < Config.Instance().LevelsCount())
	   {
			 Entry<Double, Integer> kvp = it.next();
	  		 ret[i++] = new Frequency(kvp.getValue(), new BigDecimal(kvp.getKey()));
	   }
	   
	   if (i  < Config.Instance().LevelsCount() )
	   {
		   return Arrays.copyOf(ret, i);
	   }
	   
		return ret;
	}



}
