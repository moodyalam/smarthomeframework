package electricvehicle;

public class ArrivalEvent extends AbstractTravelEvent
{
	public ArrivalEvent(int timeslot, double storedEnergy)  {
		super(timeslot, storedEnergy);
	}
}