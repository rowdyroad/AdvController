package Streamer;

import java.util.LinkedList;
import java.util.List;

import Common.Dbg;
import Common.Utils;
import Streamer.Resulter;

public class ResultSubmiter implements Resulter {
	private String key_;	
	private Thread thread_;
	private long  begin_ = 0;
	private long  end_ = 0;
	private float equ_ = 0;
	private String id_ = null;
	private volatile boolean has_ = false;
	 
	private Object locker_ = new Object();
	
	public ResultSubmiter(String key)
	{
		key_ = key;
		thread_= new Thread(new Runnable(){
			@Override
			public void run()
			{
				while (!Main.done || has_)
				{					
					if (has_)
					{				
							synchronized (locker_)
							{
								final long wait_for = (long) ((end_ - begin_) * Config.Instance().Equals());							
								if (System.currentTimeMillis()  >  end_ + wait_for )
								{
									collect_found();															
								}							
							}
					}					
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}});				
	}
	
	
	 void collect_found()
	{		
		if (id_ != null)
		{
			Dbg.Info("!!! %s %f",id_, equ_);
			
			run();
		}		
		id_ = null;
		has_ = false;
		equ_ = 0;
	}
	private String prepareForRun(String program, String id, long timestamp, int equivalence)
	{
		program = program.replaceAll("\\{key\\}", key_);
		program = program.replaceAll("\\{id\\}", id);
		program = program.replaceAll("\\{probability\\}",String.format("%d",equivalence));
		return program.replaceAll("\\{timestamp\\}",  String.valueOf(timestamp));
	}
	
	private int run()
	{
		String path = prepareForRun(Config.Instance().ResultProgram(),id_, begin_,  Math.round(equ_  * 100));
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
	public boolean OnFound(String id, long  begin, long  end,  float equ) 
	{
		synchronized(locker_)
		{
			Dbg.Info("%s  [%d - %d] %f %s",id,begin,end,equ,has_);
			if (has_ && begin >=  end_)
			{
				collect_found();
				id_ = id;
				begin_ = begin;
				end_ =  end;
				equ_ = equ;
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

			has_ = true;
			
			if (!thread_.isAlive())
			{
				thread_.start();
			}
			return false;
		}
	}

}
