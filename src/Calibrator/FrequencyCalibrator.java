package Calibrator;

import Calculation.FFT;
import Common.Dbg;
import Common.Source;
import Common.Utils;

public class FrequencyCalibrator extends Calibrator
{
	private FFT fft_;
	private int min_frequency_ = Integer.MAX_VALUE;
	private int max_frequency_ = 0;
	private static   int  WINDOW_SIZE = 8192;
	private int begin_;
	private int end_;
	private float [] data_ = new float[WINDOW_SIZE];
	
	public FrequencyCalibrator(Source source) {
		super(source);
		Dbg.Info("Frequency Calibration");
		fft_ = new  FFT(FFT.FFT_POWER, WINDOW_SIZE, FFT.WND_BLACKMAN_NUTTALL);
		begin_ = (int)Math.round(Common.Config.Instance().GetProperty("f",20) * WINDOW_SIZE / (double)source_.GetSettings().SampleRate());
		end_ = (int)Math.round(Common.Config.Instance().GetProperty("F",20000) * WINDOW_SIZE / (double)source_.GetSettings().SampleRate());
	}

	@Override
	public void OnSamplesReceived(float[] db) {
		// TODO Auto-generated method stub
		int index = 0;
		while (db.length - index >= WINDOW_SIZE)
		{			
			System.arraycopy(db,index,data_, 0, WINDOW_SIZE);
			index+=WINDOW_SIZE;
			fft_.transform(data_,null);		
			double max = 0;
			int max_index = 0;
			for (int i = begin_; i < end_; ++i)
			{
				if (data_[i] > max)
				{
					max = data_[i];
					max_index = i;
				}
			}	
			
			double  db_max = Double.NEGATIVE_INFINITY;
			for (int i = 0; i < db.length; ++i)
			{
				db_max = Math.max(db_max, db[i]);
			}
			
			//if (max < 1000) continue;
			int freq = (int)(Math.round((double)source_.GetSettings().SampleRate() / data_.length * max_index));
			min_frequency_ = Math.min(min_frequency_, freq);
			max_frequency_ = Math.max(max_frequency_, freq);
			Dbg.Debug("%d - %f  / %d %d [%f]",freq,max,min_frequency_,max_frequency_,db_max);
		}
	}

	@Override
	void OnComplete() 
	{
		// TODO Auto-generated method stub
		Dbg.Info("frequency: %d - %d",min_frequency_,max_frequency_);
	}

}
