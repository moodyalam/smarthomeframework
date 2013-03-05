package storage;

public abstract class AbstractElectricBattery implements IStorage{

	private final String name;
	private final double capacity;
	private final double maxCharge;
	private final double maxDischarge;
	private final double energyStorageLoss;

	public AbstractElectricBattery(String name, double capacity, double maxCharge, double maxDischarge, double energyStorageLoss) {
		this.name = name;
		this.capacity = capacity;
		this.maxCharge = maxCharge;
		this.maxDischarge = maxDischarge;
		this.energyStorageLoss = energyStorageLoss;	
	}

	public double getCapacity() {return capacity;}
	public double getMaxCharge() {return maxCharge;}
	public double getMaxDischarge() {return maxDischarge;}
	public double getEnergyStorageLoss() {return energyStorageLoss;}
	public String getName() {return name;}
	
}
