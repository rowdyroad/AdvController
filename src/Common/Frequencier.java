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

import Common.AltFFT.Complex;

public class Frequencier implements Source.AudioReceiver {

	public interface Catcher
	{
		public boolean OnReceived(Frequency[] frequency, long timeoffset);
		public void OnError();
	}
	
	private FFT fft_;	
	private double [] buf_;
	private int index_ = 0;
	private Catcher catcher_ = null;
	private Settings settings_;
	
	public Frequencier(Catcher catcher, Settings settings)
	{
		buf_ = new double[settings.WindowSize()];
		catcher_ = catcher;
		fft_ = new FFT(FFT.FFT_MAGNITUDE, settings.WindowSize(), FFT.WND_HAMMING);
		settings_  = settings;
	}
	
	int time_ = 0;
	Frequency[] last_ =null;

	Map<Double, Double> gistogram = new HashMap<Double,Double>();
	
	
	
	private void convertToFrequency(double[] data, int begin,  Map<Integer, Double> ret)
	{
		boolean hasMax = false;
		int k = Math.round(settings_.SampleRate() / settings_.FFTWindowSize());

		for (int i = settings_.ProcessStart(); i < settings_.ProcessStop();++i)
		{
			double diff = data[begin + i+1] - data[begin + i];
			
			if (diff < 0 && !hasMax)
			{
				int freq = k * i;
				Double d = ret.get(freq);
				ret.put(freq, (d == null) ? new Double(data[i]) : d + data[begin + i]);
				hasMax = true;
				continue;	
			}
		
			if (diff > 0 && hasMax)
			{
				hasMax = false;
			}
		}
	}
	
	
	@Override
	public void OnSamplesReceived(double[] db) 
	{
		if (db.length < settings_.WindowSize())
		{
			double[] newdb =  new double[settings_.WindowSize()];
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
			
			if (index_ == settings_.WindowSize())
			{
				double[] windowed = AltFFT.window(buf_);
				try
				{
					int totals = 0;
					Map<Integer, Double> freqs = new TreeMap<Integer,Double>();
					
					while (totals < windowed.length)
					{						
						AltFFT.transform(windowed, totals, settings_.FFTWindowSize());
						convertToFrequency(windowed, totals, freqs);
						totals+= settings_.FFTWindowSize();
					}
						
					
					LinkedList<Frequency> flist = new LinkedList<Frequency>();
		
					for (Entry<Integer,Double> kvp: freqs.entrySet())
					{
						flist.add(new Frequency(kvp.getKey(), new BigDecimal(kvp.getValue())));
					}
					
					Collections.sort(flist, new Comparator<Frequency>() {
						@Override
						public int compare(Frequency arg0, Frequency arg1) {
							return -arg0.level.compareTo(arg1.level);
						}});
					
					
					Frequency[] data = new Frequency[Math.min(Common.Config.Instance().LevelsCount(), flist.size())];
					
					int i = 0;
					for (Frequency p: flist)
					{
						data[i++] = p;
						if (i >= Common.Config.Instance().LevelsCount()) break;
					}
					
					if (! catcher_.OnReceived(data, settings_.FFTWindowSize()))
					{
							return;	
					}
						
					index_ = settings_.WindowSize()  - settings_.OverlappedLength();
					System.arraycopy(buf_, settings_.OverlappedLength(), buf_, 0, index_);
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
	
	private Frequency[]  iterate(double[] data) throws Exception
	{
		LinkedList<Frequency> flist = new LinkedList<Frequency>();
		boolean hasMax = false;

		for (int i = settings_.ProcessStart(); i < settings_.ProcessStop();++i)
		{
			if (data[i] < 1)  continue;

			double diff = data[i+1] - data[i];
			
			if (diff < 0 && !hasMax)
			{
				int freq = ((int)(i*settings_.SampleRate() / settings_.FFTWindowSize()));
				
				freq = (int)Math.round((double)freq / 10) * 10;
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
		
		
		while (flist.size() > Common.Config.Instance().LevelsCount()) 
		{
			flist.removeLast();
		}
		Frequency[] ret = new Frequency[flist.size()];
		flist.toArray(ret);
		return  ret;
	}



}
