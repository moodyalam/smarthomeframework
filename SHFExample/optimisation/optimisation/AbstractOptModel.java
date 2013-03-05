package optimisation;
import generators.IGenerator;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.HashMap;

import loadevents.AbstractLoadEvent;
import models.MyAgent;
import models.OptResult;
import models.OptResult.Keys;
import storage.IStorage;
import util.CommonMethod;
import util.Config;


public abstract class AbstractOptModel {

	protected int timeslots =Config.timeslots;
	protected int timeBasedInstances = Config.timeslots;
	protected HashMap<AbstractLoadEvent, ArrayList<IloIntVar[]>> deferrableNonInterruptableToVarsMap = new HashMap<AbstractLoadEvent, ArrayList<IloIntVar[]>>();
	protected HashMap<AbstractLoadEvent, IloIntVar[]> deferrableInterruptableToVarsMap = new HashMap<AbstractLoadEvent, IloIntVar[]>();
	protected HashMap<AbstractLoadEvent, IloIntVar[]> nonDeferrableToVarsMap = new HashMap<AbstractLoadEvent, IloIntVar[]>();
	
	protected HashMap<IGenerator, IloNumVar[]> generatorsToUsageVarsMap = new HashMap<IGenerator, IloNumVar[]>();
	protected HashMap<IGenerator, IloNumVar[]> generatorsToCostVarsMap = new HashMap<IGenerator, IloNumVar[]>();
	protected HashMap<IGenerator, IloNumVar[]> generatorsToCarbonVarsMap = new HashMap<IGenerator, IloNumVar[]>();
	
	protected HashMap<IStorage, IloNumVar[]> storageToChargeVarsMap = new HashMap<IStorage, IloNumVar[]>();
	protected HashMap<IStorage, IloNumVar[]> storageToDischargeVarsMap = new HashMap<IStorage, IloNumVar[]>();
	protected HashMap<IStorage, IloNumVar[]> storageToStoredEnergyVarsMap = new HashMap<IStorage, IloNumVar[]>();
	protected HashMap<IStorage, IloNumVar[]> storageToEVConsumptionVarsMap = new HashMap<IStorage, IloNumVar[]>();
	
	protected String optID;
	protected IloCplex cplex;
	protected MyAgent agent;
	protected IloNumVar[] baseLoad;
	protected IloNumVar[] link;
	protected IloNumVar[] waste;
		
	public AbstractOptModel(String optID, MyAgent agent) throws IloException
	{
		this(optID, agent, new IloCplex());		
	}
	
	public AbstractOptModel(String optID, MyAgent agent, IloCplex cplex) {	
		this.optID = optID;
		this.agent = agent;
		try{
			this.cplex = cplex;
			
			//TODO: Do we need this here?
			cplex.setParam(IloCplex.IntParam.SolutionTarget, 2);
			//This turns off the output to console
			cplex.setOut(null);
						
			baseLoad= cplex.numVarArray(timeslots, agent.getBaseLoad().getLoadPerTimeslot(), agent.getBaseLoad().getLoadPerTimeslot());
			link= cplex.numVarArray(timeslots, -Math.abs(agent.getMaxLink()), Math.abs(agent.getMaxLink()));
			waste = cplex.numVarArray(timeslots, 0, Double.MAX_VALUE);
			
			addBasicModelConstraints();
			
		}
		catch (IloException e) {
			System.out.println(e);
		}
		
	}
	
