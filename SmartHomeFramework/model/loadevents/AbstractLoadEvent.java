package loadevents;

import appliances.AbstractAppliance;

public abstract class AbstractLoadEvent implements ILoadEvent{
	
	protected final String eventName;
	protected final AbstractAppliance appliance;
	protected final double totalPreference;
	protected final int runLength;
	protected final boolean interruptable;
	protected final boolean critical;
	
	public AbstractLoadEvent(AbstractAppliance appliance, String eventName, double totalPreference, int runLength, boolean interruptable, boolean critical) {
		this.appliance = appliance;
		this.eventName = eventName;
		this.totalPreference = totalPreference;
		this.runLength = runLength;
		this.interruptable = interruptable;
		this.critical = critical;		
	}
	
	public abstract double[] getPreferenceVector(int timeslots);
	
	
	public double getMaxPowerRequired() { return appliance.getMaxPower();}
	public double getTotalPreference(){return totalPreference;}
	public String getName() { return appliance.getName()+"-"+eventName;}
	public int getRunLength(){return runLength;}
	public AbstractAppliance getAppliance(){return appliance;}
	public boolean isInterruptable(){return interruptable;}
	public boolean isCritical(){return critical;}
	
}
