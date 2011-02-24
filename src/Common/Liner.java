package Common;

public class Liner implements Source.AudioReceiver
{
	final int k = 64;
 
	
	@Override
	public void OnSamplesReceived(float[] db) 
	{		
		if (db == null) return;
		int step = db.length / 64;
		float [] d = new float[64];
		
		float t_max = 0.0f;
		int idx = 0;
		for (int i =0; i < db.length; i+=step)
		{			
			float max = 0.0f;
			int c = 0;
			for (int j = 0; j < step; ++j)
			{			
				float z = db[j+i];
				if (z < 0.0f) continue;								
				if (z == 0.0f)
				{
					++c;
					continue;
				}				
				z = (float) (100.0f + 20 * Math.log(z));		
				max+=z;
				++c;
			}			
			d[idx++] =Math.round( max / c  + 1);			
		}
		
		for (int i = 0; i < idx-1; ++i)
		{
			System.out.println(d[i] / d[i+1]);
		}		
		
		//Dbg.Info("----");		
	}

}
