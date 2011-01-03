package Streamer;

public class DTW {
	
	private  float[][] data_;
	public DTW(int size)
	{
		data_ = new float[size][size];
	}

    private float distance2Similarity(float x) 
    {
        return 1.0f - (x / (1 + x));
    }
    
    public float measure(float[][] doublesOne, float[][] doublesTwo)
    {
        int i, j;       
        for (i = 0; i < doublesOne.length; ++i) 
        {        	
            for (j = 0; j < doublesTwo.length; ++j) 
            {
            	final float[] a = doublesOne[i];
            	final float[] b = doublesTwo[j];
            	float diff = 0;
            	float adiff = 0;
                for (int k = 0; k < a.length; k++) 
                {
                	adiff = a[k] - b[k];
                    diff +=  adiff * adiff;
                }
                
                data_[i][j]  = diff;    
                if (j ==0 && i > 0)
                {
                	data_[i][0] = data_[i][0] + data_[i - 1][0];
                }
                else
                if ( j > 0)
                {
                	if (i > 0)
                	{
                    	data_[i][j] = data_[i][j] +Math.min(data_[i - 1][j - 1], Math.min(data_[i - 1][j], data_[i][j - 1]));
                	}
                	else
                	{
                    	data_[0][j] = data_[0][j] + data_[0][j - 1];
                	}
                }
            }
        }

        i = doublesOne.length - 1;
        j = doublesTwo.length - 1;
        int k = 1;
        double dist = data_[i][j];
        while (i + j > 2)
        {
            if (i == 0) 
            {
                --j;
            } 
            else 
            if (j == 0) 
            {
                --i;
            } 
            else 
            {                        
            	final float ij11 = data_[i-1][j-1];
            	final float ij10 = data_[i-1][j];
            	final float ij01 = data_[i][j-1];            
            	
            	if (ij10 < ij01)
            	{
            		--i;
            		if (ij11 < ij10)
            		{
            			 --j;
            		}
            	}
            	else
            	{
            		--j;
            		
            		if (ij11 < ij01)
            		{
            			--i;
            		}
            	}            	
            }
            ++k;
        }
        return distance2Similarity((float)Math.sqrt(dist) / k);
    }

}
