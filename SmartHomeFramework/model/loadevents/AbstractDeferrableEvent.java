package loadevents;

import appliances.AbstractAppliance;

public abstract class AbstractDeferrableEvent extends AbstractLoadEvent{

	protected int endTimeslot;
	
	public AbstractDeferrableEvent(AbstractAppliance appliance, String eventName, double totalPreference, int endTimeslot, int runLength, boolean interruptable, boolean critical) {
		super(appliance, eventName, totalPreference, runLength, interruptable, critical);
		this.endTimeslot = endTimeslot;
	}
	
	public int getEndTimeslot() {return endTimeslot;}
	
	
}
