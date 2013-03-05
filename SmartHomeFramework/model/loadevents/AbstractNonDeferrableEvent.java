package loadevents;

import appliances.AbstractAppliance;

public abstract class AbstractNonDeferrableEvent extends AbstractLoadEvent{
	protected final int startTimeslot;
	
	public AbstractNonDeferrableEvent(AbstractAppliance appliance, String eventName, double totalPreference, 
			int startTimeslot, int runLength,  boolean critical)	
	{
		super(appliance, eventName, totalPreference, runLength, false, critical);
		this.startTimeslot = startTimeslot;		
	}

	//TODO: Take this segment out
	
/*	
	public int[] calculateActiveTimeslots(){
		int temp [] = new int[timeslots];
		for (int i=0; i<timeslots; i++)
			if(i==startTimeslot)
				for (int k=0; k<timeslots; k++)
					temp[(i+k)%timeslots]=1;		
		return temp;
			
	}
*/
	public int getStartTimeslot(){return startTimeslot;}
	
}
