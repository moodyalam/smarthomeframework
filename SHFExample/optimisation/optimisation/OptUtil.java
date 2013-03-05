package optimisation;

import electricvehicle.AbstractElectricVehicle;
import electricvehicle.Journey;
import generators.IGenerator;
import generators.IRenewableGenerator;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.HashMap;

import loadevents.AbstractLoadEvent;
import loadevents.AbstractNonDeferrableEvent;
import models.MyAgent;
import storage.IMobileStorage;
import storage.IStorage;
import util.CommonMethod;
import util.Config;
import util.NotImplementedException;

public class OptUtil {

	protected static final int timeslots = Config.timeslots;
	protected static final int timeBasedInstances = Config.timeslots;


	public static void addLinkFlowConstraints(IloCplex cplex, boolean isConnected, IloNumVar[] link) throws IloException
	{
		
		if(!isConnected)
			for (int i = 0; i < link.length; i++)
				cplex.addEq(link[i], 0);
	}
	
	public static void prepareStorageVars(IloCplex cplex,
			ArrayList<IStorage> storage,
			HashMap<IStorage, IloNumVar[]> storageToChargeVarsMap,
			HashMap<IStorage, IloNumVar[]> storageToDischargeVarsMap,
			HashMap<IStorage, IloNumVar[]> storageToStoredEnergyVarsMap, HashMap<IStorage, IloNumVar[]> storageToEVEnergyUsageVarsMap) throws IloException{

		for (int i=0; i<storage.size(); i++)
		{
			IStorage s = storage.get(i);
			IloNumVar[] charge = cplex.numVarArray(timeslots, 0, s.getMaxCharge());
			IloNumVar[] discharge = cplex.numVarArray(timeslots, -s.getMaxDischarge(),0);
			IloNumVar[] batteryCharge= cplex.numVarArray(timeslots, 0, s.getCapacity());
			IloNumVar[] EVEnergyUsage= cplex.numVarArray(timeslots, -s.getCapacity(),s.getCapacity());

			storageToChargeVarsMap.put(s, charge);
			storageToDischargeVarsMap.put(s, discharge);
			storageToStoredEnergyVarsMap.put(s, batteryCharge);
			storageToEVEnergyUsageVarsMap.put(s,EVEnergyUsage);

		}

	}


	public static void prepareGeneratorVars(IloCplex cplex,
			ArrayList<IGenerator> generators, 
			HashMap<IGenerator, IloNumVar[]> generatorsToUsageVarsMap,
			HashMap<IGenerator, IloNumVar[]> generatorsToCostVarsMap, 
			HashMap<IGenerator, IloNumVar[]> generatorsToCarbonVarsMap) throws IloException {

		for (int i=0; i<generators.size(); i++)
		{
			IGenerator generator = generators.get(i);
			IloNumVar[] usageVars = null;
			IloNumVar[] costVars = null;
			IloNumVar[] carbonVars = null;

			if(generator instanceof IRenewableGenerator)
			{
				//We have no control over how much renewable energy is produced from the following.
				if (!((IRenewableGenerator)generator).isControllableCapacity())
				{
					usageVars = cplex.numVarArray(timeslots, generator.getGeneration(), generator.getGeneration());
					//We also assume that the cost and carbon usage of uncontrallable capacity is zero
					costVars = cplex.numVarArray(timeslots, 0, 0);
					carbonVars = cplex.numVarArray(timeslots, 0, 0);
				}

				else  //Remember - for this to work, the generators name should be unique
				{
					//This is renewable generator where the generation capacity is controlled (e.g. a CHP or biomass unit)
					usageVars = cplex.numVarArray(timeslots, 0, generator.getMaxGenerationCapacity());
					costVars = cplex.numVarArray(timeslots, 0, generator.getMaxCost());
					carbonVars = cplex.numVarArray(timeslots, 0, generator.getMaxCarbonCost());						
				}


			}

			else //Nonrenewable generators
			{
				usageVars = cplex.numVarArray(timeslots, 0, generator.getMaxGenerationCapacity());
				costVars = cplex.numVarArray(timeslots, 0, generator.getMaxCost());
				carbonVars = cplex.numVarArray(timeslots, 0, generator.getMaxCarbonCost());					
			}


			generatorsToUsageVarsMap.put(generator, usageVars);
			generatorsToCostVarsMap.put(generator , costVars);
			generatorsToCarbonVarsMap.put(generator, carbonVars);

		}

	}

