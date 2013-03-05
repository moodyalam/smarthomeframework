package appliances.extended;

import util.Config;
import appliances.AbstractAppliance;

public class HVACSystem{

	int timeslots = Config.timeslots;
	private ThermoOptimiser thermoOptimiser;
	private ElectricHeater[] timeBasedInstances = new ElectricHeater[timeslots];
	private double[] desiredTemps = new double[timeslots];   

	public HVACSystem() {
		this.thermoOptimiser = new ThermoOptimiser();
		desiredTemps = thermoOptimiser.getThermalLoads();

		for (int i = 0; i < timeBasedInstances.length; i++) {
			timeBasedInstances[i]= new ElectricHeater("ElectricHeater", desiredTemps[i]);
		}

	}
	
	public ElectricHeater[] getElectricHeaterIntances(){return timeBasedInstances;}

	private class ThermoOptimiser {

		public double[] getThermalLoads()
		{
			if(Config.timeslots!=48)
				System.out.println("ThermoOptimiser does not support any timeperiod other than 48");

			double[] loads = new double[Config.timeslots];

			for (int i = 0; i < loads.length; i++) {
				if(i<14 || i>36)
				{
					if (i<14)
						loads[i] = 3*Math.random();
					else
						loads[i] = 5*Math.random();
				}

			}
			return loads;			
		}
	}

	public class ElectricHeater extends AbstractAppliance{
		public ElectricHeater(String name, double kW) {	super(name, kW);}
	}
}
