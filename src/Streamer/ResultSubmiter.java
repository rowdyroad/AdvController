package Streamer;

import java.util.LinkedList;
import java.util.List;

import Common.Dbg;
import Common.Utils;
import Streamer.Resulter;

public class ResultSubmiter implements Resulter {

	final float ALLOWED_EQU = 0.1f;
	private String key_;
	
	Thread thread_;
	long  begin_ = 0;
	long  end_ = 0;
	float equ_ = 0;
	String id_ = null;
	
	public ResultSubmiter(String key)
	{
		key_ = key;
		thread_= new Thread(new Runnable(){

			@Override
			public void run()
			{
				while (thread_.isAlive())
				{
					if (end_  >=  System.currentTimeMillis()  + 1000)
					{
							collect_found();
					}				
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				Dbg.Warn("Interrupterd");
			}});
		
		thread_.setDaemon(true);
		thread_.start();
	}
	
	
	synchronized void collect_found()
	{		
		if (id_ != null && equ_ > ALLOWED_EQU)
		{
			Dbg.Info("!!! %s %f",id_, equ_);
			run();
		}		
		id_ = null;
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
	public boolean OnFound(String id, long  begin, long  end,  float equ) {
		// TODO Auto-generated method stub
		
		//if (equivalence < 0.2) return false;
		//Dbg.Info("!!! Found Key: %s\nId: %s\nTimestamp : %d\nEquivalence: %f",key_, id, timestamp, equivalence );
		
		/*Dbg.Info("ID: %s CID: %s\nTS: %d / %d\nCTS: %d / %d\nE: %f CE: %f",
				id,
				id_,
				begin,
				end,
				begin_, 
				end_, 
				equ,
				equ_);
		*/
		if (begin >=  end_)
		{
			//Dbg.Info("2");
			collect_found();
			id_ = id;
			begin_ = begin;
			end_ =  end;
			equ_ = equ;
			return true;
		}
		else
		{		
		//	Dbg.Info("1");
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
