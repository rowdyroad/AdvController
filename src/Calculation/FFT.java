package Calculation;

import Common.Utils;

public final class FFT
{
	public static final int FFT_FORWARD = -1;
	public static final int FFT_REVERSE = 1;
	public static final int FFT_MAGNITUDE = 2;
	public static final int FFT_MAGNITUDE_PHASE = 3;
	public static final int FFT_NORMALIZED_POWER = 4;
	public static final int FFT_POWER = 5;
	public static final int FFT_POWER_PHASE = 6;
	public static final int FFT_INLINE_POWER_PHASE = 7;

	public static final int WND_NONE = -1;
	public static final int WND_RECT = 0;
	public static final int WND_HAMMING = 1;
	public static final int WND_BH3 = 2;
	public static final int WND_BH4 = 3;
	public static final int WND_BH3MIN = 4;
	public static final int WND_BH4MIN = 5;
	public static final int WND_GAUSS = 6;
	public static final int WND_HANNING = 7;
	public static final int WND_USER_DEFINED = 8;
	public static final int WND_HANNINGZ = 9;  
	public static final int WND_BLACKMAN_NUTTALL= 10;

	private  float[] windowFunction;
	private  float windowFunctionSum;
	private int windowFunctionType;
	private int transformationType;
	private int windowSize;
	private static final  float twoPI = (float) (2 * Math.PI);
	private 	int bits;
	private   float[]  Wj_rs;
	private   float[]  Wj_is;
	private  int[] Tj;

	public FFT(int transformationType, int windowSize)
  {
    this(transformationType, windowSize, WND_NONE, windowSize);
  }

  public FFT(int transformationType, int windowSize, int windowFunctionType)
  {
    this(transformationType, windowSize, windowFunctionType, windowSize);
  }

  public FFT(int transformationType, int windowSize, int windowFunctionType, int support)
  {
	 bits = (int)Math.rint(Math.log(windowSize) / Math.log(2));
	 Wj_rs = new  float[bits+1];
	 Wj_is = new  float[bits+1];	
	for(int m = 1; m <= bits; m++)
	{
		int localN = 1 << m;
		Wj_rs[m] = (float) Math.cos(twoPI / localN);
		Wj_is[m] = (float) - Math.sin(twoPI / localN);			
	}
	
	Tj= new int[windowSize-1];
	int j = 0;
	int start = windowSize >> 1;
	for (int i = 0; i <  windowSize -1 ; ++i)
	{
		Tj[i] = j;
		int k;
		for (k = start; k <= j; k = k >> 1)
		{
			j-=k;				
		}		
		j+=k;
	}
    //check and set fft type
    this.transformationType = transformationType;
    if(transformationType < -1 || transformationType > 7)
    {
      transformationType = FFT_FORWARD;
      throw new IllegalArgumentException("unknown fft type");
    }

    //check and set windowSize
    this.windowSize = windowSize;
    if (windowSize != (1 << ((int)Math.rint(Math.log(windowSize) / Math.log(2)))))
      throw new IllegalArgumentException("fft data must be power of 2");

    //create window function buffer and set window function
    this.windowFunction = new  float[windowSize];
    setWindowFunction(windowFunctionType, support);
  }

  public FFT(int transformationType, int windowSize,  float[] windowFunction)
  {
    this(transformationType, windowSize, WND_NONE, windowSize);

    if(windowFunction.length != windowSize)
    {
      throw new IllegalArgumentException("Data array is smaller than FFT window size");
    }
    else
    {
      this.windowFunction = windowFunction;
      this.windowFunctionType = WND_USER_DEFINED;
      calculateWindowFunctionSum();
    }

  }

  public void transform( float[] re,  float[] im)
  {
    if(re.length < windowSize)
      throw new IllegalArgumentException("Data array is smaller than FFT window size");
    applyWindowFunction(re);
    switch(transformationType)
    {
      case FFT_FORWARD:
        if(im.length < windowSize)
          throw new IllegalArgumentException("Data array is smaller than FFT window size");
        else
          fft(re, im);
        break;
      case FFT_MAGNITUDE:
    	  magnitudeFFT(re);
        break;
      case FFT_MAGNITUDE_PHASE:
        if(im.length < windowSize)
          throw new IllegalArgumentException("Data array is smaller than FFT window size");
        else
         magnitudePhaseFFT(re, im);
        break;
      case FFT_NORMALIZED_POWER:
        normalizedPowerFFT(re);
        break;
      case FFT_POWER:
        powerFFT(re);
        break;
      case FFT_POWER_PHASE:
        if(im.length < windowSize)
          throw new IllegalArgumentException("Data array is smaller than FFT window size");
        else
          powerPhaseFFT(re, im);
        break;
    }
  }
  
