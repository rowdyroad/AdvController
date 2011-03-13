package Common;
import java.io.IOException;

import UnitTests.WAVFile;

import Calculation.FFT_2;
import Calculation.MFCC_2;

public class Frequencier implements Source.AudioReceiver {

	public interface Catcher
	{
		public boolean OnReceived(float[][]  mfcc, long timeoffset);
		public void OnIgnore(long timeoffset);
		public void OnError();
	}
	

	private Catcher catcher_ = null;
	private Settings settings_;
	private int overlap_length_;
	private static int d =0;
	public Frequencier(Catcher catcher, Settings settings, int overlapLength, int min_frequency, int max_frequency)
	{
		catcher_ = catcher;
		settings_  = settings;
		mfcc_ = new Calculation.MFCC(
																		settings_.SampleRate(), 
																		Common.Config.Instance().FFTWindowSize(),
																		20,
																		false,
																		min_frequency,
																		max_frequency,
																		40);
		overlap_length_ = overlapLength;
		over =   new Overlapper(settings.WindowSize(), overlap_length_);
		
		mfcc_2_ = new MFCC_2(20,(float)settings.SampleRate(), 24, Common.Config.Instance().FFTWindowSize(), true,22,false);
	}
	

	private Calculation.MFCC mfcc_;
	
	private Calculation.MFCC_2 mfcc_2_;
	Overlapper over;
	
	@Override
	public void OnSamplesReceived( float[] db) 
	{		
		if (db == null)
		{  
			catcher_.OnIgnore(settings_.WindowSize());
			return;
		}		
		String str = String.format("%d.wav",++d);
		Dbg.Info("Len:%d", db.length);
		try 
		{
	//		WAVFile.CreateFile(str, settings_.Format(), db);
	//		Dbg.Info("save to:"+str);
		} 
		catch (Exception e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try 
		{
			catcher_.OnReceived(null,db.length);
			while (true)
			{
				 float[] ret = over.Overlapp(db);
				 if (ret == null) break;								 
				try 
				{
					catcher_.OnReceived(mfcc_.process(ret),overlap_length_);					
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}			
			}
			
		}
		catch (IllegalArgumentException e) 
		{
			e.printStackTrace();
		}
	}

	@Override
	public void OnCompleted() {
		// TODO Auto-generated method stub
		
	}	
}
