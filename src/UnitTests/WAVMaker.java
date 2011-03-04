package UnitTests;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.sound.sampled.AudioFormat;

import Common.Dbg;
import Common.Source;

public class WAVMaker implements Source.AudioReceiver
{		
		final RandomAccessFile  file_;
		final AudioFormat format_;
		final int headerSize_;
		
		public WAVMaker(String filename, AudioFormat format) throws IOException
		{
			file_ = new RandomAccessFile(filename,"rw");
			file_.setLength(0);
			format_ = format;
			byte[] sign = {'R','I','F','F','0','0','0','0','W','A','V','E','f','m','t',' '};
			file_.write(sign);
			writeInt(16);
			writeShort((short)1); 
			writeShort((short)1);
			writeInt((int)format.getSampleRate());
			writeInt((int)(format.getSampleRate() * 2));
			writeShort((short) 2);
			writeShort((short)16);
			byte[] data = {'d','a','t','a','0','0','0','0'};		
			file_.write(data); 
			
			headerSize_ = (int) file_.length();
		}

		
		@Override
		public void OnSamplesReceived(float[] db) 
		{
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
								if (Math.abs(max) < 0.03 || (i - last_index) < 20 ) max = 0.0f;
 									Dbg.Info("[%d - %d]  %d %f", last_index, i,  i - last_index, max);
									for (int j = last_index; j < i; ++j)
									{
										try {
											writeShort((short)(max * Short.MAX_VALUE));
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
				}
			}
		
		
		public void Save() throws IOException
		{
			file_.seek(headerSize_ - 4);
			writeInt((int)(file_.length() - headerSize_));		
			file_.seek(4);
			writeInt((int) file_.length() - 8);
			file_.close();
		}
		
		private  void writeShort(short data) throws IOException {
	        short theData = (short) (((data >>> 8) & 0x00FF) | ((data << 8) & 0xFF00));
	        file_.writeShort(theData);
	    }
		
	    private  void writeInt( int data) throws IOException {
	        short theDataL = (short) ((data >>> 16) & 0x0000FFFF);
	        short theDataR = (short) (data & 0x0000FFFF);
	        short theDataLI = (short) (((theDataL >>> 8) & 0x00FF) | ((theDataL << 8) & 0xFF00));
	        short theDataRI = (short) (((theDataR >>> 8) & 0x00FF) | ((theDataR << 8) & 0xFF00));
	        int theData = ((theDataRI << 16) & 0xFFFF0000)
	                | (theDataLI & 0x0000FFFF);
	        file_.writeInt(theData);
	    }
	    
		
}
