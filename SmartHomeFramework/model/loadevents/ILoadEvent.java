package loadevents;

import appliances.AbstractAppliance;

public interface ILoadEvent {

	String getName();
	AbstractAppliance getAppliance();
	int getRunLength();
	boolean isCritical();
	boolean isInterruptable();
	double getTotalPreference();
	double getMaxPowerRequired();
	
	double[] getPreferenceVector(int timeslots);		
}
