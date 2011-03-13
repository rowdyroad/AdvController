package UnitTests;

import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.RandomAccessFile;

import javax.sound.sampled.AudioFormat;

import Common.Dbg;
import Common.Source;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class WAVMaker implements Source.AudioReceiver
{
	AudioFormat format_;
	String path_;
	static public int id_ = 0;
	
	public WAVMaker(String path, AudioFormat format)
	{
		path_ = path;
		format_ = format;
	}
	
	@Override
	public void OnSamplesReceived(float[] db) 
	{
		try {
			Dbg.Info("Saving");
			WAVFile.CreateFile(path_+"\\"+(++id_)+".wav",format_,db);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void OnCompleted() {
		// TODO Auto-generated method stub
		
	}		
	
}
/*public class WAVMaker implements Source.AudioReceiver
{		
		final AudioFormat format_;

	
		private String dir_;
		public WAVMaker(String path, AudioFormat format) throws IOException
		{
			dir_ = path;
			format_ = format;
		}

		int count_ =  Integer.MAX_VALUE;
		boolean start_  = false;
		int k = 0;
		WAVFile file = null;
		@Override
		public void OnSamplesReceived(float[] db) 
		{
			try
			{
				for (int i  = 0;  i < db.length; ++i,++k)
				{					
					if (file != null)
						file.Write(db[i]);
					
					if (Math.abs(db[i]) <= 0.01f)
					{
						++count_;
					}
					else
					{											
						if (count_ >= format_.getSampleRate() / 2)
						{
							String[] r = (new Timestamp((long) (k / format_.getSampleRate() * 1000))).toString().split(" ");
							Dbg.Info("%d/%d - %s ", count_, k, r[1]);							
							count_ = 0;
							if (file != null)
							{
								file.Save();
							}
							
							String f = String.format(dir_+"\\%d.wav", (int)( k / format_.getSampleRate()));
							file = new WAVFile(f, format_);
						}
						else
						{
							if (--count_ < 0)
							{
								count_ = 0;
							}							
						}
					}
					
					
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				/*
				float last = 0;
				int last_index = -1;
				float max = 0.0f;
				for (int i = 0; i < db.length; ++i)
				{	
						if (Math.abs(db[i]) > Math.abs(max))
						{
							max = db[i];
						}
						
						float sign = Math.signum(db[i]);						
						if (last != sign)
						{
							if (last_index == -1)
							{
								last_index=  i;
							}
							else
							{									
 									//Dbg.Info("[%d - %d]  %d %f", last_index, i,  i - last_index, max);
									for (int j = last_index; j < i; \++j)
									{
										try {
											writeShort((short)(Math.abs(max) < 0.01 ? 0.0f : db[j]  * Short.MAX_VALUE));
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
									max = 0.0f;
									last_index = i;
							}
						}
						
						
						
						last = sign;
				}*/
		
//			}
		
		



//}
