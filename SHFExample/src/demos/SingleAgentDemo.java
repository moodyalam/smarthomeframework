package demos;


import generators.extended.SolarPanel;
import generators.extended.WindTurbine;
import ilog.concert.IloException;
import models.DeferrableLoadEvent;
import models.ElectricVehicle;
import models.MyAgent;
import models.NonDeferrableLoadEvent;
import models.OptResult;
import optimisation.extended.MaxUtilOptModel;
import storage.HomeBattery;
import storage.MobileBattery;
import util.Config;
import visualiser.desktop.OptInfoDesktopHandler;
import appliances.extended.DishWasher;
import appliances.extended.HVACSystem;
import appliances.extended.HVACSystem.ElectricHeater;
import appliances.extended.Oven;
import appliances.extended.TV;
import appliances.extended.WashingMachine;
import dataloader.DummyDataFactory;
import electricvehicle.ArrivalEvent;
import electricvehicle.DepartureEvent;
import electricvehicle.Journey;

public class SingleAgentDemo {
	MyAgent agent =null;
	WashingMachine washingMachine = null;
	DishWasher dishWasher = null;
	TV tv = null;
	Oven oven = null;
	HVACSystem hvacSystem = null;
	HomeBattery battery = null;
	
	private void addAppliances()
	{	
		washingMachine = new WashingMachine();
		dishWasher = new DishWasher();
		tv = new TV();
		oven = new Oven();
	}
	
	private void addStorage()
	{
		battery = new HomeBattery("HomeBattery", Config.maxCapacity, Config.maxCharge, Config.maxDischarge, Config.maxEnergyLoss);
		agent.addStorage(battery);		
	}
	private void addGeneration()
	{
		agent.addGenerator(new SolarPanel("SolarPanel", 2.5));
		agent.addGenerator(new WindTurbine("Wind Turbine", 2));
		
		//Grid grid = new Grid(new Tariff(20, 0.1, 0.05));
		//agent.addGenerator(grid);
	}
	
	private void addLoadRequirements()
	{
		agent.setBaseLoad(DummyDataFactory.createBaseLoad());
		
		agent.addEvent(new DeferrableLoadEvent(washingMachine, "defIntCrit", 3, 20, 3, true, true));
		agent.addEvent(new DeferrableLoadEvent(washingMachine, "defUnintCrit", 3, 20, 4, false, true));
		agent.addEvent(new DeferrableLoadEvent(washingMachine, "defUnintrpNotcrit", 4, 3, 3, false, false));
		agent.addEvent(new DeferrableLoadEvent(washingMachine, "defUnintrpNotcrit2", 3, 40, 2, true, false));
		agent.addEvent(new DeferrableLoadEvent(washingMachine, "defIntrpNotcrit", 5, 30, 3, true, false));
		agent.addEvent(new NonDeferrableLoadEvent(tv, "The Simpsons", 5, 40, 4, true));
		agent.addEvent(new NonDeferrableLoadEvent(oven, "Baking", 5, 40, 4, false));		
	}
	
		
	public void demo() throws IloException
	{		
		agent = new MyAgent("SomeAgent");
		
		//Building a smart home	
		addAppliances();
		addGeneration();
		addLoadRequirements();
		addStorage();
		addEV();
		addElectricHeating();
		
		//Optimisation
		MaxUtilOptModel optModel = new MaxUtilOptModel("SomeOptimisation", agent);
		OptResult optResult = optModel.optimise();
		
		//Visualisation
		if(optResult.isSolved())
			new OptInfoDesktopHandler(optResult);
		else
			System.out.println("Model is unsolvable - Try removing critical loads");
			
	}
	
		
	private void addEV()
	{
		MobileBattery evBattery1 = new MobileBattery("EVBattery", 40, 10, 10, 0.1);
		ElectricVehicle ev = new ElectricVehicle("EV", evBattery1);
		ev.addJounery(new Journey(new DepartureEvent(44, 30), new ArrivalEvent(46, 0)));
		agent.addElectricVehical(ev);
	}
	
	private void addElectricHeating()
	{
		hvacSystem = new HVACSystem();
		ElectricHeater[] heater = hvacSystem.getElectricHeaterIntances();
		for (int i = 0; i < heater.length; i++) 
			agent.addEvent(new NonDeferrableLoadEvent(heater[i], ""+i, heater[i].getMaxPower(), i, 1, false));
	}
	
	
	public static void main(String[] args) throws IloException{	
		new SingleAgentDemo().demo();
	} 
	

}
