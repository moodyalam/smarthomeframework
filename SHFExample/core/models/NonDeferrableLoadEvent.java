package models;

import loadevents.AbstractNonDeferrableEvent;
import appliances.AbstractAppliance;

public class NonDeferrableLoadEvent extends AbstractNonDeferrableEvent{

	private double[] preferenceVector;
	private boolean preferenceCalculated;

	public NonDeferrableLoadEvent(AbstractAppliance appliance, String eventName, double totalPreference, int startTimeslot, int runLength, boolean critical) {
		super(appliance, eventName, totalPreference, startTimeslot, runLength, critical);
	}

	@Override
	public double[] getPreferenceVector(int timeslots) {
		if(preferenceCalculated)
			return preferenceVector;
		
		preferenceVector = new double[timeslots];
		double preferencePerTimeslot = totalPreference/runLength;
		
		for(int i=startTimeslot; i<startTimeslot+runLength; i++)
			preferenceVector[i] = preferencePerTimeslot; 
		
		preferenceCalculated = true;
		
		return preferenceVector;
	}

	
	
}
	
