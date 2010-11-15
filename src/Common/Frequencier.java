package Common;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

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
	private double frequency_step_;
	private int process_start_;
	private int process_stop_;
	
	
	public Frequencier(Catcher catcher)
	{
		catcher_ = catcher;
		fft_ = new FFT(FFT.FFT_MAGNITUDE, Config.Instance().WindowSize(), FFT.WND_HAMMING);
		overlapped_length_ =  Config.Instance().WindowSize() / Config.Instance().OverlappedCoef();
		frequency_step_ = (double)Config.Instance().SampleRate() / Config.Instance().WindowSize();
		
		process_start_ =  (int) (Config.Instance().MinFrequency() / frequency_step_);
		process_stop_ = (int) (Config.Instance().MaxFrequency() / frequency_step_);
	}
	
	int time_ = 0;
	Frequency[] last_ =null;

	Map<Double, Double> gistogram = new HashMap<Double,Double>();
	
	@Override
	public void OnSamplesReceived(double[] db) 
	{
		if (db.length < Config.Instance().WindowSize())
		{
			double[] newdb =  new double[Config.Instance().WindowSize()];
			Arrays.fill(newdb, 0);
			System.arraycopy(db,0, newdb, 0, db.length);
			db = newdb;
		}
		
		int db_len = db.length;
		int db_index = 0;
		
		while (db_len > 0)
		{
			int len = db.length - index_;

			System.arraycopy(db, db_index, buf_, index_, len);
			index_+=len;
			db_index += len;
			db_len-=len;
			
			if (index_ == Config.Instance().WindowSize())
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
	
	
	private Frequency[]  iterate(double[] buf) throws Exception
	{
		
		double dbm = Double.NEGATIVE_INFINITY;
		
		/*for (int i =0;i<buf.length;++i)
		{
			double db = 20*Math.log10(buf[i]);
			if (Double.isInfinite(db) || Double.isNaN(db)) continue;
			dbm = Math.max(db, dbm);
		}
		
		if (dbm < -20) return null;
		*/
		double[] data = AltFFT.transform(buf);
		
		 LinkedList<Frequency> flist = new LinkedList<Frequency>();
		

		boolean hasMax = false;
		for (int i = process_start_; i < process_stop_;++i)
		{
			if (data[i] < 1)  continue;

			double diff = data[i+1] - data[i];
			
			if (diff < 0 && !hasMax)
			{
				int freq = ((int)(i*frequency_step_));
				flist.add(new Frequency(freq, new BigDecimal(data[i])));
				hasMax = true;
				continue;	
			}
			
			if (diff > 0 && hasMax)
			{
				hasMax = false;
			}
		}
		
		if (flist.isEmpty()) return null;
		
		
		Collections.sort(flist, new Comparator<Frequency>() {
			@Override
			public int compare(Frequency arg0, Frequency arg1) {
				
				return -arg0.level.compareTo(arg1.level);
			} });
		
		while (flist.size() > 10) 
		{
			flist.removeLast();
		}
		Frequency[] ret = new Frequency[flist.size()];
		flist.toArray(ret);
		
		return  ret;
	/*	
		SortedMap<Double, Integer> map = new TreeMap<Double, Integer>(new DescComparator());		
		Frequency[] ret = new Frequency[Config.Instance().LevelsCount()];
		
		

	
		for (int i = start; i <stop; ++i)
		{
				map.put(data[i], (int)(i *srps));
		}	
		
		if (map.firstKey() < Common.Config.Instance().NoiseGate()) return new Frequency[0];

		
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
*/	   
	}



}
