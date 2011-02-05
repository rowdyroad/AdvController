package Streamer;

public class Distance {

	
	public  static float measure(float[][] a, float[][] b)
	{
		float total = 0;
		for (int  i =0; i< a.length; ++i)
		{
			
			double sumTop = 0;
	        double sumOne = 0;
	        double sumTwo = 0;
			for (int j = 0; j < a[i].length; ++j)
			{
				float x = a[i][j];
				float y = b[i][j];			
				 sumTop += x * y;
		         sumOne += x *x;
		         sumTwo += y * y;
			}			
			total+= sumTop / (Math.sqrt(sumOne) * Math.sqrt(sumTwo));
			
		}		
		return total / a.length;
		
	}
}
