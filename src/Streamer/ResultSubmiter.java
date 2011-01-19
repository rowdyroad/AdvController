package Streamer;

import Common.Dbg;
import Common.Utils;
import Streamer.Resulter;

public class ResultSubmiter implements Resulter {

	private String key_;
	public ResultSubmiter(String key)
	{
		key_ = key;
	}
	private String prepareForRun(String program, String id, long timestamp, float equivalence)
	{
		program = program.replaceAll("\\{key\\}", key_);
		program = program.replaceAll("\\{id\\}", id);
		program = program.replaceAll("\\{equivalence\\}",String.format("%f",equivalence));
		return program.replaceAll("\\{timestamp\\}",  String.valueOf(timestamp));
	}
	
	@Override
	public boolean OnFound(String id, long timestamp,  float equivalence) {
		// TODO Auto-generated method stub
		
		//if (equivalence < 0.2) return false;
		
		//Dbg.Info("!!! Found Key: %s\nId: %s\nTimestamp : %d\nEquivalence: %f",key_, id, timestamp, equivalence );
		Dbg.Info("!!! %s %f",id, equivalence );
		String path = prepareForRun(Config.Instance().ExternalProgram(),id, timestamp, equivalence);
		Dbg.Debug(path);
		
		try {
			Process p = Runtime.getRuntime().exec(path);
			int ret = p.waitFor();
			Dbg.Info("Return code: %d",ret);			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return true;
	}

}
