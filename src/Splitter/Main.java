package Splitter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;


import Common.Args;
import Common.Dbg;

public class Main {

	public Main(String[] args) 
	{
		Args.Init(args);		
		int soundCardIndex = Args.Get('i',"index", 0);
		int sampleRate = Args.Get('s',"samplerate",48000);
		int channels = Args.Get('c',"channels",2);
		int bitsPerSample = Args.Get('b',"bitspersample",16);
		int windowLength = Args.Get('w',"window",12);
		int step  = Args.Get('S',"step",1);
		boolean deleteRecords = (Args.Get('C',"clean",1) == 1);		
		String recordDir = Args.Get('r',"record-dir","");
		String detectorPath =Args.Get('d',"detector","");
		Dbg.Info("Audio Format: %d/%d %s\nWindow: %d / %d\nCleaning: %s\nRecord Dir: %s\nDetector: %s\n",
				sampleRate,
				bitsPerSample, 
				(channels == 1) ?"Mono":"Stereo" ,
				windowLength, 
				step,
				deleteRecords,
				recordDir,
				detectorPath);
		
		AudioFormat format = new AudioFormat(sampleRate,bitsPerSample,channels,true,false);
		
		List<LineInfo> lines = CardSelector.List(format);
		Dbg.Info("Support record lines:");
		for (LineInfo line: lines)
		{
			Dbg.Info("\t%s",line.Mixer());
		}		
		IDetector detector =  null;
		try 
		{
			if (soundCardIndex < 0 || soundCardIndex >= lines.size())
			{
				throw new Exception(String.format("Incorrect sound card index: %d",soundCardIndex));
			}
			
			Dbg.Info("Use record line: %s",lines.get(soundCardIndex).Mixer());

			if (channels == 1)
			{
				String result_file = Args.Get('f',"result-file","");
				detector = new MonoDetector(format, recordDir, Integer.toString(soundCardIndex), new DetectorLoader(detectorPath, new Resulter(result_file),deleteRecords));
			}
			else if (channels == 2)
			{ 
				String left_result = Args.Get('L',"left",(String)null);
				Dbg.Info("Left result file:%s",left_result);

				ILoader left =  (left_result  == null) ? null :  new DetectorLoader(detectorPath, new Resulter(left_result),deleteRecords);
				String right_result = Args.Get('R',"right",(String)null);
				Dbg.Info("Right result file:%s",right_result);
				ILoader right =  (right_result  == null) ? null :  new DetectorLoader(detectorPath, new Resulter(right_result),deleteRecords);			
				if (left == null && right == null) 	throw new Exception("Undefined result files");
				detector = new StereoDetector(format, recordDir,Integer.toString(soundCardIndex), left,right );				
			}
			else
				throw new Exception("Incorrect channels count");
		} catch (Exception e1) 
		{
			e1.printStackTrace(); 
			System.exit(1);
		}
		
		final int second_length = sampleRate * channels * bitsPerSample / 8;
		final int step_length = second_length * step;
		final int length = second_length *  windowLength;
		
		 TargetDataLine line = null;
			try 
			{
				line = lines.get(soundCardIndex).Line();
				line.open(format);
				line.start();
			} catch (LineUnavailableException e) 
			{
				e.printStackTrace();
				System.exit(1);
			}
			Dbg.Info("Listening..");			
			byte[] cache = new byte[length]; 
			int index =0;
			while (true)
			{				
				byte[] buf  = new byte[length];
				System.arraycopy(cache, 0, buf,0, index);
				while (index < length)
				{
					final int len = line.read(buf,index, length - index);
					if (len == -1)
					{
						System.exit(0);
					}
					index+=len;
				}		
				System.arraycopy(buf, step_length, cache,0, index - step_length);				
				detector.Detect(buf);											
				index-= step_length;				
			}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 new Main(args);
	}

}
