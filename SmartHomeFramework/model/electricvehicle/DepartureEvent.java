package electricvehicle;

public class DepartureEvent extends AbstractTravelEvent
{
	public DepartureEvent(int timeslot, double storedEnergy)  {
		super(timeslot, storedEnergy);
	}
}