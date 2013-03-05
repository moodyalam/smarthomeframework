package models;

import storage.IMobileStorage;
import electricvehicle.AbstractElectricVehicle;

public class ElectricVehicle extends AbstractElectricVehicle{

	public ElectricVehicle(String name, IMobileStorage storage) {
		super(name, storage);
	}

}
