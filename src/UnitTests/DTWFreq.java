package UnitTests;

import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import Common.Dbg;

public class DTWFreq {
	

	public DTWFreq()
	{
		TreeMap<Integer, Integer> a = new TreeMap<Integer,Integer>();
		TreeMap<Integer, Integer> b = new TreeMap<Integer,Integer>();
		

			a.put(10,100);
			b.put(100,100);			
		
		Dbg.Info(this.distance(a,b));
		
	}
	
    private float distance2Similarity(float x) 
    {
        return 1.0f - (x / (1 + x));
    }
    
    public  float distance(TreeMap<Integer, Integer> a, TreeMap<Integer, Integer> b)
	 {		 
		 int sum = 0;		 
		 int c = 0;		 
		 for (Entry<Integer, Integer> kvp : a.entrySet())
		 {
			 int a_w = kvp.getValue();			 
			 int b_w =  (b.containsKey(kvp.getKey())) ? b.get(kvp.getKey()) : 0;		
			 ++c;
			 sum += Math.pow(a_w - b_w,2);			 
		 }
		 
		 for (Entry<Integer, Integer> kvp : b.entrySet())
		 {
			 if (!a.containsKey(kvp.getKey()))
			 {						
				 sum+= Math.pow(kvp.getValue(),2);
				 ++c;
			 }
		 }		 
		 return (float) Math.sqrt( (float)sum / c);			
	 }
    
    public float measure(List<TreeMap<Integer, Integer>>  a1, List<TreeMap<Integer, Integer>> b1)
    {    	 
        float diff = 0;
        for (int i = 0; i < a1.size(); ++i) 
        {        	                        	
            	final TreeMap<Integer, Integer> a= a1.get(i);
            	final TreeMap<Integer, Integer> b = b1.get(i);
            	diff += distance(a,b);            	
         }        
        return   diff / a1.size();
    }

}
