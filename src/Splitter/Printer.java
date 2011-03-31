package Splitter;

import java.io.IOException;
import java.io.PrintStream;

import javax.sound.sampled.AudioFormat;
public class Printer  implements IDetector 
{
		AudioFormat format_;
		public Printer(AudioFormat format) 
		{
			format_ = format;		
		}
		@Override
		public void Detect(byte[] buffer) 
		{
			final int channel_sample = format_.getSampleSizeInBits() / 8;
				byte[] left = new byte[buffer.length/2];
				byte[] right = new byte[buffer.length/2];
				for (int i = 0,c = 0; i < buffer.length - format_.getFrameSize(); i+=format_.getFrameSize(),c+=channel_sample)
				{							
							for (int j = 0; j < channel_sample; ++j)
							{
								left[c + j] = buffer[i + j];
							}													
							for (int j = 0; j < channel_sample; ++j)
							{
								right[c + j] = buffer[i + j + channel_sample];
							}
				}
				try {
					System.out.write(left);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					System.err.write(right);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
		}
	
}
