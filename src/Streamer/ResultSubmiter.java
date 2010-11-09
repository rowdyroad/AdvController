package Streamer;

import java.io.IOException;

import Common.Utils;
import Streamer.Comparer.Resulter;

public class ResultSubmiter implements Resulter {

	private String prepareForRun(String program, String id, long timestamp, double equivalency)
	{
		program = program.replaceAll("\\{id\\}", id);
		program =  program.replaceAll("\\{equivalency\\}",  String.format("%.03f",equivalency));
		return program.replaceAll("\\{timestamp\\}",  String.valueOf(timestamp));
	}
	
	@Override
	public boolean OnFound(String id, long timestamp, double equivalency) {
		// TODO Auto-generated method stub
		Utils.Dbg("Id: %s TS: %d %f", id, timestamp,equivalency );
		
		String path = prepareForRun(Config.Instance().ExternalProgram(),id, timestamp, equivalency);
		Utils.Dbg(path);
		
		try {
			Runtime.getRuntime().exec(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

}
