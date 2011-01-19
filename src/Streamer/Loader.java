package Streamer;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import Common.Dbg;
import Common.FingerPrint;
import Common.Utils;

public class Loader implements Runnable {
	
	public interface Processor
	{
		public void AddFingerPrint(FingerPrint fp);
		public void RemoveFingerPrint(FingerPrint fp);
	}
		
	private String directory_;
	private Thread thread_;
	
	private Map<String, TreeMap<String, FingerPrint>> data_ = new TreeMap<String, TreeMap<String, FingerPrint>>();
	
	private Map<String, LinkedList<Processor>> processors_ = new TreeMap<String, LinkedList<Processor>>();
	
	public Loader(String directory)
	{
		directory_ = directory;		
	}
	
	private void removeOld(String key, TreeMap<String,FingerPrint> fps, File[] files)
	{
		Iterator<Entry<String, FingerPrint>> it = fps.entrySet().iterator();
		
		while (it.hasNext())
		{
			Entry<String,FingerPrint> kvp = it.next();
			boolean found = false;

			for (int i = 0; i < files.length; ++i)
			{
				if (files[i].getName().compareTo(kvp.getKey())==0)
				{
					found = true;
					break;
				}
			}
			
			if (! found)
			{
				LinkedList<Processor> ps = processors_.get(key);
				if (ps != null)
				for (Processor p : ps)
				{
					p.RemoveFingerPrint(kvp.getValue());
				}
				
				Dbg.Info("\tRemove %s",kvp.getKey());
				it.remove();
			}
		}
	}
	
	private void load()
	{
		for (String key : processors_.keySet())
		{
			File dir = new File(directory_+"/"+key);
			if (dir == null || !dir.exists() || !dir.isDirectory())
			{
				//Utils.Dbg("Couldn't load files from %s",key);
				continue;
			}
			
			TreeMap<String,FingerPrint> fps  = data_.get(dir.getName());
			
			if (fps == null)
			{
				fps = new TreeMap<String,FingerPrint>(); 
				data_.put(dir.getName(),fps);
			}
			
			File[] files = dir.listFiles();
			removeOld(dir.getName(), fps, files);
			for (File file : files)
			{	
				try {
					FingerPrint fp = fps.get(file.getName());
					if (fp == null)
					{
						Dbg.Debug("Loading  %s..",file.getName());
						FingerPrint nfp = FingerPrint.Deserialize(file);
						fps.put(file.getName(), nfp);
						Dbg.Debug("Loaded %s\n",nfp.Id());

						LinkedList<Processor> ps = processors_.get(dir.getName());
						if (ps != null)
						for (Processor p : ps)
						{
							p.AddFingerPrint(nfp);
						}
					}
				} catch (IOException e) {
					continue;
				} catch (ClassNotFoundException e) {
					continue;
				}
			}
		}
	}
	
	public void AddProcessor(String key, Processor processor)
	{
		LinkedList<Processor> ps = processors_.get(key);
		if (ps == null)
		{
			ps = new LinkedList<Processor>();
			processors_.put(key,ps);
		}
		ps.add(processor);
	}
	
	
	public void Process()
	{
		load();
		thread_ = new Thread(this);
		thread_.setDaemon(true);
		thread_.start();		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub		
		while (true)
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
			load();
		}
	}
}
