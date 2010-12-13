package Streamer;

import java.io.File;
import Common.FingerPrint;
import Common.Utils;

class Main {




public static void main (String args []) throws Exception {
	
	if (args.length > 0)
	{
		Common.Config.Filename = args[0];
	}
	new Streamer().Process();
}

}
