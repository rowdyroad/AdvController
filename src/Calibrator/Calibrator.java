package Calibrator;

import Common.Source;

public abstract class Calibrator implements Source.AudioReceiver {
	
	protected Source source_;
	
	public Calibrator(Source source)
	{
		source_ = source;
	}
	
	public void Process()
	{
		source_.Process();
		OnComplete();
	}
	
	abstract void OnComplete();
}
