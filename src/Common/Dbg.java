package Common;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Dbg {

	static public DateFormat DateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
	static public OutputStream Output = System.out;
	
	static  public  final int  Error = 1;
	static  public  final int  Warning = 1 << 1;
	static  public  final int  Info = 1 << 2;
	static public final int Debug = 1 << 3;
	
	static public int LogLevel  = Error | Warning | Info;

	static private void Log(int level, String format, Object... args)
	{
		if ((LogLevel & level) == level)
		{
			String frmt = String.format("[ %s ]", DateFormat.format(new Date()));			
			char[] pad = new char[frmt.length()];			
			try {
				String data = String.format(format, args);				
				Arrays.fill(pad,' ');
				String str_pad = new String(pad);
				str_pad = "\n" + str_pad + "\t";								
				data = frmt+"\t"+data.replaceAll("\n",str_pad)+"\n";
				Output.write(data.getBytes());
			} catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	static private void Log(int level, Object object)
	{
		Log(level, "%s",object);
	}
	
	static public void Info(String format, Object... args)
	{
		Dbg.Log(Info, format,args);
	}
	
	static public void Warn(String format, Object... args)
	{
		Dbg.Log(Warning, format,args);
	}
	
	static public void Error(String format, Object... args)
	{
		Dbg.Log(Error, format,args);
	}
	
	static public void Debug(String format, Object... args)
	{
		Dbg.Log(Debug, format, args);
	}
	
	static public void Info(Object object)
	{
		Dbg.Log(Info, object);
	}
	
	static public void Warn(Object object)
	{
		Dbg.Log(Warning, object);
	}
	
	static public void Error(Object object)
	{
		Dbg.Log(Error, object);
	}
	
	static public void Debug(Object object)
	{
		Dbg.Log(Debug,object);
	}
	
	
	
	
}
