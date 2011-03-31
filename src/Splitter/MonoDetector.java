package Splitter;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;

import UnitTests.WAVFile;


public class MonoDetector implements IDetector 
{	
	class Worker implements Runnable
	{		
		private byte[] buffer_;
		WAVFile  file_;
		String  filename_;
		int index_;
		MonoDetector detector_;
		@Override
		public void run() 
		{	
			try
			{
						file_.File().write(buffer_);
						file_.Save();
						detector_.channel_.LoadProcess(index_, filename_);		
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
		}
				
		 public Worker(int index,  byte[] buffer, MonoDetector detector)
		 {
			 detector_ = detector;
			 buffer_ = buffer;
			 index_ = index;
			 try {
				filename_ = detector.dir_+"\\"+detector.prefix_+"_"+index+".wav";
				file_ = new WAVFile(filename_,format_);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }		
	}
	
	AudioFormat format_;
	String dir_;
	String prefix_;
	ILoader channel_ ;
	volatile int index_ = 1;
	
	public MonoDetector(AudioFormat format, String dir,String prefix,  ILoader channel) throws Exception
	{
		if (format.getChannels() != 1)
		{
			throw new Exception("There is not mono signal");
		}
		
		prefix_ = prefix;
		format_ = format;
		dir_ = dir;
		channel_ = channel;	
	}

	@Override
	public void Detect(byte[] buffer) {
		// TODO Auto-generated method stub
		new Thread(new Worker(index_++, buffer, this)).start();
	}
}