  private void fft( float re[],  float im[])
  {
		int n = re.length;
		int last = n - 1;
		for (int i = 0; i < last; ++i)
		{
			int j = Tj[i];
			if (i < j)
			{
				 float temp = re[j];
				re[j] = re[i];
				re[i] = temp;
			}
		}

		int localN = 0;
		for(int m = 1; m <= bits; m++)
		{
			localN = 1 << m;
			 float Wjk_r = 1;
			 float Wjk_i = 0;
			 float Wj_r = Wj_rs[m];
			 float Wj_i = Wj_is[m];
			int nby2 = localN >>  1;			
			for (int j = 0; j < nby2; ++j)
			{
				for (int k = j; k < n; k += localN)
				{
					int id = k + nby2;
					 float tempr = Wjk_r * re[id] - Wjk_i * im[id];
					 float tempi = Wjk_r * im[id] + Wjk_i * re[id];
					re[id] = re[k] - tempr;
					im[id] = im[k] - tempi;
					re[k] += tempr;
					im[k] += tempi;
				}
				 float wtemp = Wjk_r;
				Wjk_r = Wj_r * Wjk_r  - Wj_i * Wjk_i;
				Wjk_i = Wj_r * Wjk_i  + Wj_i * wtemp;
			}
		}
		
	}


	/** Computes the power spectrum of a real sequence (in place).
	 *  @param re the real input and output data; length must be a power of 2
	 */
	private void powerFFT( float[] re)
  {
		 float[] im = new  float[re.length];

		fft(re, im);

		for (int i = 0; i < re.length; i++)
			re[i] = re[i] * re[i] + im[i] * im[i];
	}


  /** Computes the magnitude spectrum of a real sequence (in place).
   *  @param re the real input and output data; length must be a power of 2
	 */
  private void magnitudeFFT( float[] re)
  {
	  float[] im = new  float[re.length];

    fft(re, im);

    for (int i = 0; i < re.length; i++)
      re[i] = (float) Math.sqrt(re[i] * re[i] + im[i] * im[i]);
  }


  /** Computes the power spectrum of a real sequence (in place).
   *  @param re the real input and output data; length must be a power of 2
   */
  private void normalizedPowerFFT( float[] re)
  {
	  float[] im = new  float[re.length];
    double r, i;

    fft(re, im);

    for (int j = 0; j < re.length; j++)
    {
      r = re[j] / windowFunctionSum * 2;
      i = im[j] / windowFunctionSum * 2;
      re[j] = (float) (r * r + i * i);
    }
  }


	/** Converts a real power sequence from to magnitude representation,
	 *  by computing the square root of each value.
	 *  @param re the real input (power) and output (magnitude) data; length
	 *  must be a power of 2
	 */
	private void toMagnitude( float[] re)
  {
		for (int i = 0; i < re.length; i++)
			re[i] = (float) Math.sqrt(re[i]);
	}


	/** Computes a complex (or real if im[] == {0,...}) FFT and converts
	 *  the results to polar coordinates (power and phase). Both arrays
	 *  must be the same length, which is a power of 2.
	 *  @param re the real part of the input data and the power of the output
	 *  data
	 *  @param im the imaginary part of the input data and the phase of the
	 *  output data
	 */
	private void powerPhaseFFT( float[] re,  float[] im)
  {
		fft(re, im);

		for (int i = 0; i < re.length; i++)
    {
			 float pow = re[i] * re[i] + im[i] * im[i];
			im[i] = (float) Math.atan2(im[i], re[i]);
			re[i] = pow;
		}
	}

	/** Computes a complex (or real if im[] == {0,...}) FFT and converts
	 *  the results to polar coordinates (magnitude and phase). Both arrays
	 *  must be the same length, which is a power of 2.
	 *  @param re the real part of the input data and the magnitude of the
	 *  output data
	 *  @param im the imaginary part of the input data and the phase of the
	 *  output data
	 */
	private void magnitudePhaseFFT( float[] re,  float[] im)
  {
		powerPhaseFFT(re, im);
		toMagnitude(re);
	}


