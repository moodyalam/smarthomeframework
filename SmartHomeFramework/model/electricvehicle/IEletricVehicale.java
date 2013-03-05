package electricvehicle;

import java.util.ArrayList;

import storage.IStorage;
import appliances.IAppliance;

public interface IEletricVehicale extends IAppliance{
	void addJounery(Journey journey);
	int getNumberOfJourneys();
	ArrayList<Journey> getJourneys();
	IStorage getStorage();
}
