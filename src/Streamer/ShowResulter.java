package Streamer;
import Capturer.Capturer;
import Common.Utils;





	public class ShowResulter implements Capturer.Resulter
	{

		@Override
		public void OnResult(Capturer id, double equivalence, long timestamp) {
			// TODO Auto-generated method stub
			if (equivalence < 0.50) return;
			System.out.printf("%s:  %s -  %.03f\n", Utils.Time(timestamp), id.GetId(), equivalence);
		}
	}
