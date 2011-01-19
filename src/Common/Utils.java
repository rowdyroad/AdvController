package Common;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.sound.sampled.AudioInputStream;


public class Utils 
{
		static public Integer DeltaFrequency = 8;
		
		static public String CompletePath(String path)
		{
			if (!path.isEmpty() && ! path.endsWith(System.getProperty("file.separator")))
			{
				path += System.getProperty("file.separator");
			}
			return path;
		}
		
		static public Integer GreaterBinary(Integer a)
		{
			int pow = (int)Math.ceil(Math.log(a)/ Math.log(2));
			return (int)Math.pow(2,pow);
		}
}
