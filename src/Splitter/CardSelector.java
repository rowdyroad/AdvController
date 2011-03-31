package Splitter;

import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import Common.Dbg;

 class LineInfo
{
	private final TargetDataLine line_;
	private final  Mixer.Info mixer_;			
	public LineInfo(Mixer.Info mixer, TargetDataLine line)
	{
		mixer_ = mixer;
		line_ = line;
	}
	
	public final TargetDataLine Line()
	{
		return line_;
	}
	public final Mixer.Info Mixer()
	{
		return mixer_;
	}	
}
public class CardSelector
{
		
		
		private static List<LineInfo> getLines(AudioFormat format)
		{
			 List<LineInfo> lines = new LinkedList<LineInfo>();
			Mixer.Info[]  mixers = AudioSystem.getMixerInfo();			
			for (Mixer.Info mixer: mixers)
			{
				try
				{
					TargetDataLine l = AudioSystem.getTargetDataLine(format, mixer);				
					lines.add(new LineInfo(mixer, l));					
				}
				catch (Exception e)
				{
					//Dbg.Warn("Unsupported line %s",line.getName());
					continue;
				}
			}			
			if (!lines.isEmpty())
			{
				lines.remove(0);
			}
			return lines;
		}
		
		public static List<LineInfo> List(AudioFormat format)
		{
			return getLines(format);
		}
		public static TargetDataLine GetSource(AudioFormat format, int index)
		{
			if (index < 0) return null;		
			List<LineInfo> lines = getLines(format);			
			if (index >= lines.size()) return null;
			return lines.get(index).Line();			
		}
		
		public static TargetDataLine GetSource(AudioFormat format, String name)
		{
			List<LineInfo> lines = getLines(format);			
			for (LineInfo  line: lines)
			{
				if (line.Mixer().toString().contains(name))
				{
					return line.Line();
				}
			}			
			return null;
		}
}
