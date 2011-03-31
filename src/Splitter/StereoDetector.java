package Splitter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;

import UnitTests.WAVFile;

import Common.Dbg;

public class StereoDetector  implements IDetector
{	
	class Worker implements Runnable
	{		
		private byte[] buffer_;
		WAVFile  left_file_;
		WAVFile right_file_;
		String left_filename_;
		String right_filename_;
		int index_;
		StereoDetector detector_;
		@Override
		public void run() 
		{	
			final int channel_sample = detector_.format_.getSampleSizeInBits() / 8;
			try 
			{ 
				byte[] left = new byte[buffer_.length/2];
				byte[] right = new byte[buffer_.length/2];
				for (int i = 0,c = 0; i < buffer_.length - detector_.format_.getFrameSize(); i+=detector_.format_.getFrameSize(),c+=channel_sample)
				{							
						if (detector_.left_!=null)
						{
							for (int j = 0; j < channel_sample; ++j)
							{
								left[c + j] = buffer_[i + j];
							}					
						}
						
						if (detector_.right_!=null)
						{
							for (int j = 0; j < channel_sample; ++j)
							{
								right[c + j] = buffer_[i + j + channel_sample];
							}
						}
				}
				
				if (detector_.left_!=null)
				{
					try
					{
						left_file_.File().write(left);
						left_file_.Save();
						detector_.left_.LoadProcess(index, left_filename_);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}					
				}
				if (detector_.right_ != null)
				{
					try
					{
						right_file_.File().write(right);
						right_file_.Save();
						detector_.right_.LoadProcess(index, right_filename_);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}	
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
		}
				
		 public Worker(int index,  byte[] buffer, StereoDetector detector)
		 {
			 detector_ = detector;
			 buffer_ = buffer;
			 index_ = index;
			 try 
			 {
				left_filename_ = detector.dir_+"\\"+detector.prefix_+"_left_"+index+".wav";
				right_filename_ =  detector.dir_+"\\"+detector.prefix_+"_right_"+index+".wav";
				left_file_ = new WAVFile(left_filename_,format_);
				right_file_ = new WAVFile(right_filename_,format_);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }		
	}
	
	private AudioFormat format_;	
	private volatile int index  = 1;
	private String dir_;
	private String prefix_;
	private ILoader left_ = null;
	private ILoader right_ = null;
	
	public StereoDetector(AudioFormat format, String dir, String prefix, ILoader right, ILoader left) throws Exception
	{
		if (format.getChannels() != 2) 
		{
			throw new Exception("Need stereo signal");
		}
		
		prefix_ = prefix;
		format_ = format;		
		dir_ =  dir;
		left_ = left;
		right_ = right;
		if (!new File(dir_).isDirectory())
		{
			throw new FileNotFoundException("No such directory " + dir_);
		}
	}
	
	public void Detect(byte[] buffer)
	{
		Dbg.Info("[%d] For detect %s bytes", index, buffer.length);		
		new Thread(new Worker(index++,  buffer,  this)).start();
	}	
}
