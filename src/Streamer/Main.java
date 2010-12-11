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

	Streamer stm = new Streamer();
	
	String dirs[] = new File(Config.Instance().PromosPath()).list();
	
	for(int i = 0; i < dirs.length; i++)
	{
		File dir = new File(Config.Instance().PromosPath()+dirs[i]);
		if (! dir.isDirectory()) continue;
		String key = dirs[i];
		Utils.Dbg("Found key: %s",key);
		
		File [] files = dir.listFiles();
		
		for (int j = 0; j < files.length; ++ j)
		{
			try
			{
				Utils.Dbg("\tFound finger print:%s",files[j].getName());
				FingerPrint fp = FingerPrint.Deserialize(files[j]);
				stm.AddFingerPrint(key, fp);
				Utils.Dbg("\tSerialized");
			}
			catch (Exception e)
			{
				Utils.Dbg("\tCould not serialize");
				e.printStackTrace();
			}
		}
	} 
	
	if (stm.Count() ==0)
	{
		Utils.Dbg("Hasn't found any finger print. Nothing to compare");
		System.exit(1);
	}
	stm.Process();
}

}