	public static void addPowerAndBatteryConstraints(IloCplex cplex, MyAgent agent, 
			HashMap<IGenerator, IloNumVar[]> generatorsToVarsMap, 
			HashMap<AbstractLoadEvent, ArrayList<IloIntVar[]>> deferrableNonInterruptableToVarsMap, 
			HashMap<AbstractLoadEvent, IloIntVar[]> deferrableInterruptableToVarsMap,
			HashMap<AbstractLoadEvent,IloIntVar[]> nonDeferrableToVarsMap,			
			HashMap<IStorage, IloNumVar[]> storageToChargeVarsMap,
			HashMap<IStorage, IloNumVar[]> storageToDischargeVarsMap,
			HashMap<IStorage, IloNumVar[]> storageToStoredEnergyVarsMap,
			HashMap<IStorage, IloNumVar[]> storageToEVEnergyUsaugeVarsMap,
			IloNumVar[] baseLoad, 
			IloNumVar[] waste, IloNumVar[] link) throws IloException
			{
		//Power constraints
		for (int t=0; t<timeslots; t++)
		{
			IloNumExpr deferrableContinuousPower = cplex.numExpr();
			for (AbstractLoadEvent load:deferrableNonInterruptableToVarsMap.keySet())
				for (int timeInstance=0; timeInstance<timeBasedInstances; timeInstance++)
					deferrableContinuousPower = cplex.sum(deferrableContinuousPower, cplex.prod(getkWhConsumptionInOneTimeslot(load),deferrableNonInterruptableToVarsMap.get(load).get(timeInstance)[t])); 

			IloNumExpr nonDeferrablePower = cplex.numExpr();
			for (AbstractLoadEvent load:nonDeferrableToVarsMap.keySet())
				nonDeferrablePower = cplex.sum(nonDeferrablePower, cplex.prod(getkWhConsumptionInOneTimeslot(load),nonDeferrableToVarsMap.get(load)[t]));

			IloNumExpr deferrableDiscretePower = cplex.numExpr();
			for (AbstractLoadEvent load:deferrableInterruptableToVarsMap.keySet())
				deferrableDiscretePower = cplex.sum(deferrableDiscretePower, cplex.prod(getkWhConsumptionInOneTimeslot(load),deferrableInterruptableToVarsMap.get(load)[t]));

			IloNumExpr chargeExpr = cplex.numExpr();
			for (IStorage storage:storageToChargeVarsMap.keySet())
				chargeExpr = cplex.sum(chargeExpr, storageToChargeVarsMap.get(storage)[t]);

			IloNumExpr dischargeExpr = cplex.numExpr();
			for (IStorage storage:storageToDischargeVarsMap.keySet())
				dischargeExpr = cplex.sum(dischargeExpr, storageToDischargeVarsMap.get(storage)[t]);

			IloNumExpr powerRequirementsExpr = cplex.sum(baseLoad[t], waste[t], link[t]);
			powerRequirementsExpr = cplex.sum(powerRequirementsExpr, deferrableContinuousPower, nonDeferrablePower, deferrableDiscretePower);
			powerRequirementsExpr = cplex.sum(powerRequirementsExpr, chargeExpr, dischargeExpr);

			IloNumExpr totalPowerAtThisTimeslot = cplex.numExpr(); 
			IloNumExpr renewablePowerAtThisTimesot = cplex.numExpr();

			for(int i=0; i<agent.getGenerators().size(); i++)
			{
				IGenerator generator = agent.getGenerators().get(i);
				IloNumVar[] vars = generatorsToVarsMap.get(generator);
				totalPowerAtThisTimeslot = cplex.sum(totalPowerAtThisTimeslot, vars[t]);

				if(generator instanceof IRenewableGenerator)
					cplex.sum(renewablePowerAtThisTimesot, vars[t]);				
			}

			//TODO: Are these comments right?
			//We want to know how much of the renewable energy was not used, i.e. 'wasted'. Therefore, the power usage at time t must be 
			//equal to or more than the generated renewable energy (if the load is less than the generated renewable power, it will go to waste variable
			//and thus we will know how much was extra)

			//cplex.addEq(renewablePower, powerRequirementsExpr);

			//cplex.addLe(renewablePower, powerRequirementsExpr);
			cplex.addGe(totalPowerAtThisTimeslot, powerRequirementsExpr);
			cplex.addGe(powerRequirementsExpr, renewablePowerAtThisTimesot);
		}


		//Let's add the storage constraints for each storage battery 

		for(IStorage storage: storageToChargeVarsMap.keySet())
		{
			IloNumVar[] charge = storageToChargeVarsMap.get(storage);
			IloNumVar[] discharge = storageToDischargeVarsMap.get(storage);
			IloNumVar[] storedEnergy = storageToStoredEnergyVarsMap.get(storage);
			IloNumVar[] evConsumption = storageToEVEnergyUsaugeVarsMap.get(storage);

			//Although, there are some similarities between home and mobile batteries and they can share code,  
			//to keep both separated for the sake of simplicity
			boolean isMobileBattery = storage instanceof IMobileStorage;

			if (!isMobileBattery) //Home battery 
				for (int t=0; t<timeslots; t++) //The next state of the battery depends on the last state + charge - discharge
				{					
					IloNumExpr expr = cplex.sum(storedEnergy[t], cplex.prod((1-storage.getEnergyStorageLoss()),charge[t]),	discharge[t], evConsumption[t]);

					//the last state of the battery is related to the first state of the battery
					if(t==(timeslots-1))
						cplex.addEq(storedEnergy[0], expr);
					else
						cplex.addEq(storedEnergy[t+1], expr);

					//EV consumption is meaningless for a home battery
					cplex.addEq(evConsumption[t], 0);
				}

			else if(isMobileBattery)
			{
				//Let's get the EV this battery belongs to, and then all the journeys it will make	
				AbstractElectricVehicle ev =  ((IMobileStorage) storage).getEV();
				ArrayList<Journey> journeys =ev.getJourneys();

				//This variable make sure that EV consumption remains in each time slot remains the same for a journey
				//Removing this will allow the optimiser to assign combination of values to EV consumption to satisfy 
				//departure and arrival energy constraints.
				IloNumVar evSmoothUsageVariable[] = cplex.numVarArray(journeys.size(), -storage.getCapacity(), storage.getCapacity());
				//This contains all timeslots that the EV was on journey
				ArrayList<Integer> allJouneyTimeslots = new ArrayList<Integer>();

				for (int t=0; t<timeslots; t++) //The next state of the battery depends on the last state + charge - discharge
				{					
					//This describe the general relationship of charge/discharge and stored energy
					IloNumExpr expr = cplex.sum(storedEnergy[t], cplex.prod((1-storage.getEnergyStorageLoss()),charge[t]),	discharge[t], evConsumption[t]);
					if(t==(timeslots-1))
						cplex.addEq(storedEnergy[0], expr);
					else
						cplex.addEq(storedEnergy[t+1], expr);

					//Now let's go through each journey for this storage device
					for(int i=0; i<journeys.size(); i++)
					{
						Journey journey = journeys.get(i);						
						if (journey.containsTimeslot(t))
						{							
							allJouneyTimeslots.add(t);
							//When the EV is on a journey, we can't charge/discharge it from home
							cplex.addEq(charge[t], 0);
							cplex.addEq(discharge[t], 0);
							//We assume that the EV consumption will remain in all timeslots of a single journey
							cplex.addEq(evConsumption[t], evSmoothUsageVariable[i]);
						}

						if(journey.getDepartureEvent().getTimeslot()==t)
						{
							int timeslotBeforeDeparture = t;
							if (timeslotBeforeDeparture==0)
								timeslotBeforeDeparture = Config.timeslots-1;

							//The battery must contain the specified energy on the timeslot just before the departure 
							cplex.addEq(storedEnergy[timeslotBeforeDeparture-1],journey.getDepartureEvent().getStoredEnergy());
						}

						if(journey.getArrivalEvent().getTimeslot()==t)
						{
							//On arrival the battery will contain a specified amount of energy 
							cplex.addEq(storedEnergy[t],journey.getArrivalEvent().getStoredEnergy());
						}

					}

				}

				//In times other than the journey times, the EV is not operation (i.e. EV consumption is zero) 
				//Note: The battery can still be charged/discharged its just that there is no energy loss due to EV movement (no movement = no EV consumption)
				for(int t=0; t<timeslots; t++)
					if(!allJouneyTimeslots.contains(t))
						cplex.addEq(evConsumption[t],0);

			}
		}
			}

