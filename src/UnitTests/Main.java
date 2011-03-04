package UnitTests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import Streamer.DTW;


import Calculation.FFT;
import Calculation.math.Matrix;
import Capturer.Capturer;
import Common.Dbg;
import Common.FingerPrint;
import Common.Overlapper;
import Common.Settings;
import Common.Source;
import Common.Utils;

public class Main {
	
	static double[] UT_FFT()
	{
		//FFT fft = new FFT(FFT.FFT_MAGNITUDE, 256, FFT.WND_NONE);
		
		double[] r = new double[256];
		double[] m = new double[256];
		for (int i=0;i<256;i++)
		{
			r[i] = Math.sin(i);
			
		}
		/*long t = System.currentTimeMillis();
		double[] aret = AltFFT.transform(r);
		Utils.Dbg("altFFT:%d", System.currentTimeMillis() - t );
			
		 t = System.currentTimeMillis();
		double[] ret = fft.transform(r,m);
		Utils.Dbg("FFT:%d", System.currentTimeMillis() - t);
		
		int cur = 0, acur = 0;
		for (int i=0;i<ret.length;++i)
		{
			Utils.Dbg("re[%d]=%f\t\t\t%f\t",i,ret[i],aret[i]);
		}
		
		return ret;*/
		
		return null;
	}
	
	static void UT_FFTMAXLEVELS(double[] data)
	{
		
		/*re[213]=5,287231			3,812152	
		re[214]=24,743402			17,841068	
		re[215]=45,359218			32,705563	
		re[216]=37,435467			26,992529	
		re[217]=13,355486			9,629663	
		re[218]=1,556568	
		*/
		
		boolean hasMax = false; 
		for (int i = 0; i < data.length - 1;++i)
		{
			if (data[i] < 1) continue;
			
			double diff = data[i+1] - data[i];
			
			if (diff < 0 && !hasMax)
			{
				Dbg.Debug("FR:%d M:%f", i, data[i]);		
				hasMax = true;
				continue;
			}
			
			if (diff > 0 && hasMax)
			{
				hasMax = false;
			}
		}
	}

	static 	int overlapped_cache_len_ = 0;
	static int[] overlapped_cache_ = new int[20];
	static int overlapped_length_ = 5;
	
	static int readChunkSize_ = 8192;
	static int cache_len_ = 0;
	static byte[] cache_ = new byte[8192];
	static InputStream stream_;
	static int readed = 0;
	
