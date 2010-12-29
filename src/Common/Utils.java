package Common;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.sound.sampled.AudioInputStream;


public class Utils 
{
		static public Integer DeltaFrequency = 8;
		static public void ShowStreamInfo(AudioInputStream ret)
		{
			System.out.println("Channels:" + ret.getFormat().getChannels());
			System.out.println("FrameRate:" + ret.getFormat().getFrameRate());
			System.out.println("FrameSize:" + ret.getFormat().getFrameSize());
			System.out.println("SampleRate:" + ret.getFormat().getSampleRate());
			System.out.println("SizeInBits:" + ret.getFormat().getSampleSizeInBits());
			System.out.println("IsEndian:" + ret.getFormat().getEncoding());
			System.out.println("IsEndian:" + ret.getFormat().isBigEndian());
		}
		
		static public void Dbg(Object obj)
		{
			System.out.println(obj);
		}
		static public void DbgAr(double[] d)
		{
			for (int i = 0 ; i< d.length; ++i)
			{
				System.out.print(d[i]);
				System.out.print("\t");
			}
			System.out.println();
		}
		static public void DbgFrq(List<Frequency> frequency)
		{
			for (Frequency f: frequency)
			{
				Utils.Dbg("%d; %f;", f.frequency, f.level);
			}
		}
		
		@SuppressWarnings("deprecation")
		static public String Time(long timestamp)
		{
			Date d = new Date(timestamp);
			return String.format("%02d:%02d:%02d", d.getHours(), d.getMinutes(), d.getSeconds());
		}
		
		static private 			DateFormat date_format_  = new SimpleDateFormat("[ dd/MM/yyyy HH:mm:ss.SSS ] ");

		
		static public void Dbg(String format, Object... args)
		{
			System.out.printf(date_format_.format(new Date())+" "+format+"\n",args);
		}
		
		
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
