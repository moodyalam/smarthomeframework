package storage;

public interface IStorage {

	String getName();
	double getCapacity();
	double getMaxCharge();
	double getMaxDischarge();
	double getEnergyStorageLoss();	
}
