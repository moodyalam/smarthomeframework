package models;

import loadevents.AbstractOtherLoads;
import util.Config;


public class BaseLoad implements AbstractOtherLoads{
	
	private final String name="SimpleLoad";
	private final boolean isCritical;
	private double[] load;
	private double[] pref;
	
	public BaseLoad(boolean isCritical){
		this.load = new double[Config.timeslots];
		this.pref = new double[Config.timeslots];
		this.isCritical = isCritical;
	}
	
	public BaseLoad(double[] load, double[] pref, boolean isCritical)
	{
		this.load = load;
		this.pref =pref;
		this.isCritical = isCritical;
	}
	
	public String getName() {return name;}
	public double[] getLoadPerTimeslot() { return load;}
	public double[] getPreferenceVector() {return pref;}
	public boolean isCritical() {return isCritical;}	
	
}
