package util;

import java.io.IOException;
import java.util.Vector;

import util.math.Maths;

public class CentSpectrum 
{
	
	  //fields
	  protected int windowSize;
	  protected int hopSize;
	  protected float sampleRate;
	  protected double baseFreq;
	  protected FFT magnitudeFFT;
	  protected int centStart;
	  protected int centHop;
	  protected int normalizationWidth;
	  protected int blockWidth = 512;
	  protected int linearFilters;	  

	  //implementation: buffers
	  private double[] inputData;
	  private double[] buffer;
	  private int[] centUpper;

	  /**
	   * Creates a Sone object with default window size of 256 for the given sample
	   * rate. 
	   *
	   * @param sampleRate float samples per second, must be greater than zero; none integer
	   *                         values get rounded
	   * @throws IllegalArgumentException raised if method contract is violated
	   */
	  public CentSpectrum(float sampleRate) throws IllegalArgumentException
	  {
	    //initialize
	    this(2048, 512, sampleRate, 2050, 100, 5, 100);
	  }


	  /**
	   * Creates a Sone object with given window size and sample rate. The overleap
	   * of the windows is fixed at 50 percent. The window size must be 2^n and at
	   * least 32. The sample rate must be at least 1.
	   *
	   * @param windowSize int size of a window
	   * @param sampleRate float samples per second, must be greater than zero; not
	   *                         whole-numbered values get rounded
	   * @throws IllegalArgumentException raised if method contract is violated
	   */
	  public CentSpectrum(int windowSize, int hopSize, float sampleRate, int centStart, int centHop, int linearFilters, int normalizationWidth) throws IllegalArgumentException
	  {
	    //check for correct window size
	    if(windowSize < 32)
	    {
	        throw new IllegalArgumentException("window size must be at least 32");
	    }
	    else
	    {
	        int i = 32;
	        while(i < windowSize && i < Integer.MAX_VALUE)
	          i = i << 1;

	        if(i != windowSize)
	            throw new IllegalArgumentException("window size must be 2^n");
	    }

	    //check sample rate
	    sampleRate = Math.round(sampleRate);
	    if(sampleRate < 1)
	      throw new IllegalArgumentException("sample rate must be at least 1");

	    //initialize fields
	    this.windowSize = windowSize;
	    this.hopSize = hopSize;
	    this.sampleRate = sampleRate;
	    this.baseFreq = sampleRate/windowSize;
	    this.centStart = centStart;
	    this.centHop = centHop;
	    this.linearFilters = linearFilters;
	    this.normalizationWidth = normalizationWidth;

	    //create buffers
	    inputData = new double[windowSize];
	    buffer = new double[windowSize];

	    //create normalized power fft object
	    magnitudeFFT = new FFT(FFT.FFT_MAGNITUDE, windowSize, FFT.WND_HANNINGZ);

	    centUpper = getUpperBoundsCent();
	  }
	  
	  public int[] getUpperBoundsCent()
	  {
			Vector<Integer> upper_bounds = new Vector<Integer>();
			
			//arrays holding frequency of each bin in hz and cent
			float[] bins_hz = new float[windowSize/2 + 1];
			float[] bins_cent = new float[windowSize/2 + 1];

			float base_freq = ((float) sampleRate) / ((float) windowSize);
			for (int i = 0; i < windowSize/2 + 1; i++)
			{
				//compute for the current bin the corresponding frequency in hz
				bins_hz[i] = base_freq * i;

				//convert hz to cent
				if(i==0)
					bins_cent[i] = -1;
				else
					bins_cent[i] = hz2cent(bins_hz[i]);
			}

			//compute the upper bound (in terms of fft bins) of each frequency band
			int start = centStart;
			boolean createLinearFilters = true;
			for(int i = 0; i < windowSize/2; i++)
			{
			    if(bins_cent[i] > start)
			    {
					//split the lowest band into the given number of linear frequency bands
			        if(createLinearFilters)
			        {
			            double sizeInBins = ((double) i) / ((double) linearFilters);
			            double cur = 0;
			            for(int k = 0; k < linearFilters-1; k++)
			            {
			                cur = cur + sizeInBins;
			                upper_bounds.add(new Integer((int) Math.floor(cur)));
						}
						createLinearFilters = false;
					}

					upper_bounds.add(i+1);
			        start = start + centHop;
				}
			}
			
			int[] centUpper = new int[upper_bounds.size()];
			for(int i = 0; i < upper_bounds.size(); i++)
				centUpper[i] = upper_bounds.get(i).intValue();
			
			return centUpper;
	  }


