package Calculation;

import java.io.IOException;
import java.util.Arrays;

import Calculation.math.Matrix;
import Common.Dbg;

/**
 * <b>Mel Frequency Cepstrum Coefficients - MFCCs</b>
 *
 * <p>Description: </p>
 * Computes the MFCC representation of a pcm signal. The signal is cut into
 * short overlapping frames, and for each frame, a feature vector is is computed,
 * which consists of Mel Frequency Cepstrum Coefficients.<br>
 * The cepstrum is the inverse Fourier transform of the log-spectrum. We call
 * mel-cepstrum the cepstrum computed after a non-linear frequency wrapping onto
 * a perceptual frequency scale, the Mel-frequency scale. Since it is a inverse
 * Fourier transform, the resulting coefficients are called Mel frequency
 * cepstrum coefficients (MFCC). Only the first few coefficients are used to
 * represent a frame. The number of coefficients is a an important parameter.
 * Therefore MFCCs provide a low-dimensional, smoothed version of the log
 * spectrum, and thus are a good and compact representation of the spectral shape.
 * They are widely used as features for speech recognition, and have also proved
 * useful in music instrument recognition [1].<br>
 *<br>
 * [1] Aucouturier, Pachet "Improving Trimbre Similarity: How high's the sky?",
 *     in Journal of Negative Results in Speech and Audio Sciences, 1(1), 2004.
 *
 *
 * @author Klaus Seyerlehner
 * @version 1.0
 */
public class MFCC
{
  //general fields
  protected int windowSize;
  protected int hopSize;
  protected float sampleRate;
  protected float baseFreq;

  //fields concerning the mel filter banks
  protected float minFreq;
  protected float maxFreq;
  protected int numberFilters;

  //fields concerning the MFCCs settings
  protected int numberCoefficients;
  protected boolean useFirstCoefficient;

  //implementation details
  private Matrix dctMatrix;
  private Matrix melFilterBanks;
  private FFT normalizedPowerFFT;
  private float scale;
  private int halfWindowSize;
  private float[] ret;
  private static float log10 = (float) (10 * (1 / Math.log(10))); // log for base 10 and scale by factor 10

  public MFCC(int sampleRate, int windowSize, int numberCoefficients, boolean useFirstCoefficient, int minFreq, int maxFreq, int numberFilters) throws IllegalArgumentException
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

    //check numberFilters
    if(numberFilters < 2 || numberFilters > (windowSize/2) + 1)
      throw new IllegalArgumentException("number filters must be at least 2 and smaller than the nyquist frequency");

    //check numberCoefficients
    if(numberCoefficients < 1 || numberCoefficients >= numberFilters)
      throw new IllegalArgumentException("the number of coefficients must be greater or equal to 1 and samller than the number of filters");

    //check minFreq/maxFreq
    if(minFreq <= 0 || minFreq > maxFreq || maxFreq > 88200.0f)
      throw new IllegalArgumentException("the min. frequency must be greater 0 smaller than the max. frequency, which must be smaller than 88200.0");;

    this.sampleRate = sampleRate;
    this.windowSize = windowSize;
    this.hopSize = windowSize/2; //50% Overlap
    this.baseFreq = sampleRate/windowSize;

    this.numberCoefficients = numberCoefficients;
    this.useFirstCoefficient = useFirstCoefficient;

    this.minFreq = minFreq;
    this.maxFreq = maxFreq;
    this.numberFilters = numberFilters;

    //create buffers
    halfWindowSize = windowSize / 2;
   
    

