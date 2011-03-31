package Splitter;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

class Reader implements Runnable
{
	private TargetDataLine line_;
	private String filename_;
	
	public Reader(TargetDataLine line, String filename)
	{
		line_ = line;
		filename_ = filename;
	}
	@Override
	public void run() 
	{
		try {
			AudioSystem.write( new AudioInputStream(line_), AudioFileFormat.Type.WAVE,new File(filename_));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