	private static double getkWhConsumptionInOneTimeslot(AbstractLoadEvent load) {
		double maxPower = load.getMaxPowerRequired();
		return (24d/timeslots)*maxPower;
	}

	public static void addNonDeferrableNonInterruptableLoads(IloCplex cplex, HashMap<AbstractLoadEvent, IloIntVar[]> nonDeferrableDiscreteLoads) throws IloException
	{
		for(AbstractLoadEvent load: nonDeferrableDiscreteLoads.keySet())
		{

			int runPeriods = load.getRunLength();
			int startTimeslot = ((AbstractNonDeferrableEvent) load).getStartTimeslot();
			IloIntVar[] vars = nonDeferrableDiscreteLoads.get(load);

			ArrayList<Integer> indices = CommonMethod.getNextIndices(timeslots, startTimeslot, runPeriods-1, false);
			//This describes how the 'main timeslot' is related to the all adjacent 'runPeriods' timeslots
			for(int index:indices)
			{
				cplex.addGe(cplex.sum(vars[index], cplex.negative(vars[startTimeslot])), 0);
				cplex.addGe(cplex.sum(vars[startTimeslot], cplex.negative(vars[index])), 0);
			}

			//Anything not in above the indices should be zero
			for(int t=0; t<timeslots; t++)
				if(indices.contains(t) | t==startTimeslot)
					continue;
				else
					cplex.addEq(vars[t], 0);

			//Make sure the critical load is satisfied
			if(load.isCritical())
				cplex.addEq(cplex.sum(vars), runPeriods);
		}
	}
	public static void addDeferrableInterruptableLoads(IloCplex cplex, HashMap<AbstractLoadEvent, IloIntVar[]> deferrableDiscreteLoads) throws IloException

