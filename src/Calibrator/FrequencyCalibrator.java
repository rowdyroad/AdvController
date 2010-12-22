package Calibrator;

import util.FFT;
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
	private double [] data_ = new double[WINDOW_SIZE];
	
	public FrequencyCalibrator(Source source) {
		super(source);
		Utils.Dbg("Frequency Calibration");
		fft_ = new  FFT(FFT.FFT_POWER, WINDOW_SIZE, FFT.WND_BLACKMAN_NUTTALL);
		begin_ = (int)Math.round(20 * WINDOW_SIZE / (double)source_.GetSettings().SampleRate());
		end_ = (int)Math.round(20000 * WINDOW_SIZE / (double)source_.GetSettings().SampleRate());
	}

	@Override
	public void OnSamplesReceived(double[] db) {
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
			
			if (max < 10000) continue;
			int freq = (int)(Math.round((double)source_.GetSettings().SampleRate() / data_.length * max_index));
			
			Utils.Dbg("%d - %f",freq,max);
			min_frequency_ = Math.min(min_frequency_, freq);
			max_frequency_ = Math.max(max_frequency_, freq);
			
		}
	}

	@Override
	void OnComplete() {
		// TODO Auto-generated method stub
		Utils.Dbg("frequency: %d - %d",min_frequency_,max_frequency_);
	}

}