	  /**
	   * Returns the number of samples skipped between two windows.
	   * Since the overleap of 50 percent is fixed, the hop size is half the window
	   * size.
	   *
	   * @return int hop size
	   */
	  public int getHopSize()
	  {
	    return hopSize;
	  }
	  
	  /**
	   * Returns the number of samples skipped between two windows.
	   * Since the overleap of 50 percent is fixed, the hop size is half the window
	   * size.
	   *
	   * @return int hop size
	   */
	  public int getWindowSize()
	  {
	    return windowSize;
	  }
	  

	  /**
	   * Performs the transformation of the input data to Sone. This is done by
	   * splitting the given data into windows and processing each of these windows
	   * with processWindow().
	   *
	   * @param in AudioPreProcessor input data is a complete Audio stream, must
	   *                             have the same sample rate like this sone object,
	   *                             must not be a null value
	   * @return Vector this vector contains a double array of Sone value for each
	   *                window
	   * @throws IOException if there are any problems regarding the inputstream
	   * @throws IllegalArgumentException raised if mehtod contract is violated
	   */
	  public Vector<double[]> process(AudioPreProcessor in) throws IllegalArgumentException, IOException
	  {
	    //check in
	    if(in == null)
	      throw new IllegalArgumentException("the audio preprocessor must not be a null value");

	    //check for correct input format
	    if(in.getSampleRate() != sampleRate)
	        throw new IllegalArgumentException("sample rates of inputstream differs from sample rate of the sone processor");

	    Vector<double[]> cent = new Vector<double[]>();

	    //read whole window
	    int samplesRead = in.append(inputData, 0, windowSize);
	    if(samplesRead == windowSize)
	    {
	    	//now just read a hopSize
	    	samplesRead = hopSize;
	    	while (samplesRead == hopSize)
	    	{
	    		//process the current window
	    		cent.add(processWindow(inputData, 0));
	    		
	    		//move data in window (overleap)
	    		for (int i = hopSize, j = 0; i < windowSize; j++, i++)
	    			inputData[j] = inputData[i];

	    		//read new data
	    		samplesRead = in.append(inputData, windowSize-hopSize, hopSize);
	    	}
	    }
	    
	    double[][] centSpectrum = new double[cent.size()][];
	    for(int i = 0; i < cent.size(); i++)
	    	centSpectrum[i] = cent.get(i);
	    
	    double[][] normalizedSpectrum = normalize(centSpectrum, normalizationWidth);
	    
	    cent = new Vector<double[]>();
	    for(int i = 0; i < normalizedSpectrum.length; i++)
	    	cent.add(normalizedSpectrum[i]);
	    
	    return cent;
	  }
	  
	  

	  /**
	   * Performs the transformation of the input data to Sone.
	   * This is done by splitting the given data into windows and processing
	   * each of these windows with processWindow().
	   *
	   * @param input double[] input data is an array of samples, must be a multiple
	   *                       of the hop size, must not be a null value
	   * @return double[][] an array of arrays contains a double array of Sone value
	   *                    for each window
	   * @throws IOException if there are any problems regarding the inputstream
	   * @throws IllegalArgumentException raised if mehtod contract is violated
	   */
	  public double[][] process(double[] input) throws IllegalArgumentException, IOException
	  {
	    //check for null
	    if(input == null)
	      throw new IllegalArgumentException("input data must not be a null value");

	    //check for correct array length
	    if((input.length % hopSize) != 0)
	        throw new IllegalArgumentException("Input data must be multiple of hop size (windowSize/2).");

	    //create return array with appropriate size
	    double[][] cent = new double[(input.length/hopSize)-1][centUpper.length];

	    //process each window of this audio segment
	    for(int i = 0, pos = 0; pos < input.length - hopSize; i++, pos+=hopSize)
	      cent[i] = processWindow(input, pos);
	    
	    cent = normalize(cent, normalizationWidth);

	    return cent;
	  }