	/** Fill an array with the values of a standard Hamming window function
	 *  @param data the array to be filled
	 *  @param size the number of non zero values; if the array is larger than
	 *  this, it is zero-padded symmetrically at both ends
	 */
	private void hamming(int size)
  {
    int start = (windowFunction.length - size) / 2;
		int stop = (windowFunction.length + size) / 2;
		 float scale = (float) (1.0 * 0.54 / size );
		 float factor = twoPI / ( float)size;

		for (int i = 0; start < stop; start++, i++)
			windowFunction[i] = (float) (scale * (25.0/46.0 - 21.0/46.0 * Math.cos(factor * i)));
	}


  /** Fill an array with the values of a standard Hanning window function
   *  @param data the array to be filled
   *  @param size the number of non zero values; if the array is larger than
   *  this, it is zero-padded symmetrically at both ends
   */
  private void hanning(int size)
  {
    int start = (windowFunction.length - size) / 2;
    int stop = (windowFunction.length + size) / 2;
    float factor = (float) (twoPI / (size - 1.0f));

    for (int i = 0; start < stop; start++, i++)
      windowFunction[i] = (float) (0.5 * (1 - Math.cos(factor * i)));
  }
  
  /** In MATLABTM, picking up the standard hanning window gives an incorrect periodicity,
   *  because the boundary samples are non-zero; in OCTAVETM, both boundary samples 
   *  are zero, which still gives an incorrect periodicity. This is why we use hanningz,
   *  a modified version of the hanning window available with the MATLABTM toolboxes: 
   *  function w = hanningz(n) w = .5*(1 - cos(2*pi*(0:n-1)'/(n)));
   *  
   *  @param data the array to be filled
   *  @param size the number of non zero values; if the array is larger than
   *  this, it is zero-padded symmetrically at both ends
   */
  private void hanningz(int size)
  {
    int start = (windowFunction.length - size) / 2;
    int stop = (windowFunction.length + size) / 2;

    for (int i = 0; start < stop; start++, i++)
      windowFunction[i]	= (float) (0.5 * (1 - Math.cos((twoPI*i)/size)));
  }

private void blackmanNuttall(int size)
{
    int start = (windowFunction.length - size) / 2;
    int stop = (windowFunction.length + size) / 2;
    
	for (int i =0; start < stop; start++, ++i)
	{
		 float  w = twoPI * i / windowSize;
		windowFunction[i] =  (float) (0.3635819 - 0.4891775*Math.cos(w) + 0.1365995*Math.cos(2*w) - 0.0106411*Math.cos(3*w));
	}
}

  /** Fill an array with the values of a minimum 4-sample Blackman-Harris
	 *  window function
	 *  @param data the array to be filled
	 *  @param size the number of non zero values; if the array is larger than
	 *  this, it is zero-padded symmetrically at both ends
	 */
	private void blackmanHarris4sMin(int size)
  {
    int start = (windowFunction.length - size) / 2;
		int stop = (windowFunction.length + size) / 2;
		 float  scale = 1.0f * 0.36f /  size;

		for (int i = 0; start < stop; start++, i++)
			windowFunction[i] = (float) (scale * ( 0.35875 -
								0.48829 * Math.cos(twoPI * i / size) +
								0.14128 * Math.cos(2 * twoPI * i / size) -
								0.01168 * Math.cos(3 * twoPI * i / size)));
	}


	/** Fill an array with the values of a 74-dB 4-sample Blackman-Harris
	 *  window function
	 *  @param data the array to be filled
	 *  @param size the number of non zero values; if the array is larger than
	 *  this, it is zero-padded symmetrically at both ends
	 */
	private void blackmanHarris4s(int size)
  {
    int start = (windowFunction.length - size) / 2;
		int stop = (windowFunction.length + size) / 2;
		float scale = 1.0f  * 0.4f / size;

		for (int i = 0; start < stop; start++, i++)
			windowFunction[i] = (float) (scale * ( 0.40217 -
								0.49703 * Math.cos(twoPI * i / size) +
								0.09392 * Math.cos(2 * twoPI * i / size) -
								0.00183 * Math.cos(3 * twoPI * i / size)));
	}


