package optimisation.extended;
import generators.IGenerator;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.HashMap;

import loadevents.AbstractLoadEvent;
import models.MyAgent;
import optimisation.AbstractOptModel;
import storage.IStorage;
import util.Config;


public class MaxUtilOptModel extends AbstractOptModel{

	private static final int timeslots = Config.timeslots;
	
	public MaxUtilOptModel(String optID, MyAgent agent) throws IloException {	
		super(optID, agent);		
	}
	
	public MaxUtilOptModel(String optID, MyAgent agent, IloCplex cplex) throws IloException {	
		super(optID, agent, cplex);		
	}
	
	public IloObjective getObjectiveFunction() throws IloException
	{
		//Add utility of the agent as the objective to maximise
		IloNumExpr ua = prepareUtilityFunctionForAgent(cplex, agent, baseLoad, waste, 
				generatorsToUsageVarsMap, 
				deferrableNonInterruptableToVarsMap, 
				deferrableInterruptableToVarsMap, 
				nonDeferrableToVarsMap,
				storageToChargeVarsMap
				);
		IloObjective obj = cplex.maximize(ua);
		return obj;
	}
	

	public void addModelSpecificConstraints() throws IloException{}
	
	public static IloNumExpr prepareUtilityFunctionForAgent(IloCplex cplex, MyAgent a, 
			IloNumExpr[] baseLoad,
			IloNumExpr[] waste,
			HashMap<IGenerator, IloNumVar[]> generatorsToVarsMap,
			HashMap<AbstractLoadEvent, ArrayList<IloIntVar[]>> deferrableNonInterruptableToVarsMap,
			HashMap<AbstractLoadEvent, IloIntVar[]> deferrableInterruptableToVarsMap,
			HashMap<AbstractLoadEvent, IloIntVar[]> nonDeferrableToVarsMap,
			HashMap<IStorage, IloNumVar[]> storageToChargeVarsMap
			) throws IloException 
	{
		
		IloNumExpr agentUtil =cplex.numExpr();
		
		for (int t=0; t<timeslots; t++)
		{
			IloNumExpr deferrableObj = cplex.numExpr();
			
			for (AbstractLoadEvent load:deferrableNonInterruptableToVarsMap.keySet())
				for (int timeInstance=0; timeInstance<timeslots; timeInstance++)
					deferrableObj = cplex.sum(deferrableObj, cplex.prod(load.getPreferenceVector(timeslots)[t],deferrableNonInterruptableToVarsMap.get(load).get(timeInstance)[t])); 
			
			IloNumExpr nonDeferrableObj = cplex.numExpr();
			for (AbstractLoadEvent load:nonDeferrableToVarsMap.keySet())
				nonDeferrableObj = cplex.sum(nonDeferrableObj, cplex.prod(load.getPreferenceVector(timeslots)[t],nonDeferrableToVarsMap.get(load)[t]));
			
			IloNumExpr deferrableDiscreteObj = cplex.numExpr();
			for (AbstractLoadEvent load:deferrableInterruptableToVarsMap.keySet())
				deferrableDiscreteObj = cplex.sum(deferrableDiscreteObj, cplex.prod(load.getPreferenceVector(timeslots)[t],deferrableInterruptableToVarsMap.get(load)[t]));
						
			agentUtil = cplex.sum(agentUtil, deferrableObj, nonDeferrableObj, deferrableDiscreteObj);
			//Add the penalty for waste
			agentUtil = cplex.sum(agentUtil, cplex.prod(Config.wasteIncenstive, waste[t]));
			//Add the penalty for charging the battery
			for(IStorage storage: storageToChargeVarsMap.keySet())
				agentUtil = cplex.sum(agentUtil, cplex.prod(Config.batteryUsagePenality, storageToChargeVarsMap.get(storage)[t]));
			
			//Add the cost of energy consumption
			for(IGenerator generator: generatorsToVarsMap.keySet())
				{
					IloNumVar vars[] = generatorsToVarsMap.get(generator);
					IloNumExpr cost = cplex.prod(vars[t], generator.getCost()[t]);
					agentUtil = cplex.sum(agentUtil, cplex.negative(cost));
				}
			
		}
		return agentUtil;
	}
	
}