	{
		for (AbstractLoadEvent load: deferrableDiscreteLoads.keySet())
		{
			int runPeriods = load.getRunLength();
			IloIntVar[] vars = deferrableDiscreteLoads.get(load);

			IloNumVar switchOn = cplex.intVar(0, 1);
			IloNumVar switchOff = cplex.intVar(0, 1);

			IloNumExpr expr = cplex.sum(vars);

			cplex.addLe(expr, runPeriods);
			cplex.addEq(expr, cplex.sum(cplex.prod(0, switchOff), cplex.prod(runPeriods, switchOn)));
			cplex.addEq(1, cplex.sum(switchOn, switchOff));

			//critcial load must be satisfied
			if(load.isCritical())
				cplex.addEq(cplex.sum(vars), runPeriods);

		}

	}
	public static void addDeferrableNonInterruptableLoads(IloCplex cplex, HashMap<AbstractLoadEvent, ArrayList<IloIntVar[]>> deferrableContinuousLoads) throws IloException
	{
		for(AbstractLoadEvent load: deferrableContinuousLoads.keySet())
		{
			int runPeriods = load.getRunLength();

			ArrayList<IloIntVar[]> vars = deferrableContinuousLoads.get(load);

			for (int timeInstance = 0; timeInstance<vars.size(); timeInstance++)
			{
				ArrayList<Integer> indices = CommonMethod.getNextIndices(timeslots, timeInstance, runPeriods-1, false);
				//This describes how the 'main timeslot' is related to the all adjacent 'runPeriods' timeslots
				for(int index:indices)
				{
					cplex.addGe(cplex.sum(vars.get(timeInstance)[index], cplex.negative(vars.get(timeInstance)[timeInstance])), 0);
					cplex.addGe(cplex.sum(vars.get(timeInstance)[timeInstance], cplex.negative(vars.get(timeInstance)[index])), 0);
				}


				//Anything not in above the indices should be zero
				for(int t=0; t<timeslots; t++)
					if(indices.contains(t) | t==timeInstance)
						continue;
					else
						cplex.addEq(vars.get(timeInstance)[t], 0);
			}

			//Only one instance of a machine is allowed to run
			IloNumExpr[] oneTimeInstanceOnlyConstraints= new IloNumExpr[timeslots];
			for (int t=0; t<timeslots;t++)
			{
				oneTimeInstanceOnlyConstraints[t] = cplex.numExpr();	
				for (int timeInstance=0; timeInstance<timeBasedInstances; timeInstance++)
					oneTimeInstanceOnlyConstraints[t]=cplex.sum(oneTimeInstanceOnlyConstraints[t],vars.get(timeInstance)[t]);							
				cplex.addLe(oneTimeInstanceOnlyConstraints[t], 1);
			}


			//To make sure ATLEAST one time instance runs across the columns and rows	 		
			if(load.isCritical())
				cplex.addEq(cplex.sum(oneTimeInstanceOnlyConstraints),runPeriods);	

			else //To make sure not more than one time instance runs across the columns and across rows
				cplex.addLe(cplex.sum(oneTimeInstanceOnlyConstraints),runPeriods);

		}
	}
	public static void prepareListOfDeferrableAndNondeferrableLoads(IloCplex cplex, MyAgent agent,
			HashMap<AbstractLoadEvent, ArrayList<IloIntVar[]>> deferrableNonInterruptableToVarsMap,
			HashMap<AbstractLoadEvent, IloIntVar[]> deferrableInterruptableToVarsMap,
			HashMap<AbstractLoadEvent, IloIntVar[]> nonDeferrableToVarsMap) throws IloException

			{
		for(AbstractLoadEvent load: agent.getDeferrableEvents())
		{
			if(!load.isInterruptable())
			{
				ArrayList<IloIntVar[]> vars = new ArrayList<IloIntVar[]>();
				for (int i=0; i<timeBasedInstances; i++)		
					vars.add(cplex.intVarArray(timeslots, 0, 1));
				deferrableNonInterruptableToVarsMap.put(load, vars);
			}

			else
			{
				IloIntVar[] vars = cplex.intVarArray(timeslots, 0, 1);
				deferrableInterruptableToVarsMap.put(load, vars);
			}
		}


		for(AbstractLoadEvent load: agent.getNonDeferrableEvents())
		{
			if(!load.isInterruptable()) //Noninterruptable load
			{
				IloIntVar[] vars = cplex.intVarArray(timeslots, 0, 1);
				nonDeferrableToVarsMap.put(load, vars);
			}

			else				
			{
				try {
					throw new NotImplementedException("OptModel does not support discrete deferrable events");
				} catch (NotImplementedException e) {
					e.printStackTrace();
				}
			}
		}
			}

}
