package Streamer;

public interface Resulter
{
	public boolean OnFound(String id, long  begin_timestamp, long  end_timestamp, float equivalence);
}