package Calculation;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import Common.Dbg;

public class FrequencyComparer {

	private int windowSize_;
	private float []  buffer_;
	private FFT fft_;
	private int hopSize_;
	private int numberCoefficients_;
	private Freq[] maxes_;		  	
	public FrequencyComparer(int windowSize, int minFrequency, int maxFrequency, int numberCoefficients)
	{
		windowSize_ = windowSize;
		buffer_  = new float[windowSize];
		fft_  = new FFT(FFT.FFT_NORMALIZED_POWER, windowSize, FFT.WND_BLACKMAN_NUTTALL);
		hopSize_ = windowSize / 2;
		numberCoefficients_ = numberCoefficients;		
		maxes_ = new Freq[numberCoefficients];
	}
	
	public float Compare(float[][]a, float[][] b)
	{
			int length = Math.min(a.length, b.length);				
			float rms = - Math.abs(a.length - b.length);		
			for (int i = 0; i <a.length; ++i )
			{
				for (int j = 0; j < b.length; ++j)
				{
					rms+=Compare(a[i],b[i]);
				}
			}			
			return rms / length;
	}
	
	public float Compare(float[] a, float[] b)
	{
				int length = Math.min(a.length, b.length);				
				//float rms = - Math.abs(a.length - b.length);
				
				float rms = 0;
				for (int i = 0; i < length; ++i)
				{
						float coef = (float) Math.pow(2,i);					
						rms+=Math.pow((a[i] - b[i]) / coef,2);				
				}				
				return (float) Math.sqrt(rms / length);
	}
	
	
	 public  float[][] process( float[] input) throws IllegalArgumentException, IOException
	  {
	    //check for null
	    if(input == null)
	      throw new IllegalArgumentException("input data must not be a null value");

	    //check for correct array length
	    if((input.length % hopSize_) != 0)
	        throw new IllegalArgumentException("Input data must be multiple of hop size (windowSize/2).");

	    //create return array with appropriate size
	    float[][] capt = new  float[(input.length/hopSize_)-1][numberCoefficients_];

	    //process each window of this audio segment
	    for(int i = 0, pos = 0; pos < input.length - hopSize_; i++, pos+=hopSize_)
	    {
	    	capt[i] = processWindow(input, pos);
	    }
	    return capt;
	  }
	
	  public  float[] processWindow( float[] window, int start)
	  {
		  if(start < 0)
		      throw new IllegalArgumentException("start must be a positve value");

		  //check window size
		  if(window == null || window.length < windowSize_)
			  throw new IllegalArgumentException("the given data array must not be a null value and must contain data for one window");

		  //just copy to buffer and rescaled the input samples according to the original matlab implementation to 96dB
		  for (int j = 0; j < windowSize_; j++)
			  buffer_[j] = window[j + start];// * scale;

		  //perform power fft
		  fft_.transform(buffer_, null);

		  double last = buffer_[10];	 
		  boolean maxed = false;	  
		  
		  for (int i = 11; i < buffer_.length / 2; ++i)
		  {
			  	if (buffer_[i] > last)
			  	{
			  		last = buffer_[i];
			  		maxed = false;
			  	}
			  	else
			  	{		  		
			  		if (!maxed)
			  		{
			  			//Dbg.Info("i:%d max:%f",i-1, buffer[i-1]);
			  			
			  			float c = (float)(i - 1) / 10;  
			  			
			  			int left = (int) Math.ceil(c);
			  			int right = (int)Math.floor(c);		  
			  			if (left != right)
			  			{			  				
			  				addToFreq(left, buffer_[i-1] * Math.abs( left  - c) ,maxes_);
			  				addToFreq(right, buffer_[i-1]  * Math.abs( right  - c),maxes_);
			  			}
			  			else
			  			{
			  				addToFreq(left, buffer_[i-1],maxes_);
			  			}
			  			maxed  = true;		  			
			  		}
			  		last = buffer_[i];
			  	}
		  }
		  
		  float ret[] = new float[maxes_.length];		 
		  long r = 0;
		  for (int i = 0; i < maxes_.length; ++i)
		  {		  
			  ret[i] = maxes_[i].freq;		  
		  }		  
		  return ret;
	  }
	  
	  private class Freq
	  {
		  int freq;
		  float level;
		  public Freq(int frequency, float level)
		  {
			  this.freq = frequency;
			  this.level = level;		  
		  }
	  }

	  
	  private void addToFreq(int i, float level, Freq[] maxes_)
	  {
			for (int j =0 ;j < maxes_.length; ++j)
		  	{
		  		if (maxes_[j] == null)
		  		{
		  			maxes_[j] = new Freq(i, level);
		  			break;
		  		}		
		  		
		  		if (maxes_[j].freq == i )
		  		{
						maxes_[j].level += level;
						Arrays.sort(maxes_,new Comparator<Freq>(){
							@Override
							public int compare(Freq arg0, Freq arg1) 
							{
								if (arg0 == null && arg1 == null)
								{
									return 0;
								}
								if (arg0 == null) return 1;
								if (arg1 ==null) return -1;
								
								return -new Float(arg0.level).compareTo(new Float(arg1.level));
							}});
						break;
		  		}
		  			  			  		
		  		if (maxes_[j].level < level)
		  		{
		  				if (j < maxes_.length - 1)
		  				{
		  					maxes_[j+1] = maxes_[j];
		  				}
		  				maxes_[j]  = new Freq(i,level);
		  				break;
		  		}		  		
		  	}
	  }
		  
	  }
