package Streamer;

import Common.Dbg;


class Main {

public static void main (String args []) throws Exception {
	
	if (args.length > 0)
	{
		Common.Config.Arguments = new Common.Args(args);
		Dbg.LogLevel = Common.Config.Instance().LogLevel();		
	}
	
	long time  = System.currentTimeMillis();
	new Streamer().Process();
	
	Dbg.Info("\n\n%d",System.currentTimeMillis() - time);
}

}
