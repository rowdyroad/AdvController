package Common;


import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

public class Frequencier implements Source.AudioReceiver {

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
	private FFT fft_;	
	private double [] buf_ = new double[Config.Instance().WindowSize()];
	private int index_ = 0;
	private Catcher catcher_ = null;
	
	public Frequencier(Catcher catcher)
	{
		catcher_ = catcher;
		fft_ = new FFT(FFT.FFT_NORMALIZED_POWER, Config.Instance().WindowSize(), FFT.WND_HAMMING);
	}
	
	int time_ = 0;
	Frequency[] last_ =null;

	@Override
	public void OnSampleReceived(double db) 
	{
		buf_[index_++] = db;
		
		if (index_ >= Config.Instance().WindowSize())
		{
			double [] ret = new double[Config.Instance().WindowSize()];
			System.arraycopy(buf_, 0, ret, 0, Config.Instance().WindowSize());
			Arrays.fill(buf_, 0);
			System.arraycopy(ret, Config.Instance().WindowSize() / Config.Instance().OverlappedCoef(), buf_, 0, Config.Instance().WindowSize()  - Config.Instance().WindowSize() / Config.Instance().OverlappedCoef());
			index_ = (index_ < Config.Instance().WindowSize())  ?  0  :  Config.Instance().WindowSize()  - Config.Instance().WindowSize() / Config.Instance().OverlappedCoef();
			
			try
			{
				Frequency[] data = iterate(ret);
				if (data!=null && !Arrays.equals(data,last_))
				{
					if (! catcher_.OnReceived(data, time_))
					{
						return;	
					}
					time_ = 0;
				}
				last_ = data;
				time_ += (int)( Config.Instance().WindowSize() * 1000 / Config.Instance().SampleRate()/ Config.Instance().OverlappedCoef() );
			}
			catch (Exception e)
			{
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
		Frequency[] ret = new Frequency[data.length];
		SortedMap<Double, Double> map = new TreeMap<Double, Double>(new DescComparator());
	
		double  srps = Config.Instance().SampleRate() / Config.Instance().WindowSize();
		int start =  (int) (Config.Instance().MinFrequency() / srps);
		int stop = (int) (Config.Instance().MaxFrequency() / srps);
		for (int i= start; i <stop; ++i)
		{
			map.put(data[i], (double)i *srps);
		}
		
		Iterator<Entry<Double, Double>> it = map.entrySet().iterator();
	   
	  double  limit =map.firstKey() * Config.Instance().LevelLimit();;
	   int i=0;
	   while (it.hasNext())
	   {
			 Entry<Double, Double> kvp = it.next();
			 if (kvp.getKey() <= limit) break;
			 Utils.Dbg("%.03f / %f",kvp.getValue(), kvp.getKey());
	  		 ret[i++] = new Frequency(kvp.getValue(), kvp.getKey());
	   }
	  
		return ret;
	}



}
