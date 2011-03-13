package Common;

public class Chunker implements Source.AudioReceiver 
{
	private final int silentTime_;
	private int count_;
	private long index_ = 0;
	private final float killGate_;
	private float[] data_;
	private int data_index_ = 0;
	private float sum_ = 0.0f;
	private float max_ = 0.0f;
	private Source.AudioReceiver  catcher_;

	public Chunker(int silentTime, int  totalLength, float killGate,  Source.AudioReceiver  catcher)
	{
		silentTime_ = silentTime;
		killGate_ = killGate;
		data_ = new float[totalLength];
		catcher_ = catcher;
		count_ = silentTime;
	}
	
	private void process()
	{
		if (data_index_ == 0) return;		
		final float mean = sum_ / data_index_;
		int last = data_index_ - 1;
		
		for (; last >=0; --last)
		{
			if (data_[last] >= mean) break;
		}
		
		int first = 0;				
		for (; first < data_.length; ++first)
		{
			if (data_[first] >= mean) break;
		}
		
		if (last <= first) return;
		++last;
		
		float[] data = new float[last - first];
		for (int i = first,j =0 ;i < last; ++i, ++j)
		{
			data[j] = data_[i] / max_;			
		}
		catcher_.OnSamplesReceived(data);
	}
	
	
	@Override
	public void OnSamplesReceived(float[] db) 
	{	
		for (int i  = 0;  i < db.length; ++i,++index_) 
		{
			if (data_index_ >= data_.length) data_index_ = 0;
			data_[data_index_++] = db[i];
			
			max_ = Math.max(max_, Math.abs(db[i]));
			sum_ += Math.abs(db[i]);
			
			if (Math.abs(db[i]) <= killGate_)
			{
				++count_;
			}
			else
			{							
				if (count_ >= silentTime_)
				{									
					Dbg.Info("Pos:%d Len:%d", index_  / 44100, data_index_ / 44100);
					process();
					count_ = 0;
					data_index_ = 0;
					max_ = 0.0f;
					sum_ = 0.0f;
				}
				else
				{				
					count_ = 0;
				}
			}
		}
	}

	@Override
	public void OnCompleted() 
	{
		process();
	}

}
