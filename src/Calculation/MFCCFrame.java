package Calculation;

import java.io.Serializable;

public class MFCCFrame implements Serializable {

	private static final long serialVersionUID = 2248054125315248683L;

	public  class Record implements Serializable
	{
		private static final long serialVersionUID = -3180231636619810034L;
		private float[] data_;
		private float mean_  = 0.0f;
		private int index_ = 0;
		private float sum_ = 0;
		
		public Record(int size)
		{
			data_ = new float[size];
		}
		
		public float Mean()
		{
			return mean_;
		}
		
		public float Get(int i)
		{
			return data_[i];
		}
		
		public void Add(float value)
		{
			data_[index_++] = value;
			sum_ += (value * value);
			if (index_  == data_.length)
			{
				mean_ = sum_ / data_.length;
			}
		}
		public int Size()
		{
			return data_.length;
		}
	}

	private Record[] data_;
	private float mean_ = 0;
	private float sum_ = 0;
	private int index_ = 0;
	
	public MFCCFrame(int size)
	{
		data_ = new Record[size];
	}
	
	public float Mean()
	{
		return mean_;
	}
	
	public float Mean(int index)
	{
		return data_[index].Mean();
	}
	
	public float Get(int i, int j)
	{
		return data_[i].Get(j);
	}
	
	public void Add(Record record)
	{
		data_[index_++] = record;
		sum_+=record.Mean();		
	}
	
}