	  /**
	   * Transforms one window of samples to Sone. The following steps are
	   * performed: <br>
	   * <br>
	   * (1) normalized power fft with hanning window function<br>
	   * <br>
	   * (2) compute influence of the outer ear by emphasizing some frequencies
	   *     (model by Terhardt[3])<br>
	   * <br>
	   * (3) convertion to bark scale to reduce the data to the critical bands of
	   *     human hearing[4].<br>
	   * <br>
	   * (4) calculate the influence of spectral masking effekts, since the human
	   *     hear needs some regeneration time and can't perceive similar short
	   *     delayed tones[2]. Also conversion to db is done in this step<br>
	   * <br>
	   * (5) Finally the db values are converted to loudness values (Sone, a
	   *     psychoacoustic scale). This loudness scale better represent the human
	   *     perception of loudness than the db scale does[5].
	   * <br>
	   *
	   * @param window double[] data to be converted,  must contain enough data for
	   *                        one window
	   * @param start int start index of the window data
	   * @return double[] the window representation in Sone
	   * @throws IllegalArgumentException raised if mehtod contract is violated
	   */
	  public double[] processWindow(double[] window, int start) throws IllegalArgumentException
	  {
	    double value;
	    int fftSize = (windowSize / 2) + 1;
	    int centSize = centUpper.length;
	    double[] output = new double[centSize];
	    float normalization = windowSize/2.0f;


	    //check start
	    if(start < 0)
	      throw new IllegalArgumentException("start must be a positve value");

	    //check window size
	    if(window == null || window.length - start < windowSize)
	      throw new IllegalArgumentException("the given data array must not be a null value and must contain data for one window");

	    //just copy to buffer
	    for (int j = 0; j < windowSize; j++)
	      buffer[j] = window[j + start];

	    //perform power fft
	    magnitudeFFT.transform(buffer, null);

	    //transform to the cent scale
	    value = 0;
	    int band = 0;
	    for (int i = 0; i < fftSize && band < centUpper.length; i++)
	    {
	      if (i < centUpper[band])
	      {
	        value += buffer[i];
	      }
	      else
	      {
			//normalized power spectrum
			value /= normalization;

			//prevent log of zero
			if(value < 0.0000000001)
				value = 0.0000000001;

			//transform to dB
			output[band] = 20 * Math.log10(value);
			band++;       
	        value = buffer[i];
	      
	      }
	    }

	    if(band < centUpper.length)
	        output[band] = value;

	    return output;
	  }
	  
	  /* This function removes the moving average from each spectral frame.
	   * (this is some sort of loudness normalization).
	   */
	  double[][] normalize(double[][] centSpectrum, int normalizationWidth)
	  {
		double[][] normalizedCent = new double[centSpectrum.length-2*normalizationWidth][];
		for ( int i = 0; i < normalizedCent.length; i++)
			normalizedCent[i] = new double [centSpectrum[0].length];
		
	  	double[] energy = new double[centSpectrum.length];
	  	double[] movingAvg = new double[centSpectrum.length];
	  	double sum = 0;
	  	double tmp = 0;

	  	//compute the energy of each frame
	  	for(int i = 0; i < centSpectrum.length; i++)
	  	{
	  		//fetch the next cent frame
	  		double[] cent_col = centSpectrum[i];
	  		
	  		energy[i] = 0;

	  		//compute the total energy of this frame
	  		for (int j = 0; j < cent_col.length; j++)
	  			energy[i] += cent_col[j];

	  		energy[i] /= cent_col.length;
	  	}

	  	//compute moving average (initialize)
	  	for (int i = 0; i < 2*normalizationWidth+1; i++)

	  	sum += energy[i];
	  	//compute moving average
	  	for(int i = normalizationWidth; i < centSpectrum.length-(normalizationWidth+1); i++)
	  	{
	  		movingAvg[i] = sum / (2*normalizationWidth+1);
	  		sum -= energy[i-normalizationWidth];
	  		sum += energy[i+normalizationWidth+1];
	  	}

	  	//remove moving average and assign to output spectrum
	  	for(int i = normalizationWidth; i < centSpectrum.length-normalizationWidth; i++)
	  	{
	  		//fetch the next cent frame
	  		double[] cent_col = centSpectrum[i];
	  		double[] norm_cent_col = normalizedCent[i - normalizationWidth];
	  		
	  		//remove the mean from each row in this column
	  		for (int j = 0; j < cent_col.length; j++)
	  		{
	  			tmp = cent_col[j] - movingAvg[i];

	  			//if(tmp < 0)
	  			//	tmp = 0;

	  			norm_cent_col[j] = tmp;
	  		}
	  	}


	  	return normalizedCent;
	  }



	  /* Convert values given in Hz to values expressed in cent.
	   */
	  float hz2cent(float hz)
	  {
	  	return 1200.0f * ((float) Maths.log2( hz / (440.0f * Math.pow(2.0f,((3.0f/12.0f)-5.0f)))));
	  }
}
