package generators.extended;

import generators.AbstractGenerator;
import generators.IRenewableGenerator;
import util.Config;
import dataloader.DataLoader;

public class WindTurbine extends AbstractGenerator implements IRenewableGenerator{
	private final double[] generation;
	private final boolean controllableCapacity;
	private final double[] sd = new double[Config.timeslots];
	private final double[] cost = new double[Config.timeslots];
	private final double[] carbon = new double[Config.timeslots];
	
	public WindTurbine(String name, double maxGenCapacity)
	{
		super(name, maxGenCapacity);
		DataLoader dataLoader = new DataLoader(Config.PathToWindDataFile);
		this.generation =  dataLoader.getGeneration();
		this.controllableCapacity = false;
	}
	
	public double[] getGeneration() {return generation;}
	public double[] getGenerationSD(){return sd;}
	public double[] getCarbonCost() { return carbon;}
	public double[] getCost() {return cost;}
	public boolean isControllableCapacity() {return controllableCapacity;}

	public double getMaxCarbonCost() {return 0;}
	public double getMaxCost() {return 0;}
}
