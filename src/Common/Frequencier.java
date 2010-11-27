package Common;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class Frequencier implements Source.AudioReceiver {

	public interface Catcher
	{
		public boolean OnReceived(Vector<double[]>  mfcc, long timeoffset);
		public void OnError();
	}
	

	private Catcher catcher_ = null;
	private Settings settings_;
	private FFT fft_;
	private int overlap_length_;
	
	public Frequencier(Catcher catcher, Settings settings, int overlapLength)
	{
		catcher_ = catcher;
		settings_  = settings;
		fft_  = new FFT(4096);
		mfcc_ = new util.MFCC(settings_.SampleRate(),8192, 20,false,20,10000,40);
		overlap_length_ = overlapLength;
		over =   new Overlapper(65536, overlap_length_);
	}

	private void convertToFrequency(double[] data, int begin,  List<Frequency> ret)
	{
		boolean hasMax = false;
		//double k = (double)settings_.SampleRate() / settings_.WindowSize();
		
		//int b_idx = settings_.ProcessStart(); 
		//int e_idx = settings_.ProcessStart(); 
		
		double k = (double)settings_.SampleRate() / data.length;
		
		int b_idx = (int)Math.round((double)Common.Config.Instance().MinFrequency() * data.length / settings_.SampleRate());
		int e_idx = (int)Math.round((double)Common.Config.Instance().MaxFrequency() * data.length / settings_.SampleRate());
		
		for (int i = b_idx;  i < e_idx; ++i)
		{
			double diff = data[begin + i + 1] - data[begin + i];
			
			if (diff < 0 && !hasMax)
			{
				int freq = (int)(Math.round(k*i));
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
	
	int size = 8192;
	double[] cache = new double[8192];

	private util.MFCC mfcc_;
	Overlapper over;
	
	@Override
	public void OnSamplesReceived(double[] db) 
	{
		try 
		{
			while (true)
			{
				double[] ret = over.Overlapp(db);
				if (ret == null) break;
				catcher_.OnReceived(mfcc_.process(ret),overlap_length_);				
			}
		} catch(IOException e)
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}

		
		
		
		
		/*List<Frequency> freqs = new LinkedList<Frequency>();	
		double[] data = new double[4096];
		int total = 0;
		while (total <db.length)
		{
			System.arraycopy(cache, 0, data, 0, 3072);
			System.arraycopy(db, total, data, 3072,  1024);
			System.arraycopy(data, 1024, cache, 0, 3072);		
			fft_.Transform(data);
			convertToFrequency(data, 0, freqs);
			total+=1024;
			if (total % 4096 == 0)
			{
				if (! catcher_.OnReceived(freqs, 4096))
				{
					return;	
				}
				freqs = new LinkedList<Frequency>();	
			}
		}*/
		
/*		for(Frequency f:freqs)
		{
			Utils.Dbg("%d - %f",f.frequency, f.level);
		}
		Utils.Dbg("");*/
		
		

	}	
}
