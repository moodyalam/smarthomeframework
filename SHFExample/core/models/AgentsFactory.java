package models;

import generators.extended.Grid;
import generators.extended.SolarPanel;
import generators.extended.WindTurbine;

import java.util.Arrays;

import storage.HomeBattery;
import storage.MobileBattery;
import util.Config;
import util.Const;
import appliances.extended.DishWasher;
import appliances.extended.HVACSystem;
import appliances.extended.Oven;
import appliances.extended.HVACSystem.ElectricHeater;
import appliances.extended.TV;
import appliances.extended.WashingMachine;
import dataloader.LoadLoader;
import electricvehicle.ArrivalEvent;
import electricvehicle.DepartureEvent;
import electricvehicle.Journey;

public class AgentsFactory {
	
	WashingMachine washingMachine = new WashingMachine();
	DishWasher dishWasher = new DishWasher();
	TV tv = new TV();
	Oven oven = new Oven();
	HVACSystem hvacSystem = new HVACSystem();
	
	HomeBattery battery = new HomeBattery("HomeBattery", Config.maxCapacity, Config.maxCharge, Config.maxDischarge, Config.maxEnergyLoss);
	HomeBattery battery2 = new HomeBattery("HomeBattery2", Config.maxCapacity, Config.maxCharge, Config.maxDischarge, Config.maxEnergyLoss);
	
	MobileBattery evBattery1 = new MobileBattery("EVBattery", 40, 5, 5, 0.1);
	MobileBattery evBattery2 = new MobileBattery("EVBattery2", 40, 5, 5, 0.1);
	
	//MobileBattery evBattery = new MobileBattery("EVBattery", Config.maxCapacity, Config.maxCharge, Config.maxDischarge, Config.maxEnergyLoss);
	
	ElectricVehicle moodysEV = new ElectricVehicle("Moody's EV", evBattery1);
	ElectricVehicle foreverAlone = new ElectricVehicle("Forever Alone", evBattery2);
	
	
	public AgentsFactory() {}
	
	private MyAgent createAnAgent(int id, Const.generationMeans generationMeans)
	{
		MyAgent agent = new MyAgent("Agent:"+id);
		
		if(generationMeans==Const.generationMeans.SOLAR)
		{
			agent.addGenerator(new SolarPanel("SolarPanel1", 2.5));
			//agent.addGenerator(new WindTurbine("WindTurbine1", 1.5));
		}
		else
		{
			agent.addGenerator(new WindTurbine("WindTurbine", 1.5));

		}
		
		Tariff tariff = new Tariff(20, 0.1, 0.05);
		Grid grid = new Grid(tariff);
		agent.addGenerator(grid);
		
		agent.addEvent(new DeferrableLoadEvent(washingMachine, "defIntCrit", 3, 20, 3, true, true));
		agent.addEvent(new DeferrableLoadEvent(washingMachine, "defUnintCrit", 3, 20, 4, false, true));
		agent.addEvent(new DeferrableLoadEvent(washingMachine, "defUnintrpNotcrit", 4, 3, 3, false, false));
		agent.addEvent(new DeferrableLoadEvent(washingMachine, "defUnintrpNotcrit2", 3, 40, 2, true, false));
		agent.addEvent(new DeferrableLoadEvent(washingMachine, "defIntrpNotcrit", 5, 30, 3, true, false));
		agent.addEvent(new NonDeferrableLoadEvent(tv, "The Simpsons", 5, 40, 4, true));
		agent.addEvent(new NonDeferrableLoadEvent(oven, "Baking", 5, 40, 4, false));	
		
		double load[] = new LoadLoader(Config.PathToLowIncomeLoadFile).getLoad();
		double pref[] = new double[Config.timeslots];
		Arrays.fill(pref, 1);
		
		agent.setBaseLoad(new BaseLoad(new double[Config.timeslots], pref, true));
		agent.addStorage(battery);
		
		//ElectricHeater[] heater = hvacSystem.getElectricHeaterIntances();
		//for (int i = 0; i < heater.length; i++) 
			//agent.addEvent(new NonDeferrableLoadEvent(heater[i], ""+i, heater[i].getMaxPower(), i, 1, false));			
		
		
		//agent.addStorage(battery2);
		//moodysEV.addJounery(new Journey(new DepartureEvent(10, 40), new ArrivalEvent(12, 20)));
		//moodysEV.addJounery(new Journey(new DepartureEvent(30, 20), new ArrivalEvent(38, 40)));
		//moodysEV.addJounery(new Journey(new DepartureEvent(44, 30), new ArrivalEvent(46, 0)));
		//foreverAlone.addJounery(new Journey(new DepartureEvent(10, 20), new ArrivalEvent(12, 40)));
		//agent.addElectricVehical(moodysEV);
		//agent.addElectricVehical(foreverAlone);
		
		agent.setMaxLink(Config.maxLink);
		
		return agent;
	}
	
	
	public MyAgent[] getAgents(int population)
	{
		MyAgent[] agents = new MyAgent[population];
		
		for(int i=0; i<agents.length;i++)
			{
				if(i%2==0)
					agents[i]=createAnAgent(i, Const.generationMeans.SOLAR);
				else
					agents[i]=createAnAgent(i, Const.generationMeans.WIND);
			}
		
		return agents; 
	}
	

}
