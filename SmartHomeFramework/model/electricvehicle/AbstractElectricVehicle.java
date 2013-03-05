package electricvehicle;

import java.util.ArrayList;

import storage.IMobileStorage;
import storage.IStorage;
import appliances.AbstractAppliance;

public abstract class AbstractElectricVehicle extends AbstractAppliance implements IEletricVehicale{

	protected IMobileStorage storage = null;
	protected ArrayList<Journey> journeys = new ArrayList<Journey>();
	
	private AbstractElectricVehicle(String name, double kW) {
		super(name, kW);
	}	
	public AbstractElectricVehicle(String name, IMobileStorage storage)
	{
		this(name, storage.getMaxCharge());
		this.storage = storage;
		storage.setEV(this);
	}
	
	public void addJounery(Journey journey)
	{
		journeys.add(journey);
	}
	public int getNumberOfJourneys(){return journeys.size();}
	public ArrayList<Journey> getJourneys(){return journeys;}
	public IStorage getStorage(){return storage;}
}







