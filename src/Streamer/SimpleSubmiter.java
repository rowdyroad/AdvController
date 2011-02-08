package Streamer;

import Common.Dbg;

public class SimpleSubmiter implements Resulter,Runnable {

	private String key_;
	private long last_begin_timestamp_;
	private long last_end_timestamp_;
	private float last_probability_;
	private String last_id_;
	
	private float last_ = 0;
	private boolean has_maxed_ = false;
	private volatile long last_time_ = System.currentTimeMillis();
	
	
	public SimpleSubmiter(String key)
	{
		key_ = key;
		
		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}
	
	private String prepareForRun(String program, String id, long timestamp, int probability)
	{
		program = program.replaceAll("\\{key\\}", key_);
		program = program.replaceAll("\\{id\\}", id);
		program = program.replaceAll("\\{probability\\}",String.format("%d",probability));
		return program.replaceAll("\\{timestamp\\}",  String.valueOf(timestamp));
	}
	

	private void execute(String id, long begin_timestamp, long end_timestamp, float probability)
	{
		String path = prepareForRun(Config.Instance().ResultProgram(),id, begin_timestamp / 1000, (int) (last_ *100));
		Dbg.Info(path);		
		try {
			Process p = Runtime.getRuntime().exec(path);
			int ret = p.waitFor();
			Dbg.Info("Return code: %d",ret);
		} 
		catch (Exception e)
		{
			
		}
		

		last_ = 0;
		has_maxed_ = false;

	}
	public boolean OnFound(String id, long begin_timestamp, long end_timestamp, float probability) 
	{			
		last_id_ = id;
		last_begin_timestamp_ = begin_timestamp;
		last_end_timestamp_ = end_timestamp;
		last_probability_ = probability;
		last_time_ = System.currentTimeMillis();
		if (last_ > probability)
		{
			if (has_maxed_)
			{
				execute(id,begin_timestamp, end_timestamp, probability);
				return true;
			}
		}
		else
		{
			has_maxed_ = true;
		}		
		last_ = probability;
		return false;
	}

	@Override
	public void run() 
	{		
		while (true)
		{
				if (System.currentTimeMillis() - last_time_ > 5000 && has_maxed_)
				{
						execute(last_id_, last_begin_timestamp_, last_end_timestamp_, last_probability_);
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
	}
}
