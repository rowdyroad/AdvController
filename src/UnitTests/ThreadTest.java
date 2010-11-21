package UnitTests;

import Common.Utils;

public class ThreadTest implements Runnable{

	private int i = 0;
	private Object o = new Object();
	private Thread thread_, thread2_;
	public ThreadTest()
	{
		thread_ = new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
					
				while(true)
				{
					synchronized(o)
					{
						try {
							o.wait();
							Utils.Dbg("%d",i);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}
		
			}});
		
		thread2_ = new Thread(new Runnable(){

			@Override
			public void run() {
				
				while(true)
				{
					synchronized(o)
					{
						++i;
						o.notify();
					}
				}

			}});
		
		thread_.start();
		thread2_.start();
	}
	
	public synchronized void z()
	{
		try {
			thread_.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public synchronized void run() {
		// TODO Auto-generated method stub
		while (true)
		{
			++i;
			Utils.Dbg("run:%d",i);
			notify();
		}
		
	}

}
