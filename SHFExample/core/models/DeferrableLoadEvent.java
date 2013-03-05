package models;

import loadevents.AbstractDeferrableEvent;
import appliances.AbstractAppliance;

public class DeferrableLoadEvent extends AbstractDeferrableEvent{

	private double[] preferenceVector;

	private boolean preferenceCalculated=false;
	
	
	public DeferrableLoadEvent(AbstractAppliance appliance, String eventName, double preference, 
			int endTimeslot, int runLength, boolean interruptable, boolean critical) {		 
		super(appliance, eventName, preference,endTimeslot, runLength, interruptable, critical);		 		 
	}
	
	public DeferrableLoadEvent withPreference(double[] preferenceOverTimeslots)
	{
		this.preferenceVector = preferenceOverTimeslots;
		return this;
	}	
	public double[] getPreferenceVector(int timeslots) {
		//What we want is to get totalPreference/runLength each timeslot before the deadline
		//constraints will make sure that active timeslots are not more than the run length
		
		if(preferenceCalculated)
			return preferenceVector;
		
		preferenceVector = new double[timeslots];
		double preferencePerTimeslot = totalPreference/runLength;
		
		for(int i=0; i<endTimeslot; i++)
			preferenceVector[i] = preferencePerTimeslot; 
		
		preferenceCalculated=true;
		return preferenceVector;
	}
}
	