    //store filter weights and DCT matrix due to performance reason
    melFilterBanks = getMelFilterBanks();
    dctMatrix = getDCTMatrix();
    ret = new float[melFilterBanks.getRowDimension()];
    normalizedPowerFFT = new FFT(FFT.FFT_NORMALIZED_POWER, windowSize,FFT.WND_HAMMING, sampleRate, minFreq, maxFreq);
  }


  /**
   * Returns the boundaries (start, center, end) of a given number of triangular
   *\ mel filters at linear scale. Mel-filters are triangular filters on the
   * linear scale with an integral (area) of 1. However they are placed
   * equidistantly on the mel scale, which is non-linear rather logarithmic.
   * The minimum linear frequency and the maximum linear frequency define the
   * mel-scaled interval to equidistantly place the filters.
   * Since mel-filters overlap, an array is used to efficiently store the
   * boundaries of a filter. For example you can get the boundaries of the k-th
   * filter by accessing the returned array as follows:
   *
   * leftBoundary = boundaries[k-1];
   * center = boundaries[k];
   * rightBoundary = boundaries[k+1];
   *
   * @param minFreq float frequency used for the left boundary of the first
   *                       filter
   * @param maxFreq float frequency used for the right boundary of the last
   *                       filter
   * @param numberFilters int number of filters to place within the interval
   *                          [minFreq, maxFreq]
   * @return float[] array holding the boundaries
   */
  private float[] getMelFilterBankBoundaries(float minFreq, float maxFreq, int numberFilters)
  {
    //create return array
    float[] centers = new float[numberFilters + 2];
    float maxFreqMel, minFreqMel, deltaFreqMel, nextCenterMel;

    //compute mel min./max. frequency
    maxFreqMel = linToMelFreq(maxFreq);
    minFreqMel = linToMelFreq(minFreq);
    deltaFreqMel = (maxFreqMel - minFreqMel)/(numberFilters + 1);

    //create (numberFilters + 2) equidistant points for the triangles
    nextCenterMel = minFreqMel;
    for(int i = 0; i < centers.length; i++)
    {
      //transform the points back to linear scale
      centers[i] = melToLinFreq(nextCenterMel);
      nextCenterMel += deltaFreqMel;
    }

    //ajust boundaries to exactly fit the given min./max. frequency
    centers[0] = minFreq;
    centers[numberFilters + 1] = maxFreq;

    return centers;
  }


 /**
  * This method creates a matrix containing <code>numberFilters</code>
  * mel-filters. Each filter is represented by one row of this matrix. Thus all
  * the filters can be applied at once by a simple matrix multiplication.
  *
  * @return Matrix a matrix containing the filter banks
  */
 private Matrix getMelFilterBanks()
  {
    //get boundaries of the different filters
    float[] boundaries = getMelFilterBankBoundaries(minFreq, maxFreq, numberFilters);

    //ignore filters outside of spectrum
    for(int i = 1; i < boundaries.length-1; i++)
    {
      if(boundaries[i] > sampleRate/2 )
      {
        numberFilters = i-1;
        break;
      }
    }

    //create the filter bank matrix
    float[][] matrix = new float[numberFilters][];

    //fill each row of the filter bank matrix with one triangular mel filter
    for(int i = 1; i <= numberFilters; i++)
    {
      float[] filter = new float[(windowSize/2)+1];

      //for each frequency of the fft
      for(int j = 0; j < filter.length; j++)
      {
        //compute the filter weight of the current triangular mel filter
        float freq = baseFreq * j;
        filter[j] = getMelFilterWeight(i, freq, boundaries);
      }

      //add the computed mel filter to the filter bank
      matrix[i-1] = filter;
    }

    //return the filter bank
    return new Matrix(matrix, numberFilters, (windowSize/2)+1);
  }

  /**
   * Returns the filter weight of a given mel filter at a given frequency.
   * Mel-filters are triangular filters on the linear scale with an integral
   * (area) of 1. However they are placed equidistantly on the mel scale, which
   * is non-linear rather logarithmic.
   * Consequently there are lots of high, thin filters at start of the linear
   * scale and rather few and flat filters at the end of the linear scale.
   * Since the start-, center- and end-points of the triangular mel-filters on
   * the linear scale are known, the weights are computed using linear
   * interpolation.
   *
   * @param filterBank int the number of the mel-filter, used to extract the
   *                       boundaries of the filter from the array
   * @param freq float    the frequency, at which the filter weight should be
   *                       returned
   * @param boundaries float[] an array containing all the boundaries
   * @return float the filter weight
   */
  private float getMelFilterWeight(int filterBank, float freq, float[] boundaries)
  {
    //for most frequencies the filter weight is 0
    float result = 0;

    //compute start- , center- and endpoint as well as the height of the filter
    float start = boundaries[filterBank - 1];
    float center = boundaries[filterBank];
    float end = boundaries[filterBank + 1];
    float height = 2.0f/(end - start);

    //is the frequency within the triangular part of the filter
    if(freq >= start && freq <= end)
    {
      //depending on frequency position within the triangle
      if(freq < center)
      {
        //...use a ascending linear function
        result = (freq - start) * (height/(center - start));
      }
      else
      {
        //..use a descending linear function
        result = height + ((freq - center) * (-height/(end - center)));
      }
    }

    return result;
  }


  /**
   * Compute mel frequency from linear frequency.
   *
   * @param inputFreq the input frequency in linear scale
   * @return the frequency in a mel scale
   */
  private float linToMelFreq(float inputFreq)
  {
      return (float) (2595.0f * (Math.log(1.0f + inputFreq / 700.0f) / Math.log(10.0f)));
  }


  /**
   * Compute linear frequency from mel frequency.
   *
   * @param inputFreq the input frequency in mel scale
   * @return the frequency in a linear scale
   */
  private float melToLinFreq(float inputFreq)
  {
      return (float) (700.0f * (Math.pow(10.0f, (inputFreq / 2595.0f)) - 1.0f));
  }


  /**
   * Generates the DCT matrix for the known number of filters (input vector) and
   * for the known number of used coefficients (output vector). Therefore the
   * DCT matrix has the dimensions (numberCoefficients x numberFilters).
   * If useFirstCoefficient is set to false the matrix dimensions are
   * (numberCoefficients-1 x numberFilters). This matrix is a submatrix of the
   * full matrix. Only the first row is missing.
   *
   * @return Matrix the appropriate DCT matrix
   */
  private Matrix getDCTMatrix()
  {
    //compute constants
    float k = (float) (Math.PI/numberFilters);
    float w1 = (float) (1.0f/(Math.sqrt(numberFilters)));//1.0/(Math.sqrt(numberFilters/2));
    float w2 = (float) Math.sqrt(2.0f/numberFilters);//Math.sqrt(2.0/numberFilters)*(Math.sqrt(2.0)/2.0);

    //create new matrix
    Matrix matrix = new Matrix(numberCoefficients, numberFilters);

    //generate dct matrix
    for(int i = 0; i < numberCoefficients; i++)
    {
      for(int j = 0; j < numberFilters; j++)
      {
        if(i == 0)
          matrix.set(i, j, (float) (w1 * Math.cos(k*i*(j + 0.5f))));
        else
          matrix.set(i, j, (float) (w2 * Math.cos(k*i*(j + 0.5f))));
      }
    }

    //ajust index if we are using first coefficient
    if(!useFirstCoefficient)
      matrix = matrix.getMatrix(1, numberCoefficients-1, 0, numberFilters-1);

    return matrix;
  }

  /**
   * Performs the transformation of the input data to MFCCs.
   * This is done by splitting the given data into windows and processing
   * each of these windows with processWindow().
   *
   * @param input float[] input data is an array of samples, must be a multiple
   *                       of the hop size, must not be a null value
   * @return float[][] an array of arrays contains a float array of Sone value
   *                    for each window
   * @throws IOException if there are any problems regarding the inputstream
   * @throws IllegalArgumentException raised if method contract is violated
   */
  public  float[][] process( float[] input) throws IllegalArgumentException, IOException
  {
    //check for null
    if(input == null)
      throw new IllegalArgumentException("input data must not be a null value");

    //check for correct array length
    if((input.length % hopSize) != 0)
        throw new IllegalArgumentException("Input data must be multiple of hop size (windowSize/2).");

    //create return array with appropriate size
    float[][] mfcc = new  float[(input.length/hopSize)-1][];

    //process each window of this audio segment
    for(int i = 0, pos = 0; pos < input.length - hopSize; i++, pos+=hopSize)
    	mfcc[i] = processWindow(input, pos);
    
    return mfcc;
  }

  public int getWindowSize()
  {
    return windowSize;
  }
 
  public  float[] processWindow( float[] window, int start)
  {
	  if(start < 0)
	      throw new IllegalArgumentException("start must be a positve value");
	  //check window size
	  if(window == null || window.length < windowSize)
		  throw new IllegalArgumentException("the given data array must not be a null value and must contain data for one window");
	  	  
	  
	  final float[] buffer = normalizedPowerFFT.transform(window,start);
	  	  	 
	  for (int i = 0; i < ret.length; ++i)
	  {
		  ret[i] = 0;
		  for (int j = 0; j < buffer.length;  ++j)
		  {
			  ret[i] += melFilterBanks.A[i][j] * buffer[j];
		  }
		  ret[i] = (ret[i] < 1.0f ) ? 0f :  (float) (log10 * Math.log(ret[i]));
	  }
	  
	  float [] result =  new  float[dctMatrix.getRowDimension()];
	  
	  for (int i = 0; i < result.length; ++i)
	  {
		  result[i] = 0;
		  for (int j = 0; j < ret.length; ++j)
		  {
			  result[i] += dctMatrix.A[i][j] * ret[j]; 
		  }
	  }	  
	  return result;
  }
}
