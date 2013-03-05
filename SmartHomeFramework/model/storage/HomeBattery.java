package storage;

public class HomeBattery extends AbstractElectricBattery{
	public HomeBattery(String name, double capacity, double maxCharge, double maxDischarge, double energyStorageLoss) {
		super(name, capacity, maxCharge, maxDischarge, energyStorageLoss);
	}
	
}

