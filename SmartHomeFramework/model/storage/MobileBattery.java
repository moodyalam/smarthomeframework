package storage;

import electricvehicle.AbstractElectricVehicle;

public class MobileBattery extends AbstractElectricBattery implements IMobileStorage{
	
	private AbstractElectricVehicle ev=null;
	
	
	public MobileBattery(String name, double capacity, double maxCharge, double maxDischarge, double energyStorageLoss) {
		super(name, capacity, maxCharge, maxDischarge, energyStorageLoss);
	}
	
	public void setEV(AbstractElectricVehicle ev){this.ev = ev;}
	public AbstractElectricVehicle getEV() {return ev;}
	
}
