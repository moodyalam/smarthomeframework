package loadevents;


public interface AbstractOtherLoads {
	public String getName();
	public abstract double[] getLoadPerTimeslot();
	public abstract double[] getPreferenceVector();
	public abstract boolean isCritical();	
}
