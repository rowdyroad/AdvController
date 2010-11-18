package UnitTests;

import Common.AltFFT;
import Common.FFT;
import Common.Utils;

public class Main {
	
	static double[] UT_FFT()
	{
		FFT fft = new FFT(FFT.FFT_MAGNITUDE, 256, FFT.WND_NONE);
		
		double[] r = new double[256];
		double[] m = new double[256];
		for (int i=0;i<256;i++)
		{
			r[i] = Math.sin(i);
			
		}
		long t = System.currentTimeMillis();
		double[] aret = AltFFT.transform(r);
		Utils.Dbg("altFFT:%d", System.currentTimeMillis() - t );
			
		 t = System.currentTimeMillis();
		double[] ret = fft.transform(r,m);
		Utils.Dbg("FFT:%d", System.currentTimeMillis() - t);
		
		int cur = 0, acur = 0;
		for (int i=0;i<ret.length;++i)
		{
			Utils.Dbg("re[%d]=%f\t\t\t%f\t",i,ret[i],aret[i]);
		}
		
		return ret;
	}
	
	static void UT_FFTMAXLEVELS(double[] data)
	{
		
		/*re[213]=5,287231			3,812152	
		re[214]=24,743402			17,841068	
		re[215]=45,359218			32,705563	
		re[216]=37,435467			26,992529	
		re[217]=13,355486			9,629663	
		re[218]=1,556568	
		*/
		
		boolean hasMax = false; 
		for (int i = 0; i < data.length - 1;++i)
		{
			if (data[i] < 1) continue;
			
			double diff = data[i+1] - data[i];
			
			if (diff < 0 && !hasMax)
			{
				Utils.Dbg("FR:%d M:%f", i, data[i]);		
				hasMax = true;
				continue;
			}
			
			if (diff > 0 && hasMax)
			{
				hasMax = false;
			}
		}
	}

	public static void main (String args [])
	{
	 	    Utils.Dbg("FFT COMPARE");
	 	    //double[] data = UT_FFT();
			Utils.Dbg("===========");
	 	    Utils.Dbg("FFT MAX LEVELS");
			//UT_FFTMAXLEVELS(data);
			Utils.Dbg("===========");
			
			double k = 12053;
			
			
	}
}
