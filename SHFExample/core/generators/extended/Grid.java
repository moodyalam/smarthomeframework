package generators.extended;

import generators.AbstractGenerator;

import java.util.Arrays;

import models.Tariff;
import util.Config;

public class Grid extends AbstractGenerator{

	private final double[] generation;
	private final Tariff tariff;
	
	public Grid(Tariff tariff)
	{
		super("Grid", tariff.getMaxPowerLimit());
		this.generation =  new double[Config.timeslots];
		this.tariff = tariff;
		Arrays.fill(generation, tariff.getMaxPowerLimit());
	}
	
	
	public double[] getGeneration() {return generation;}
	public double[] getCarbonCost() { return tariff.getCarbonCost();}
	public double[] getCost() {	return tariff.getCost();}

	public double getMaxCarbonCost() {return 0;}
	public double getMaxCost() {return 0;}
}
