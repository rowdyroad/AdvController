package Capturer;
import java.io.File;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import Common.Config;
import Common.FingerPrint;
import Common.Frequency;
import Common.Settings;
import Common.Source;
import Common.Frequencier;
import Common.Utils;
import Common.Source.Channel;

public class Capturer implements Frequencier.Catcher
{
	private long time_ = 0;
	Source source_ = null;
	FingerPrint fp_;
	private Settings settings_;
	
	public Capturer(String filename, String id) throws Exception
	{
		AudioInputStream stream = AudioSystem.getAudioInputStream(new File(filename));
		settings_ = new Settings(stream.getFormat());
		
		source_ = new Source(stream, settings_);
		source_.RegisterAudioReceiver(Channel.LEFT_CHANNEL, new Frequencier(this,settings_,settings_.WindowSize()));																					
		fp_ = new FingerPrint(id);		
		Utils.Dbg("FrameLength:%d",stream.getFrameLength());
	}

	public FingerPrint Process()
	{
		Utils.Dbg("Working...");
		source_.Process();
		Utils.Dbg("The job is done");
		fp_.ThinOut();	
		Utils.Dbg(fp_);
		return fp_;
	}

	Vector<double[]> list = new Vector<double[]>();
	@Override
	public boolean OnReceived(Vector<double[]> frequency, long timeoffset) 
	{
		fp_.Add(frequency, time_);
		time_+=timeoffset;
		return true;
	}

	@Override
	public void OnError() {

	}

}

