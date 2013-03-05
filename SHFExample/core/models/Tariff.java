package models;
import util.Config;
import dataloader.TariffLoader;



public class Tariff {
	private final String name = "Peak-OffPeak Tariff";
	
	private TariffLoader dataLoader = new TariffLoader(Config.PathToTariffFile);
	
	private final double peakRate;
	private final double offpeakRate;
	private final double maxPowerLimit;
	private final double[] cost = new double[Config.timeslots];
	private final double[] carbon; //TODO: unit
	
	
	public Tariff(double maxPowerLimit, double peakRate, double offpeakRate) {
		
		this.peakRate = peakRate;
		this.offpeakRate = offpeakRate;
		this.maxPowerLimit = maxPowerLimit;
		carbon = dataLoader.getCarbon();
		
	}
		
	public double[] getCarbonCost() {
		return carbon;
	}
	
	public double[] getCost()
	{
		//NOTE: we can also load cost details from the tariff file
		
		double[] cost = new double[Config.timeslots];
		
		for(int t=0; t<Config.timeslots; t++)
			if (t>13 && t<47)
				cost[t]=peakRate;
			else
				cost[t]=offpeakRate;
		
		return cost;
	}
	
	public String getName(){return name;}
	public double getMaxPowerLimit() {return maxPowerLimit;}
}
