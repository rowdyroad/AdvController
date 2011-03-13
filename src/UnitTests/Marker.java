package UnitTests;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;

import Common.Dbg;
import Common.Source;

public class Marker implements Source.AudioReceiver
{
	private WAVFile file_; 
	private AudioFormat format_;
	private short id_;

	public Marker(String filename, AudioFormat format, short id) throws IOException
	{
		format_ = format;
		id_ = id;
		file_ = new WAVFile(filename, format);
		Dbg.Info(id);

		
	}
 
	private int index_ = 0;

	private float[] data_ = new float[10];
	private float sum_ = 0.0f;
	private int c_ = 0;
	private int window_ = 20;
	private int space_ = 10000;
	private int x = 10000;
	private int sk =window_;
	private float last = 0.0f;
	@Override
	public void OnSamplesReceived(float[] db) 
	{		
		try 
		{
			for (int i  = 0; i < db.length; ++i, ++index_)
			{				
				
				if (index_ % 1000 == 0)
				{
					for (int j = 0; j < 100; j++)
					{
						file_.Write(db[j+i] / id_);
					}
					i+=100;
				}
				else				
					file_.Write(db[i]);
			}
		}
		catch (Exception e) 	{  e.printStackTrace(); 	}
	}

	@Override
	public void OnCompleted() 
	{
		try {
			file_.Save();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
