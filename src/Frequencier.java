import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import javax.sound.sampled.AudioInputStream;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import analys.FFT;
import analys.MFCC;

public class Frequencier {

	public interface Catcher
	{
		public boolean OnReceived(double frequency, long timeoffset);
		public void OnError();
	}
	
	static private int WINDOW_SIZE = 4096;
	static private int OVERLAPPED_COEF = 2; 
	private AudioInputStream stream_;
	private FFT fft_;	
	private double [] buf_ = new double[WINDOW_SIZE];
	private int index_ = 0;
	private Catcher catcher_ = null;
	

	private double scale_;
	private int frameSize_;
	public Frequencier(AudioInputStream stream, Catcher catcher)
	{
		catcher_ = catcher;
		stream_ = stream;
		fft_ = new FFT(FFT.FFT_NORMALIZED_POWER, WINDOW_SIZE, FFT.WND_HAMMING);
		
		frameSize_ = stream.getFormat().getFrameSize();
		scale_ = 1.0 / ((double) stream.getFormat().getChannels() * (1 << (stream.getFormat().getSampleSizeInBits() - 1)));
		
		Utils.ShowStreamInfo(stream_);
	}
	

	private double[] read() throws Exception
	{
		
		byte[] b = new byte[frameSize_];
		while ( index_ <  WINDOW_SIZE)
		{
				int c = stream_.read(b);
				if (c == -1)
				{
					if (index_ == 0) 
					{
						throw new Exception();
					}
					else
					{
						break;
					}
				}
				
			short db =(short) (b[1] << 8 | b[0]);
			buf_[index_++] = db * scale_;
		}	
		
		double [] ret = new double[WINDOW_SIZE];
		
		
		
		System.arraycopy(buf_, 0, ret, 0, WINDOW_SIZE);
		
		Arrays.fill(buf_, 0);
		System.arraycopy(ret, WINDOW_SIZE / OVERLAPPED_COEF, buf_, 0, WINDOW_SIZE  - WINDOW_SIZE / OVERLAPPED_COEF);
		index_ = (index_ < WINDOW_SIZE)  ?  0  :  WINDOW_SIZE  - WINDOW_SIZE / OVERLAPPED_COEF;
		return ret;
	}
	

	public void process()
	{
		int time = 0;
		double last = -1;
		
		try 
		{		
			while (true)
			{
				double data = iterate();
				if (last == -1 && data == 0) continue;
				if (last != data && data > 0)
				{
					
					if (! catcher_.OnReceived(data, time))
					{
						break;	
					}
					time = 0;
				}
				
				last = data;
				time += (int)( WINDOW_SIZE * 1000 / stream_.getFormat().getSampleRate() / OVERLAPPED_COEF );
			}
			
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
			catcher_.OnError();
		}
	}
	
	private double iterate() throws Exception
	{
		int x = 0;
		double max = 0;
		double[] data = read();

		fft_.transform(data, null);
		
		for (int i=0; i < data.length; ++i)
		{
			if (data[i] > max)
			{
				max = data[i];	
				x = i;
			}
		}
		
		return x * stream_.getFormat().getSampleRate() / WINDOW_SIZE;
	}
}