	public void addBasicModelConstraints() throws IloException
	{
		//prepare all the storage constraints
		OptUtil.prepareStorageVars(cplex, agent.getStorage(), storageToChargeVarsMap, storageToDischargeVarsMap, storageToStoredEnergyVarsMap, storageToEVConsumptionVarsMap);
		//prepare the generators vars 
		OptUtil.prepareGeneratorVars(cplex, agent.getGenerators(), generatorsToUsageVarsMap, generatorsToCostVarsMap, generatorsToCarbonVarsMap);		
		//prepare the list of cplex variables for deferrable continuous, deferrable discrete and non-deferrable load events
		OptUtil.prepareListOfDeferrableAndNondeferrableLoads(cplex, agent, deferrableNonInterruptableToVarsMap, deferrableInterruptableToVarsMap, nonDeferrableToVarsMap);
		//Add deferrable continuous loads
		OptUtil.addDeferrableNonInterruptableLoads(cplex, deferrableNonInterruptableToVarsMap);
		//Add deferrable discrete loads 			
		OptUtil.addDeferrableInterruptableLoads(cplex, deferrableInterruptableToVarsMap);
		//Add nondeferrable loads
		OptUtil.addNonDeferrableNonInterruptableLoads(cplex, nonDeferrableToVarsMap);
		//Add Power and Battery constraints
		OptUtil.addPowerAndBatteryConstraints(cplex, agent, generatorsToUsageVarsMap, deferrableNonInterruptableToVarsMap, deferrableInterruptableToVarsMap, nonDeferrableToVarsMap,
				storageToChargeVarsMap,
				storageToDischargeVarsMap,
				storageToStoredEnergyVarsMap,
				storageToEVConsumptionVarsMap,
				baseLoad, waste, link);
		
		//If home is disconnected then there the link flow must be zero
		//Check: If home is connected then maxLink must not be zero
		if(agent.isConnected() && !(agent.getMaxLink()>0))
		{
			System.err.println("Agent is connected but maxLink is set to zero");
			throw new IloException();
		}
		OptUtil.addLinkFlowConstraints(cplex, agent.isConnected(), link);
		
		
	}	
	
	public abstract IloObjective getObjectiveFunction() throws IloException;
	public abstract void addModelSpecificConstraints() throws IloException;
	
	public OptResult optimise() throws IloException
	{
		OptResult optInfo = null;
		try
		{
			//Add model specific constraints 
			addModelSpecificConstraints();
			
			//Add objective
			IloObjective obj = getObjectiveFunction();
			cplex.add(obj);
						
			if ( cplex.solve() ) { 
				cplex.output().println("Solution status = " + cplex.getStatus()); 
				cplex.output().println("Solution value  = " + cplex.getObjValue());
				optInfo = makeOptInfo(true);
				
			}
			
			else
			{
				optInfo = makeOptInfo(false);
			}
			
			
			cplex.end();					

		}
		catch (IloException e) {
			System.out.println("IloException:"+e);
			e.printStackTrace();
		}	
		return optInfo;
	}
	
		
	public OptResult makeOptInfo(boolean isSolved) throws IloException
	{
		if(!isSolved)
			return new OptResult(optID, agent, isSolved);
		
		HashMap<AbstractLoadEvent, double[]> loadPowerMapping = new HashMap<AbstractLoadEvent, double[]>();
		HashMap<IGenerator, double[]> generatorUsageMapping = new HashMap<IGenerator, double[]>();
		HashMap<IStorage, double[]> chargeMapping = new HashMap<IStorage, double[]>();
		HashMap<IStorage, double[]> dischargeMapping = new HashMap<IStorage, double[]>();
		HashMap<IStorage, double[]> batteryStoredEnergyMapping = new HashMap<IStorage, double[]>();
		HashMap<IStorage, double[]> evUsageEnergyMapping = new HashMap<IStorage, double[]>();
		
		for(AbstractLoadEvent load:deferrableNonInterruptableToVarsMap.keySet())
		{
			ArrayList<IloIntVar[]> vars = deferrableNonInterruptableToVarsMap.get(load);
			
			double[] activeTimeInstance = new double[timeslots]; 
			for (int timeInstance=0; timeInstance<vars.size();timeInstance++)
				{
					//Only one time instance of a deferrable load will be active, therefore
					activeTimeInstance = CommonMethod.sum(activeTimeInstance, cplex.getValues(vars.get(timeInstance)));			
				}
			//Now we know the timeslots that the deferrable load was active. To know what was the power used at each timeslot
			CommonMethod.multiplyArrayElementsWith(activeTimeInstance, load.getMaxPowerRequired());
			//Now this has the power usage over active time slots
			loadPowerMapping.put(load, activeTimeInstance);
		}
		
		for(AbstractLoadEvent load:nonDeferrableToVarsMap.keySet())
			loadPowerMapping.put(load, CommonMethod.multiplyArrayElementsWith(cplex.getValues(nonDeferrableToVarsMap.get(load)),load.getMaxPowerRequired()));
		
		for(AbstractLoadEvent load:deferrableInterruptableToVarsMap.keySet())
			loadPowerMapping.put(load, (CommonMethod.multiplyArrayElementsWith(cplex.getValues(deferrableInterruptableToVarsMap.get(load)), load.getMaxPowerRequired())));
				
		for(IGenerator generator: generatorsToUsageVarsMap.keySet())
			generatorUsageMapping.put(generator, cplex.getValues(generatorsToUsageVarsMap.get(generator)));
		
		for(IStorage storage: storageToChargeVarsMap.keySet())
			chargeMapping.put(storage, cplex.getValues(storageToChargeVarsMap.get(storage)));
		
		for(IStorage storage: storageToDischargeVarsMap.keySet())
			dischargeMapping.put(storage, cplex.getValues(storageToDischargeVarsMap.get(storage)));
		
		for(IStorage storage: storageToStoredEnergyVarsMap.keySet())
			batteryStoredEnergyMapping.put(storage, cplex.getValues(storageToStoredEnergyVarsMap.get(storage)));
		
		for(IStorage storage: storageToEVConsumptionVarsMap.keySet())
			evUsageEnergyMapping.put(storage, cplex.getValues(storageToEVConsumptionVarsMap.get(storage)));
		
		OptResult optInfo = new OptResult(optID, agent, isSolved);
		
		optInfo.put(Keys.objValue, cplex.getObjValue());
		optInfo.put(Keys.baseLoad, cplex.getValues(baseLoad));
		optInfo.put(Keys.waste, cplex.getValues(waste));
		optInfo.put(Keys.link, cplex.getValues(link));
		optInfo.put(Keys.generatorUsageMap, generatorUsageMapping);
		optInfo.put(Keys.loadPowerMapping, loadPowerMapping);
		optInfo.put(Keys.chargeMapping, chargeMapping);
		optInfo.put(Keys.dischargeMapping, dischargeMapping);
		optInfo.put(Keys.storedEnergyMapping, batteryStoredEnergyMapping);
		optInfo.put(Keys.evUsageEnergyMapping, evUsageEnergyMapping);
		 
		return optInfo; 
	}
		
