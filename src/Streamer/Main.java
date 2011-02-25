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
		long time  = System.currentTimeMillis();
		try
		{
			Dbg.Info("Started");
			Args.Init(args);
			Common.Config.Init(Args.Instance().Get("c","streamer.ini"), "streamer");		
			Dbg.LogLevel = Common.Config.Instance().LogLevel();							
			new Streamer().Process();	
		}
		finally
		{
			done_ = true;
			Dbg.Info("Stopped [ %d ms ]",System.currentTimeMillis() - time);
		}
	}

}