	/** Fill an array with the values of a minimum 3-sample Blackman-Harris
	 *  window function
	 *  @param data the array to be filled
	 *  @param size the number of non zero values; if the array is larger than
	 *  this, it is zero-padded symmetrically at both ends
	 */
	private void blackmanHarris3sMin(int size)
  {
		int start = (windowFunction.length - size) / 2;
		int stop = (windowFunction.length + size) / 2;
		float scale = 1.0f * 0.42f / size;

		for (int i = 0; start < stop; start++, i++)
			windowFunction[i] = (float) (scale * ( 0.42323 -
								0.49755 * Math.cos(twoPI * i / size) +
								0.07922 * Math.cos(2 * twoPI * i / size)));
	}


	/** Fill an array with the values of a 61-dB 3-sample Blackman-Harris
	 *  window function
	 *  @param data the array to be filled
	 *  @param size the number of non zero values; if the array is larger than
	 *  this, it is zero-padded symmetrically at both ends
	 */
	private void blackmanHarris3s(int size)
  {
		int start = (windowFunction.length - size) / 2;
		int stop = (windowFunction.length + size) / 2;
		float scale = 1.0f * 0.45f / size;

		for (int i = 0; start < stop; start++, i++)
			windowFunction[i] = (float) (scale * ( 0.44959 -
								0.49364 * Math.cos(twoPI * i / size) +
								0.05677 * Math.cos(2 * twoPI * i / size)));
	}


	/** Fill an array with the values of a Gaussian window function
	 *  @param data the array to be filled
	 *  @param size the number of non zero values; if the array is larger than
	 *  this, it is zero-padded symmetrically at both ends
	 */
	private void gauss(int size)
  { // ?? between 61/3 and 74/4 BHW
		int start = (windowFunction.length - size) / 2;
		int stop = (windowFunction.length + size) / 2;
		float delta = 5.0f / size;
		float x = (1 - size) / 2.0f * delta;
		float c = (float) (-Math.PI * Math.exp(1.0) / 10.0);
		float sum = 0;

    for (int i = start; i < stop; i++)
    {
			windowFunction[i] = (float) Math.exp(c * x * x);
			x += delta;
			sum += windowFunction[i];
		}

    for (int i = start; i < stop; i++)
			windowFunction[i] /= sum;
	}


	/** Fill an array with the values of a rectangular window function
	 *  @param data the array to be filled
	 *  @param size the number of non zero values; if the array is larger than
	 *  this, it is zero-padded symmetrically at both ends
	 */
	private void rectangle(int size)
  {
		int start = (windowFunction.length - size) / 2;
		int stop = (windowFunction.length + size) / 2;

		for (int i = start; i < stop; i++)
			windowFunction[i] = 1.0f / size;
	}


  /**
   * This method allows to change the window function to one of the predefined
   * window function types.
   *
   * @param windowFunctionType int the type of the window function
   * @param support int
   */
  public void setWindowFunction(int windowFunctionType, int support)
  {
		if (support > windowSize)
			support = windowSize;

		switch (windowFunctionType)
		{
			case WND_NONE: 	break;
			case WND_BLACKMAN_NUTTALL: blackmanNuttall(support); break;
			case WND_RECT:		rectangle(support);				break;
			case WND_HAMMING:	hamming(support);				break;
			case WND_HANNING:   hanning(support);				break;
			case WND_BH3:		blackmanHarris3s(support);		break;
			case WND_BH4:		blackmanHarris4s(support);		break;
			case WND_BH3MIN:	blackmanHarris3sMin(support);	break;
			case WND_BH4MIN:	blackmanHarris4sMin(support);	break;
			case WND_GAUSS:		gauss(support);					break;
			case WND_HANNINGZ:   hanningz(support); 		break;
			default:
				windowFunctionType = WND_NONE;
				throw new IllegalArgumentException("unknown window function specified");
		}
		
		this.windowFunctionType = windowFunctionType;
		calculateWindowFunctionSum();
	}

  public int getTransformationType()
  {
    return transformationType;
  }

  public int getWindowFunctionType()
  {
    return windowFunctionType;
  }

	/** Applies a window function to an array of data, storing the result in
	 *  the data array.
	 *  Performs a dot product of the data and window arrays.
	 *  @param data   the array of input data, also used for output
	 *  @param window the values of the window function to be applied to data
	 */
	private void applyWindowFunction( float[] data)
  {
		if(windowFunctionType != WND_NONE)
    {
      for (int i = 0; i < data.length; i++)
        data[i] *= windowFunction[i];
    }
	}

  private void calculateWindowFunctionSum()
  {
    windowFunctionSum = 0;
    for(int i = 0; i < windowFunction.length; i++)
      windowFunctionSum += windowFunction[i];
  }
}
