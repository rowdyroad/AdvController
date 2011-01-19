package Streamer;

import Common.Dbg;


class Main {

public static void main (String args []) throws Exception {
	
	if (args.length > 0)
	{
		Common.Config.Arguments = new Common.Args(args);
		Dbg.LogLevel = Common.Config.Instance().LogLevel();		
	}
	new Streamer().Process();
}

}
