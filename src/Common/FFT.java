package Common;

public final class FFT
{
	public static final int FFT_FORWARD = -1;
	private int windowSize;
	private static final double twoPI = 2 * Math.PI;
	private double[] windowFunction;

	public FFT(int windowSize)
	{
		this.windowSize = windowSize;
		windowFunction = new double[windowSize];
		for (int i =0; i <windowSize; ++i)
		{
			double w = twoPI * i / windowSize;
			windowFunction[i] =  (0.3635819 - 0.4891775*Math.cos(w) + 0.1365995*Math.cos(2*w) - 0.0106411*Math.cos(3*w));
		}
	}

	public void Transform(double[] re)
	{
		if(re.length != windowSize)
			throw new IllegalArgumentException("data array smaller than fft window size");

		for (int i =0; i < re.length; ++i)
		{
			re[i] =  re[i] * windowFunction[i];
		}
		
		fft(re, new double[windowSize], FFT_FORWARD);
	}

	private void fft(double re[], double im[], int direction)
	{
		int n = re.length;
		int bits = (int)Math.rint(Math.log(n) / Math.log(2));

		if (n != (1 << bits))
			throw new IllegalArgumentException("fft data must be power of 2");

		int localN;
		int j = 0;
		for (int i = 0; i < n-1; i++)
		{
			if (i < j)
			{
				double temp = re[j];
				re[j] = re[i];
				re[i] = temp;
				temp = im[j];
				im[j] = im[i];
				im[i] = temp;
			}

			int k = n / 2;

			while ((k >= 1) &&  (k - 1 < j))
			{
				j = j - k;
				k = k / 2;
			}

			j = j + k;
		}

		for(int m = 1; m <= bits; m++)
		{
			localN = 1 << m;
			double Wjk_r = 1;
			double Wjk_i = 0;
			double theta = twoPI / localN;
			double Wj_r = Math.cos(theta);
			double Wj_i = direction * Math.sin(theta);
			int nby2 = localN / 2;
			for (j = 0; j < nby2; j++)
			{
				for (int k = j; k < n; k += localN)
				{
					int id = k + nby2;
					double tempr = Wjk_r * re[id] - Wjk_i * im[id];
					double tempi = Wjk_r * im[id] + Wjk_i * re[id];
					re[id] = re[k] - tempr;
					im[id] = im[k] - tempi;
					re[k] += tempr;
					im[k] += tempi;
				}
				double wtemp = Wjk_r;
				Wjk_r = Wj_r * Wjk_r  - Wj_i * Wjk_i;
				Wjk_i = Wj_r * Wjk_i  + Wj_i * wtemp;
			}
		}
	}
}