	public MyAgent getAgent() {return agent;}
	public IloNumVar[] getBaseLoad() {return baseLoad;}
	public IloNumVar[] getLink() {return link;}
	public IloNumVar[] getWaste() {return waste;}
	public HashMap<AbstractLoadEvent, ArrayList<IloIntVar[]>> getDeferrableNonInterruptableToVarsMap() {return deferrableNonInterruptableToVarsMap;}
	public HashMap<AbstractLoadEvent, IloIntVar[]> getDeferrableInterruptableToVarsMap() {return deferrableInterruptableToVarsMap;}
	public HashMap<AbstractLoadEvent, IloIntVar[]> getNonDeferrableToVarsMap() {return nonDeferrableToVarsMap;}
	public HashMap<IGenerator, IloNumVar[]> getGeneratorsToUsageVarsMap() {	return generatorsToUsageVarsMap;}
	public HashMap<IGenerator, IloNumVar[]> getGeneratorsToCostVarsMap() {return generatorsToCostVarsMap;}
	public HashMap<IGenerator, IloNumVar[]> getGeneratorsToCarbonVarsMap() {return generatorsToCarbonVarsMap;}
	public HashMap<IStorage, IloNumVar[]> getStorageToChargeVarsMap() {return storageToChargeVarsMap;}
	public HashMap<IStorage, IloNumVar[]> getStorageToDischargeVarsMap() {return storageToDischargeVarsMap;}
	public HashMap<IStorage, IloNumVar[]> getStorageToStoredEnergyVarsMap() {return storageToStoredEnergyVarsMap;}
	public HashMap<IStorage, IloNumVar[]> getStorageToEVConsumptionVarsMap() {return storageToEVConsumptionVarsMap;}
	
}