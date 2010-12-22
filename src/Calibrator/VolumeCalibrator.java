package Calibrator;

import java.io.IOException;

import Common.Source;
import Common.Utils;

public class VolumeCalibrator extends Calibrator
{
	public VolumeCalibrator(Source source) {
		super(source);
		Utils.Dbg("Volume Calibration");

	}

	private double max_  = Double.NEGATIVE_INFINITY;

	@Override
	public void OnSamplesReceived(double[] db)
	{
		//Utils.Dbg("Received");
		double max = Double.NEGATIVE_INFINITY;
		for (int  i = 0; i < db.length; ++i)
		{
			double a =  20 * Math.log10(db[i]);
			if (Double.isNaN(a)) continue;
			
			//Utils.Dbg(a);
			max = Math.max(max,a);
		}
		//Utils.Dbg(max);
		
		if (!Double.isNaN(max) && ! Double.isInfinite(max) )
		{
			max_ = Math.max(max_,max);	
		}		
	}

	@Override
	void OnComplete() {
		// TODO Auto-generated method stub
		Utils.Dbg("max: %.03f",max_);
	}
	
}
