package Streamer;


class Main {

public static void main (String args []) throws Exception {
	
	if (args.length > 0)
	{
		Common.Config.Filename = args[0];
	}
	new Streamer().Process();
}

}
