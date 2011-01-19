package Streamer;

import java.util.LinkedList;
import java.util.List;

import Common.Dbg;
import Common.Utils;
import Streamer.Resulter;

public class ResultSubmiter implements Resulter {

	private String key_;
	
	
	private class TimePeriod
	{
		int begin_timestamp;
		int end_timestamp;
		float equivalence;
		String id;
		
		public TimePeriod(String id, int begin_timestamp, int end_timestamp, float equivalence)
		{
			this.id = id;
			this.begin_timestamp = begin_timestamp;
			this.end_timestamp = end_timestamp;
			this.equivalence = equivalence;
		}
	}
	
	List<TimePeriod> time_periods_ = new LinkedList<TimePeriod>();
	
	
	int begin_ = 0;
	int end_ = 0;
	float equ_ = 0;
	String id_ = null;
	
	public ResultSubmiter(String key)
	{
		key_ = key;
	}
	private String prepareForRun(String program, String id, long timestamp, int equivalence)
	{
		program = program.replaceAll("\\{key\\}", key_);
		program = program.replaceAll("\\{id\\}", id);
		program = program.replaceAll("\\{equivalence\\}",String.format("%d",equivalence));
		return program.replaceAll("\\{timestamp\\}",  String.valueOf(timestamp));
	}
	
	private int run()
	{
		String path = prepareForRun(Config.Instance().ExternalProgram(),id_, begin_,  Math.round(equ_  * 100));
		Dbg.Debug(path);
		
		try {
			Process p = Runtime.getRuntime().exec(path);
			int ret = p.waitFor();
			Dbg.Info("Return code: %d",ret);
			return ret;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return -1;
		}
	}
	
	@Override
	public boolean OnFound(String id, int  begin, int end,  float equ) {
		// TODO Auto-generated method stub
		
		//if (equivalence < 0.2) return false;
		
		//Dbg.Info("!!! Found Key: %s\nId: %s\nTimestamp : %d\nEquivalence: %f",key_, id, timestamp, equivalence );
		
		Dbg.Debug("TS: %d / %d\nCTS: %d / %d\nE: %f CE: %f",
				begin,
				end,
				begin_, 
				end_, 
				equ,
				equ_);
		
		if (begin >=  end_)
		{
			if (id_ != null)	
			{
				if (equ_ > 0.2)
				{
					Dbg.Info("!!! %s %f",id_, equ_);
					run();
				}				
				id = null;
			}			
			id_ = id;
			begin_ = begin;
			end_ =  end;
			equ_ = equ;
			return true;
		}
		else
		{		
			if (equ_  <  equ) 
			{
				id_ = id;
				begin_ = begin;
				end_ =  end;
				equ_ = equ;	
			}			
		}
		
		return false;
		
		
		

	}

}
