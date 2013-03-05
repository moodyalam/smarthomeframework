package electricvehicle;

import java.util.ArrayList;

public class Journey {
	
	protected DepartureEvent departure = null;
	protected ArrivalEvent arrival = null;
	protected ArrayList<Integer> journeyTimeslots = new ArrayList<Integer>();
	
	public Journey(DepartureEvent departure, ArrivalEvent arrival) {
	
	//TODO: Travelling Events must not overlap
	//Departure timeslot must precede arrival timeslot - you can't arrive unless you departed earlier
	if (departure==null || arrival==null || departure.getTimeslot() >=arrival.getTimeslot())
		System.out.println("Departure timeslot must precede arrival timeslot - you can't arrive unless you departed earlier");		
	
	//TODO: Validate whether power requirements corresponds to the charging/discharging rate
	//E.g. A user shouldn't demand the battery charged to the full within one timeslot (not enough time to charge the battery)
	
	
	this.departure = departure;
	this.arrival = arrival;
	
	for(int i=departure.getTimeslot(); i<arrival.getTimeslot(); i++)
		journeyTimeslots.add(i);
	}
	
	public boolean containsTimeslot(int timeslot){return journeyTimeslots.contains(timeslot);}
	public DepartureEvent getDepartureEvent(){return departure;}
	public ArrivalEvent getArrivalEvent(){return arrival;}
	public ArrayList<Integer> getJourneyTimeslots(){return journeyTimeslots;}
	
}
