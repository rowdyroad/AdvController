package Common;
import java.io.IOException;

public class Frequencier implements Source.AudioReceiver {

	public interface Catcher
	{
		public boolean OnReceived(double[][]  mfcc, long timeoffset);
		public void OnError();
	}
	

	private Catcher catcher_ = null;
	private Settings settings_;
	private int overlap_length_;
	
	public Frequencier(Catcher catcher, Settings settings, int overlapLength, int min_frequency, int max_frequency)
	{
		catcher_ = catcher;
		settings_  = settings;
		mfcc_ = new util.MFCC(settings_.SampleRate(),8192, 20,false,min_frequency,max_frequency,40);
		overlap_length_ = overlapLength;
		over =   new Overlapper(65536, overlap_length_);
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
	}	
}
