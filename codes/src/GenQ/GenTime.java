package GenQ;

public class GenTime {
	private long start;
	private long end;
	public void Start(){
		start = System.currentTimeMillis();
	}
	public void End(){
		end = System.currentTimeMillis();
	}
	public int getTime(){
		return (int)(end - start);
	}
	
}
