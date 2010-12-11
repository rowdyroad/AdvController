package Streamer;

import Common.FingerPrint;

public class Loader {
	
	public interface Storage
	{
		
		public void AddFingerPrint(String key, FingerPrint fp);
		public void RemoveFingerPrint(String key, FingerPrint fp);
	}
	
	private Storage storage_;
	private String directory_;
	
	
	public Loader(String directory, Storage storage)
	{
		
	}
	
	
	private void load()
	{
		
	}
}
