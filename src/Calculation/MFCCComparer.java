package Calculation;

import Common.Dbg;

public class MFCCComparer {

	
	static public float compare(float[][] a, float [][] b)
	{
		int len = Math.min(a.length,b.length);
		float total = 0;
		for (int i = 0; i < len; ++i)
		{
			int len1 = Math.min(a[i].length, b[i].length);
			float d = 0;
			for (int j = 0; j <len1; ++j)
			{
				final float diff = a[i][j] - b[i][j];
				d+=diff * diff;
			}
			total += Math.sqrt(d / len1);			
		}		
		return total / len;
	}
	
	
    private static float getPearsonCorrelation(float[] scores1,float[] scores2){
        float result = 0;
        float sum_sq_x = 0;
        float sum_sq_y = 0;
        float sum_coproduct = 0;
        float mean_x = scores1[0];
        float mean_y = scores2[0];
        
        for(int i=2; i < scores1.length+1; ++i)
        {
            final float sweep =Float.valueOf(i-1)/i;
            final float delta_x = scores1[i-1] - mean_x;
            final float delta_y = scores2[i-1] - mean_y;
            
         //   Dbg.Info("s:%f dx:%f dy:%f [ %f - %f]",sweep,delta_x,delta_y, scores2[i-1], mean_y);
            sum_sq_x += delta_x * delta_x * sweep;
            sum_sq_y += delta_y * delta_y * sweep;
            sum_coproduct += delta_x * delta_y * sweep;
            mean_x += delta_x / i;
            mean_y += delta_y / i;
        }
        float pop_sd_x = (float) Math.sqrt(sum_sq_x/scores1.length);
        float pop_sd_y = (float) Math.sqrt(sum_sq_y/scores1.length);
        float cov_x_y = sum_coproduct / scores1.length;
        result = cov_x_y / (pop_sd_x*pop_sd_y);
        return result;
    }
    
	
	static public float pearson(float[][] a, float[][] b)
	{		
		float total  = 0;
		int len = Math.min(a.length, b.length);
		for (int k =0 ;k < len; ++k)
		{			
			final float f = getPearsonCorrelation(a[k],b[k]);
			total+=f;
	    }		
		return total / len;
		
	}
}
