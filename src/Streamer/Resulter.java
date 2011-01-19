package Streamer;

public interface Resulter
{
	public boolean OnFound(String id, int begin_timestamp, int end_timestamp, float equivalence);
}