package Splitter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import Common.Dbg;

public class DetectorLoader implements ILoader {
	
	public interface Resulter
	{
		void OnDetected(int id, float weight);
	}
	
	private String detector_;
	private Resulter resulter_;
	private Boolean delete_;
	
	public DetectorLoader(String detector,Resulter resulter, boolean delete) throws FileNotFoundException
	{
		if (!new File(detector).exists())
		{
			throw new FileNotFoundException("Detector "+detector+" is not exists");
		}
		detector_ = detector;		
		resulter_ = resulter;
		delete_ = delete;
	}
	
	@Override
	public void LoadProcess(int index, String filename) 
	{
		Process process;
		try {
			process = new ProcessBuilder(detector_, filename).start();		
			BufferedReader  r =new BufferedReader( new InputStreamReader(process.getInputStream()));
			while (true)
			{
				String ret  = r.readLine();
				if (ret == null) break;
					
				if (ret.startsWith("+"))
				{
					String [] data = ret.split(" ");						
						float weight =Float.valueOf(data[1]);
					int  id =Integer.valueOf(data[2]);
					Dbg.Info("[%d] Detected: %d %f",index, id, weight);
					if (id > 0)
					{
						if (resulter_ != null)
						{
							Dbg.Info("Saved to sync");
							resulter_.OnDetected(id, weight);
						}
					}
					else
						Dbg.Info("Ignored");
					
				}					
			}
			process.waitFor();
			if (delete_ )
			{
				new File(filename).delete();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

	