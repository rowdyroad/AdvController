package Splitter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

public class Resulter implements DetectorLoader.Resulter {

	private String filename_;
	
	public Resulter(String filename) throws IOException
	{
		filename_ = filename;		
		File f = new File(filename);
		if (!f.exists())
		{
			f.createNewFile();
		}
	}
	@Override
	public void OnDetected(int id, float weight) 
	{		
		RandomAccessFile f;
		try {
			f = new RandomAccessFile(filename_,"rw");
			FileLock locker = f.getChannel().lock(0, f.length(), false);		
			f.seek(f.length());
			f.writeBytes(String.format("%d,%d,%d|",System.currentTimeMillis() / 1000, id, (int)Math.round(weight)));			
			locker.release();
			f.close();
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