	static public Boolean Read()
	{
		byte[] b = new byte[readChunkSize_];		
		while (true)
		{
			try
			{
				int ret = stream_.read(b);
				if (ret == -1 ) 
				{
					if (cache_len_> 0) 
					{
						z(cache_, cache_len_);
					}
					return false;
				}
				
				readed+=ret;
				if (ret < readChunkSize_ || cache_len_  > 0)
				{
					int len2cache = Math.min(readChunkSize_ - cache_len_, ret);
					System.arraycopy(b, 0, cache_, cache_len_, len2cache);
					cache_len_+= len2cache;
					if (cache_len_ == readChunkSize_)
					{
						z(cache_,cache_len_);
						if (len2cache < ret)
						{
							cache_len_ = ret - len2cache;
							System.arraycopy(b,len2cache, cache_,0, cache_len_);
						}
						else
						{
							break;
						}
					}
				}
				else
				{
					z(b,ret);
					return true;
				}
			}
			catch (IOException e)
			{
				if (cache_len_ > 0)
				{
					z(cache_,cache_len_);
				}
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	static OutputStream out_;
	
	static private void z(byte[] data, int len)
	{
		try {
			Dbg.Debug("Write:%d",len);
			out_.write(data,0,len);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static private void process(int[] buf, int len)
	{
		if (buf.length != len) 
		{
			Dbg.Debug("buflen != len:%d %d", buf.length, len);
				return;
		}
		int buf_pos = 0;
		while (buf_pos < len)
		{			
			int add_len = len - overlapped_cache_len_;
			System.arraycopy(buf, buf_pos, overlapped_cache_, overlapped_cache_len_, add_len);
			overlapped_cache_len_+=add_len;
			buf_pos+=add_len;
			
			String str = new String();
			
			for (int i = 0; i < overlapped_cache_len_; ++i )
			{
				str+=String.format("%d ", overlapped_cache_[i]);
			}
			Dbg.Debug(str);
			System.arraycopy(overlapped_cache_, overlapped_length_, overlapped_cache_, 0, overlapped_cache_len_ - overlapped_length_);
			overlapped_cache_len_-=overlapped_length_;
				
			
		}
	}
	
	
	public static void testOver()
	{
	/*	double[]  d = {1,1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9,0,1,2,4,5,6,7,9,1,2,3,4,6,7,8,9,0};
		
		Overlapper ov = new Overlapper(10, 4);
		
		while (true)
		{
			double [] ret = ov.Overlapp(d);
			if (ret == null) break;
			
			for (int i =0 ;i < ret.length;++i)
			{
				System.out.printf("%.00f ",ret[i]);
			}
			System.out.println();
		}
		
		
		double[]  d1 = {9,8,7,6,5,4,3,2,1,2,2,3,4,4,5,6,7,8,9,0,1,2,4,5,6,7,9,1,2,3,4,6,7,8,9,0};
		while (true)
		{
			double [] ret = ov.Overlapp(d1);
			if (ret == null) break;
			
			for (int i =0 ;i < ret.length;++i)
			{
				System.out.printf("%.00f ",ret[i]);
			}
			  System.out.println();
		}
		*/
	}
	
	
	static private int[] id = new int[100000];
	
	public static void Memory(int z)
	{
		if (id[z] > z)
		{
			
		}
	}
	public static void Circle(int z)
	{
		for (int i = 0;  i < 100000; ++i)
		{
			if (i > z)
			{		
				break;
			}
		}
	}
	

	
	

    private void writeRiff(RandomAccessFile file, short[] data, int numBytes) throws IOException {
        byte[] theData = new byte[numBytes];
        int yc = 0;
        for (int y = 0; y < numBytes; y = y + 2) {
            theData[y] = (byte) (data[yc] & 0x00FF);
            theData[y + 1] = (byte) ((data[yc++] >>> 8) & 0x00FF);
        }
        file.write(theData, 0, numBytes);
    }


    public static float last = Float.NEGATIVE_INFINITY;
    
	public static void main (String args []) throws Exception
	{				
		/*AudioInputStream w = AudioSystem.getAudioInputStream(new File("d:\\work\\test_with_madgl.wav"));
		AudioInputStream wo = AudioSystem.getAudioInputStream(new File("d:\\work\\test_wo_madgl.wav"));
		AudioInputStream test = AudioSystem.getAudioInputStream(new File("d:\\temp\\ptrn\\test.wav"));
		AudioInputStream lz_rec = AudioSystem.getAudioInputStream(new File("d:\\temp\\ptrn\\z\\lz_rec.wav"));
		AudioInputStream lz = AudioSystem.getAudioInputStream(new File("d:\\temp\\ptrn\\z\\lz.wav"));*/
		
		 /*
		Capturer capt = new Capturer("d:\\work\\rec\\2\\e_no.wav", "1","left",20,20000,0.001f,100);
		FingerPrint fp = capt.Process();

		Capturer capt1 = new Capturer("d:\\work\\rec\\2\\e_rec.wav", "1","left",20,20000,0.001f,100);
		FingerPrint fp1 = capt1.Process();

		
		
		for (int c = 0; c < Math.min(fp.Frames(), fp1.Frames()); ++c)
		{
			final float a[][] = fp.Get(c);
			final float b[][] = fp1.Get(c);
			
			float tt = 0.0f;
			for (int j = 0; j < 19; ++j)
			{
				float t = 0.0f;
				for (int i = 0 ;i < a.length; ++i)
				{				
					t += Math.pow(a[i][j] - b[i][j],2);
				}
				tt+=Math.sqrt(t / a.length);				
			}
			
			
			
			Dbg.Info("Mean: %f   DTW:%f",  tt / 19, DTW.measure(15, a,b));
			
		}*/
		
		
		final AudioInputStream pi = AudioSystem.getAudioInputStream(new File("d:\\work\\148.wav"));
		Source s = new Source(pi, new Settings(pi.getFormat()), 100, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
		
	
		WAVMaker m = new WAVMaker("d:\\work\\res.wav",pi.getFormat());
		
		s.RegisterAudioReceiver(Source.Channel.LEFT_CHANNEL, m);		
		s.Process();
		
		m.Save();
		
		//Limits.Process(a, b);
		
		
	
/*			
			
			for (int j = 0; j < b.data_.size(); ++j)			
			{
				int r = a.rms(a.data_.get(i), b.data_.get(j));
				
				if (r <= 5 )
				{
					Dbg.Info("%d / %d", i, j);
					a.print(a.data_.get(i));				 
					a.print(b.data_.get(j));				 				
					Dbg.Info("rms:%d\n",r);
				}
			}		

//			Dbg.Info("min: %d max:%d",min,max);
		}
*/
		
		
				/*
		for (int i = 0; i < 1000; ++i)
		{
			Memory(i);
			Circle(i);
		}
			*/
		//testOver();
		/*Vector<Double> d = new Vector<Double>();
		
		d.setSize(10000000);
		for (int i = 0; i < d.size(); ++i)
		{
			d.set(i, new Double(0));
		}
		long time = System.currentTimeMillis();
		
		double sum = 0;
		for (int i = 0; i < d.size(); ++i)
		{
			sum+=d.get(i); 
		}
		
		Utils.Dbg("Vector: %d", System.currentTimeMillis() - time);
	
		time = System.currentTimeMillis();
		double [] da = new double[10000000];
		for (int i = 0; i < da.length; ++i)
		{
			sum+=da[i]; 
		}
		
		Utils.Dbg("Array: %d", System.currentTimeMillis() - time);
	
		double[] b = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
		
		Matrix m = new Matrix(b, 8);
		
		double[][] r = m.getArray();
		
		for(int i =0;i<r.length;++i)
		{
			for (int j = 0;j < r[i].length; ++j)
			{
				System.out.printf("%.01f\t",r[i][j]);
			}
			System.out.println();
		}
		
		int i0 = 0;
		int i1 = 4;
		int j0 = 0;
		int j1 = 0;
		   Matrix X = new Matrix(i1-i0+1,j1-j0+1);
		      double[][] B = X.getArray();
		      try {
		         for (int i = i0; i <= i1; i++) {
		            for (int j = j0; j <= j1; j++) {
		               B[i-i0][j-j0] = r[i][j];
		               Utils.Dbg("%d:%d - %f",i-i0,j-j0,r[i][j]);
		            }
		         }
		      } catch(ArrayIndexOutOfBoundsException e) {
		         throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		      }
	*/	      
		      
		      
		      
		      
		
		
		
	}


}
