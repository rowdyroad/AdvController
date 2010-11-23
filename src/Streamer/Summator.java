package Streamer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import net.sf.javaml.distance.dtw.DTWSimilarity;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

import com.sun.corba.se.impl.javax.rmi.CORBA.Util;

import Common.FingerPrint;
import Common.Settings;
import Common.Utils;
import Common.Frequencier.Catcher;
import Common.Frequency;

public class Summator implements Catcher{

	private List<Comparer> comparers_ = new LinkedList<Comparer>();
	private Settings settings_ = null;
	private Long time_ = new Long(0); 
	private LinkedList<Frequency[]> list = new LinkedList<Frequency[]>();
	private LinkedList<LinkedList<Frequency>> captures = new LinkedList<LinkedList<Frequency>>();
	private Writer wr;
	private List<FingerPrint> fingerPrints_ = new LinkedList<FingerPrint>();
	public Summator(Settings settings)
	{
		  try {
			wr = new BufferedWriter(new FileWriter(new File("log")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		settings_ = settings;
	}
	
	Vector<double[]> source = new Vector<double[]>();
	
	public void AddFingerPrint(FingerPrint fp)
	{
		for (double[] d: fp.mfcc.get(0))
		{
			source.add(d);
		}
		fingerPrints_.add(fp);
		//comparers_.add(comparer);
	}

	private LinkedList<Frequency> MergeFrequency (Frequency[] frequency)
	{
		if (list.size() >= Common.Config.Instance().OverlappedCoef())
		{
			list.removeFirst();
		}
		list.add(frequency);
		
		if (list.size() < Common.Config.Instance().OverlappedCoef()) return null;
		double max = 0;
		for (Frequency[] f: list)
		{
			max = Math.max(max, f[0].level);
		}
		if (max < 1) return null;
		
		LinkedList<Frequency> pat  = new LinkedList<Frequency>();
		
		for (Frequency[] f: list)
		{
			for (int i = 0; i  < f.length; ++i)
			{
				int index = pat.indexOf(f[i]);
				if (index == -1)
				{
					pat.add(f[i]);
				}
				else
				{
					Double level = pat.get(index).level;
					pat.get(index).level = Math.max(f[i].level,level);
				}
			}
		}
		
		Collections.sort(pat, new Comparator<Frequency>() {
			@Override
			public int compare(Frequency arg0, Frequency arg1) {
				// TODO Auto-generated method stub
				return -arg0.level.compareTo(arg1.level);
			}});
		
		
		while (pat.size() > Common.Config.Instance().LevelsCount())
		{
			pat.removeLast();
		}

		return pat;
	}
	
	
	FingerPrint fp;
	long next =0;
	int total = 0;
	int max = 0;
	int index = 4;
	boolean started = false;
	LinkedList<LinkedList<Frequency>> fr = new LinkedList<LinkedList<Frequency>>();
	
	Vector<double[]> mfcc_ = new Vector<double[]>();
	
	DTW dtw_ = new DTW();
	DTWSimilarity dwt = new DTWSimilarity();
	@Override
	public boolean OnReceived(double[] mfcc, long timeoffset) 
	{
		//	Utils.Dbg("%d %d",time_,mfcc_.size());
		if (mfcc[0] <1 ) return true;
		mfcc_.add(mfcc);
		time_+=timeoffset;
		
	
		if (time_ >= settings_.WindowSize() / 2)
		{
			double x = dtw_.measure(fingerPrints_.get(0).mfcc.get(index), mfcc_);
			//Utils.Dbg("%d: %f / %d",time_, x, index);
			if (x >0.1)
			{
				Utils.Dbg("%d: %f / %d",time_, x,index);
				index++;
			}
			mfcc_.remove(0);
		}
		
		
	
		
		/*Utils.Dbg(frequency);
		LinkedList<Frequency> fr = MergeFrequency(frequency);
		if (fr == null)
		{			
			time_+=timeoffset;
			return true;
		}
		if (captures.size() >= Common.Config.Instance().OverlappedCoef())
		{
			captures.removeFirst();
		}
		captures.add(fr);
		if (captures.size() < Common.Config.Instance().OverlappedCoef()) 
		{
			time_+=timeoffset;
			return true;
		}
		
		for (Comparer c: comparers_)
		{
			c.OnReceived(captures, time_);
		}	*/
		return true;
	}

	@Override
	public void OnError() {
		// TODO Auto-generated method stub

	} 




}
