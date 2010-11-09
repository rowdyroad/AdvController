package Common;
import java.util.Date;

import javax.sound.sampled.AudioInputStream;


public class Utils 
{
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
		static public void DbgFrq(Frequency[] frequency)
		{
			for (int i =0; i < frequency.length; ++i)
			{
				Utils.Dbg("%d %f", frequency[i].frequency, frequency[i].level);
			}
			Utils.Dbg("");
		}
		
		@SuppressWarnings("deprecation")
		static public String Time(long timestamp)
		{
			Date d = new Date(timestamp);
			return String.format("%02d:%02d:%02d", d.getHours(), d.getMinutes(), d.getSeconds());
		}
		
		static public void Dbg(String format, Object... args)
		{
			System.out.printf(format+"\n",args);
		}
		
		
}
