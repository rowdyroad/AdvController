package Common;

import java.util.Arrays;

public class Overlapper 
{
	double[] data_;
	private int window_size_;
	private int overlapped_length_;
	private int data_length_;
	public Overlapper(int windowSize,int overlappLength) 
	{
		window_size_ = windowSize;
		data_ = new double[windowSize];
		overlapped_length_ = overlappLength;
		data_length_ = windowSize - overlapped_length_;
	}
	
	private int current_data_ = 0;
	private int current_data_index_ = 0;
	
	public void Clear()
	{
		current_data_ = 0;
		current_data_index_ = 0;
		Arrays.fill(data_,0);
		data_length_ = window_size_  - overlapped_length_;
	}
	
	public double[] Overlapp(double [] data)
	{
			if (current_data_ != data.hashCode())
			{
				current_data_index_ = 0;
				current_data_ = data.hashCode();
			}

			
			int read_len = Math.min(data.length - current_data_index_, data_.length - data_length_);
			System.arraycopy(data, current_data_index_, data_, data_length_, read_len);
			current_data_index_+=read_len;
			data_length_+=read_len;
			
			if (data_length_ == window_size_)
			{
				double []ret = Arrays.copyOf(data_, window_size_);
				System.arraycopy(data_,overlapped_length_, data_, 0, data_length_ - overlapped_length_);
				data_length_ -= overlapped_length_;
				return ret;
			}
			return null;
	}
	
}


