package electricvehicle;

public abstract class AbstractTravelEvent
{	
	private int timeslot;
	private double storedEnergy;
	
	public AbstractTravelEvent(int timeslot, double storedEnergy) {
		this.timeslot = timeslot;
		this.storedEnergy = storedEnergy;
	}
	
	public int getTimeslot(){return timeslot;}
	public double getStoredEnergy(){return storedEnergy;}
	
}