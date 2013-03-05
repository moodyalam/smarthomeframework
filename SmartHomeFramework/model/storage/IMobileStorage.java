package storage;

import electricvehicle.AbstractElectricVehicle;

public interface IMobileStorage extends IStorage{
	void setEV(AbstractElectricVehicle ev);
	AbstractElectricVehicle getEV();
	
}
