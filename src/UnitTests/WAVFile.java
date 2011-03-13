package UnitTests;

import java.io.IOException;
import java.io.RandomAccessFile;
import javax.sound.sampled.AudioFormat;

public class WAVFile
{			
	private  RandomAccessFile  file_;
	private  int headerSize_;
	private final AudioFormat format_;
	private String filename_;
	private boolean opened_ = true;

	public WAVFile(String filename, AudioFormat format) throws IOException
	{
		filename_ = filename;
		format_ = format;
		file_ = new RandomAccessFile(filename_,"rw");
		file_.setLength(0);
		byte[] sign = {'R','I','F','F','0','0','0','0','W','A','V','E','f','m','t',' '};
		file_.write(sign);
		writeInt(16);
		writeShort((short)1); 
		writeShort((short)1);
		writeInt((int)format_.getSampleRate());
		writeInt((int)(format_.getSampleRate() * 2));
		writeShort((short) 2);
		writeShort((short)16);
		byte[] data = {'d','a','t','a','0','0','0','0'};		
		file_.write(data); 			
		headerSize_ = (int) file_.length();
	}

	public static void CreateFile(String filename, AudioFormat audio, float[] data) throws Exception
	{
		WAVFile f = new WAVFile(filename, audio);
		for (int i = 0;i < data.length; ++i)
		{
			f.Write(data[i]);
		}
		f.Save();
	}
	public void Write(float db) throws Exception
	{
			if (!opened_) throw new Exception();			
			writeShort((short)(db * Short.MAX_VALUE));
	}

	public void Save() throws Exception
	{
		if (!opened_) throw new Exception();
		file_.seek(headerSize_ - 4); 
		writeInt((int)(file_.length() - headerSize_));		
		file_.seek(4);
		writeInt((int) file_.length() - 8);
		file_.close();
		opened_= false;
		
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