package Streamer;

import java.io.File;
import java.io.FileInputStream;

import Common.Args;
import Common.Dbg;
import Common.JSON.JSONObject;


class Main {

	private static volatile boolean done_ = false;
	
	public static boolean IsDone()
	{
		return done_;
	}
	
	public static void main (String args []) throws Exception 
	{
		
		try
		{
			Args.Init(args);
			Common.Config.Init(Args.Instance().Get("c","streamer.ini"), "streamer");		
			Dbg.LogLevel = Common.Config.Instance().LogLevel();		
			long time  = System.currentTimeMillis();
			new Streamer().Process();	
			Dbg.Info("\n\n%d",System.currentTimeMillis() - time);
			Dbg.Info("==============================");
		}
		finally
		{
			done_ = true;
		}
	}

}
