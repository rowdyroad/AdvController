package Sourcer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import Common.Dbg;
import Common.Source;

public class Main {

	private AudioFormat format_;
	
	public Main(String[] args)
	{
		format_ = new AudioFormat(48000,2,16,true,false);			
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format_);
		TargetDataLine line = null;
			try {
				line = (TargetDataLine)AudioSystem.getLine(info);				
				line.open(format_);
				line.start();
			} catch (LineUnavailableException e) 
			{
				e.printStackTrace();
				System.exit(1);
			}			
						
			byte[] buf = new byte[(int) (format_.getSampleRate() * format_.getFrameSize())];
			final int channel_frame_size = format_.getFrameSize() / format_.getChannels();
			 final int  off  = (args.length>= 4 && args[3] == "right") ? channel_frame_size : 0;
					
			while (line.isOpen())
			{
				final int len = line.read(buf,0,buf.length);
				if (format_.getChannels() == 2)
				{
 							for (int i = 0; i < len; i+=format_.getFrameSize())
							{
								System.out.write(buf, i + off, channel_frame_size);
							}
						//	System.out.flush();
				}
				else
				{	
					System.out.write(buf,0, len);				
					//System.out.flush();
				}
			}		
	}
	public static void main(String[] args) 
	{
		new Main(args);
	}

}
