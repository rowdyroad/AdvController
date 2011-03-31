package Splitter;

import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import Common.Dbg;

public class PrinterTest {

	/**
	 * @param args
	 * @throws LineUnavailableException 
	 */
	
	
	public static void main(String[] args)  
	{
		
		AudioFormat format = new AudioFormat(48000,16,2,true,false);
	
		List<LineInfo> r = CardSelector.List(format);
		
		for (LineInfo l : r)
		{
			Dbg.Info(l);
		}
		/*;				
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		 TargetDataLine line = null;
			try 
			{
				line = (TargetDataLine)AudioSystem.getLine(info);				
				line.open(format);
				line.start();
			} catch (LineUnavailableException e) 
			{
				e.printStackTrace();
				System.exit(1);
			}
			long time = System.currentTimeMillis() + 20 * 1000000;
			Printer p = new Printer(format);
			byte[] b = new byte[format.getFrameSize() * (int)format.getSampleRate()];
			while (time > System.currentTimeMillis())
			{
				int len = line.read(b, 0,b.length);				
				if (len == -1) break;				
				byte [] c  = new byte[len];				
				System.arraycopy(b,0,c,0,len);
				p.Detect(c);
			}	*/
	}

}
