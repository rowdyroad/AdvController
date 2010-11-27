package Streamer;

import java.io.IOException;

import Common.Utils;
import Streamer.Resulter;

public class ResultSubmiter implements Resulter {

	private String prepareForRun(String program, String id, long timestamp)
	{
		program = program.replaceAll("\\{id\\}", id);
		return program.replaceAll("\\{timestamp\\}",  String.valueOf(timestamp));
	}
	
	@Override
	public boolean OnFound(String id, long timestamp) {
		// TODO Auto-generated method stub
		Utils.Dbg("Id: %s TS: %d", id, timestamp );
		
		String path = prepareForRun(Config.Instance().ExternalProgram(),id, timestamp);
		Utils.Dbg(path);
		
		try {
			Runtime.getRuntime().exec(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return true;
	}

}
