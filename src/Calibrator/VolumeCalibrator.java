package Calibrator;

import Common.Dbg;
import Common.Source;
import Common.Utils;

public class VolumeCalibrator extends Calibrator
{
	public VolumeCalibrator(Source source) {
		super(source);
		Dbg.Info("Volume Calibration");
	}

	private double max_  = Double.NEGATIVE_INFINITY;

	@Override
	public void OnSamplesReceived(float[] db)
	{
		//Utils.Dbg("Received");
		float max = Float.NEGATIVE_INFINITY;
		float  db_max = 0;
		
		for (int  i = 0; i < db.length; ++i)
		{
			db_max = (Math.abs(db[i]) > Math.abs(db_max)) ? db[i] : db_max;
			
			float a =  (float) (20 * Math.log10(db[i]));
			if (Float.isNaN(a)) continue;
			
			//Utils.Dbg(a);
			max = Math.max(max,a);
		}
		Dbg.Debug("Max: %f dBMax: %.20f", max,db_max);
		
		if (!Float.isNaN(max) && ! Float.isInfinite(max) )
		{
			max_ = Math.max(max_,max);	
		}
	}

	@Override
	void OnComplete() {
		// TODO Auto-generated method stub
		Dbg.Info("Max: %.03f",max_);
	}
	
}
