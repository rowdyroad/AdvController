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
		public boolean OnReceived(List<Frequency> frequency, long timeoffset);
		public void OnError();
	}
	

	private Catcher catcher_ = null;
	private Settings settings_;
	
	public Frequencier(Catcher catcher, Settings settings)
	{
		catcher_ = catcher;
		settings_  = settings;
	}

	private void convertToFrequency(double[] data, int begin,  List<Frequency> ret)
	{
		boolean hasMax = false;
		double k = (double)settings_.SampleRate() / settings_.WindowSize();
		
		for (int i = settings_.ProcessStart(); i < settings_.ProcessStop();++i)
		{
			double diff = data[begin + i + 1] - data[begin + i];
			
			if (diff < 0 && !hasMax)
			{
				int freq = (int)(Math.round(k * i / 8) * 8) ;
				Frequency f = new Frequency(freq, data[begin+i]);
				
				int index = ret.indexOf(f);
				if (index != -1)
				{
					ret.get(index).level = Math.max(ret.get(index).level,  f.level);
				}
				else
				{
					ret.add(f);
				}
				hasMax = true;
				continue;	
			}
		
			if (diff > 0 && hasMax)
			{
				hasMax = false;
			}
		}
		Collections.sort(ret, new Comparator<Frequency>() {
			@Override
			public int compare(Frequency arg0, Frequency arg1) {
				return -arg0.level.compareTo(arg1.level);
			}});		
		
			while (ret.size() > Common.Config.Instance().LevelsCount() )
			{
				((LinkedList<Frequency>)ret).removeLast();
			}
	}
	
	
	@Override
	public void OnSamplesReceived(double[] db) 
	{
		List<Frequency> freqs = new LinkedList<Frequency>();		
		AltFFT.window(db);

		double[] wdb = new double[4096];
			
		for (int i =0; i < db.length; i+=4096)
		{
			AltFFT.transform(db);
			convertToFrequency(db, 0, freqs);
		}
		
		if (! catcher_.OnReceived(freqs, settings_.WindowSize()  /  Common.Config.Instance().OverlappedCoef()))
		{
				return;	
		}

	}	
}
