package Streamer;

import Capturer.Capturer;



class Main {




public static void main (String args []) throws Exception {
	
		Streamer stm = new Streamer();
//		stm.AddCapturer(new Capturer("d:\\temp\\patterns\\pepsi.wav","Pepsi", new ShowResulter()));
	//	stm.AddCapturer(new Capturer("d:\\temp\\patterns\\axe1.wav","Axe", new ShowResulter()));
	//	stm.AddCapturer(new Capturer("d:\\temp\\patterns\\axe2.wav","Axe", new ShowResulter()));
	//	stm.AddCapturer(new Capturer("d:\\temp\\patterns\\axe3.wav","Axe", new ShowResulter()));
		
//		stm.AddCapturer(new Capturer("d:\\temp\\patterns\\pegeot.wav","Pegeouti", new ShowResulter()));
//		stm.AddCapturer(new Capturer("d:\\temp\\patterns\\pegeot-308.wav","pegeot-308", new ShowResulter()));
//		stm.AddCapturer(new Capturer("d:\\temp\\patterns\\sprite.wav","Sprite", new ShowResulter()));
	//	stm.Process();
		
	  /* MFCC mfcc = new MFCC(8000, 512, 12, true);	 
	   
	   AudioPreProcessor prep = new AudioPreProcessor(GetStreamFromFile(), 8000);
	   while (true)
	   {		
			double[] data = mfcc.process(prep);
			if (data == null)
			{
				break;
			}
			double d = ignore(data);
			if (d > 0)
			{
				System.out.printf("%.03f:\t\t", d);
							printMFCC(data);
			}
		}	
		
		System.out.println("recorded patterns: "+pattern.size());
		
		prep = new AudioPreProcessor(GetAudioStream(), 8000);
		
		while (true)
		{
			double[] data = mfcc.process(prep);
			if (data == null)
			{
				break;
			}
			double d = ignore(data);
			if (d > 20)
			{
				System.out.printf("%.03f:\t\t", d);
					printMFCC(data);
			}
		}
*/		
	/*MFCC mfcc = new MFCC(8000);
	
	Mixer.Info.class.getM
	

	
	
	 int bufferSize = (int) (format.getSampleRate() * format.getSampleSizeInBits()/8);

	 byte buffer[] = new byte[bufferSize];
	 
	 
	  
	  double[] buf = new double[mfcc.getWindowSize()];
	
	  while (externalTrigger) {
	    int count = line.read(buffer, 0, buffer.length);
	    double[] z = frameToSignedDoubles(buffer);
	    
	    
	    for (int i=0;i<z.length; ++i)
	    {
	    	System.out.println(z[i]);
	    }
	  
  	  }*/
	
	
	// Line.Info[] sourceLineInfos = mixer.getSourceLineInfo();

	// for (int j = 0; j < sourceLineInfos.length; j++) {

	// System.out.println("\tsource line info:\t" + sourceLineInfos[j]);

	// setVolume(sourceLineInfos[j]);

	// }


	  
	
	  
	
	  

}

}
