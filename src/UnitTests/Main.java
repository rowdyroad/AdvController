package UnitTests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import Common.Overlapper;
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
				Utils.Dbg("FR:%d M:%f", i, data[i]);		
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
			Utils.Dbg("Write:%d",len);
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
			Utils.Dbg("buflen != len:%d %d", buf.length, len);
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
			Utils.Dbg(str);
			System.arraycopy(overlapped_cache_, overlapped_length_, overlapped_cache_, 0, overlapped_cache_len_ - overlapped_length_);
			overlapped_cache_len_-=overlapped_length_;
				
			
		}
	}
	
	
	public static void testOver()
	{
		double[]  d = {1,1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9,0,1,2,4,5,6,7,9,1,2,3,4,6,7,8,9,0};
		
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
		
	}
	
	public static void main (String args [])
	{
		testOver();
	}
}
