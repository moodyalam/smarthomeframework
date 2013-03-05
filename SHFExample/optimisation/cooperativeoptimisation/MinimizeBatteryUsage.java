package cooperativeoptimisation;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.cplex.IloCplex;

import java.util.ArrayList;

import models.MyAgent;
import models.OptResult;
import optimisation.er.MaxUtilOptModel_ER;
import storage.IStorage;
import util.CommonMethod;
import util.Config;
import util.Const_ER;


public class MinimizeBatteryUsage {

	
	private int timeslots = Config.timeslots;
	int timeBasedInstances = Config.timeslots;
	
	private MaxUtilOptModel_ER[] agentOptModels;

	private IloCplex cplex;
	private MyAgent[] agents;
	//private final double maxLink = Config.maxLink;
	private ArrayList<IloNumVar[]> linksVars = new ArrayList<IloNumVar[]>();
	private ArrayList<double[]> proposedLinkForEachAgent = new ArrayList<double[]>();
	private double[] proposedCommunityLink = new double[timeslots];
	
	public MinimizeBatteryUsage(MyAgent[] agents) {	
		try{
			this.agents = agents;
			this.cplex = new IloCplex();

			//TODO: Do we need this?
			cplex.setParam(IloCplex.IntParam.SolutionTarget, 2);
			this.agentOptModels = new MaxUtilOptModel_ER[agents.length];
			
			//Each agent has its own optimisation model
			for(int i=0; i<agents.length; i++)
				{	
					//Let's optimise each having their link established
					String optID = Const_ER.ANY_FLOW;						
					agentOptModels[i] =new MaxUtilOptModel_ER(optID, agents[i], cplex);
					linksVars.add(agentOptModels[i].getLink());
				}
			
		}
		catch (Exception e) {
			System.out.println(e);
		}
		
	}

	
	public void optimise() throws IloException
	{
		try
		{
			//Define and constrain the community link
			IloNumVar[] communityLink = cplex.numVarArray(timeslots, 0, Double.MAX_VALUE);
			
			for(int t=0; t<timeslots; t++)
			{
				IloNumExpr expr = cplex.numExpr();
				
				//sum of flows for this particular timeslot
				for(int j=0; j<linksVars.size(); j++)
					{
						expr = cplex.sum(expr, linksVars.get(j)[t]);
					}
				
				//sum of all flows at time t must equal the produced community flow for that timeslot
				cplex.addEq(expr,communityLink[t]);
				
			}
			
			
			//Add the fairness measure
			IloNumVar fairnessMeasure = cplex.numVar(0, Double.MAX_VALUE); 
			for(int i=0; i<agents.length; i++)
			{
				//We consider only one battery per agent
				IStorage storage = agents[i].getStorage().get(0);
				
				//What is the sum of charging for agent as an individual (i.e. with no link flow)
				double previousChargingSum = CommonMethod.sum(agents[i].getOptInfo(Const_ER.NO_FLOW).getChargeMapping().get(storage));
				
				IloNumExpr newChargeSum =  cplex.negative(cplex.sum(agentOptModels[i].getStorageToChargeVarsMap().get(storage)));
				IloNumExpr chargeDiff = cplex.sum(previousChargingSum, newChargeSum);
				//cplex.addEq(cplex.abs(chargeDiff), fairnessMeasure);				
			}		
			
			//Add solution guarantees
			for(int i=0; i<agents.length; i++)
			{
				IloNumExpr utility = agentOptModels[i].prepareUtilityFunction();
				double disagreementUtil =agents[i].getOptInfo(Const_ER.NO_FLOW).getObjValue(); 
				
				//Solution Guarantees
				//1. Agents will get equal or more utility
				cplex.addGe(utility, disagreementUtil);
				
				//We consider only one battery per agent
				IStorage storage = agents[i].getStorage().get(0);
				//2. Agents will use equal or less battery
				cplex.addGe(CommonMethod.sum(agents[i].getOptInfo(Const_ER.NO_FLOW).getChargeMapping().get(storage)), 
						cplex.sum(agentOptModels[i].getStorageToChargeVarsMap().get(storage)));
			}				
			
			//Prepare the objective function
			IloNumExpr objExpr = cplex.numExpr();
			for(int i=0; i<agentOptModels.length; i++)
			{
				IStorage storage = agents[i].getStorage().get(0);
				objExpr = cplex.sum(objExpr, cplex.sum(agentOptModels[i].getStorageToChargeVarsMap().get(storage)));
			}
			
			//We want to produce some community level energy for the communal usage (i.e. street lights etc.)
			objExpr = cplex.sum(objExpr, cplex.prod(0.1, cplex.negative(cplex.sum(communityLink))));
			
			//Add objective function
			IloObjective obj = cplex.minimize(objExpr);
			cplex.add(obj);		
			
			if (cplex.solve() ) { 
				
				cplex.output().println("Solution status = " + cplex.getStatus()); 
				cplex.output().println("Solution value  = " + cplex.getObjValue());
				
				proposedCommunityLink =cplex.getValues(communityLink);
				
				for(int i=0; i<linksVars.size(); i++)
					proposedLinkForEachAgent.add(cplex.getValues(linksVars.get(i)));
				
				//Let's store the optimisation information for each agent
				for(int i=0; i<agents.length; i++)
					{
						OptResult optInfo = agentOptModels[i].makeOptInfo(true);
						
						//System.out.println(optInfo.getObjValue());
						
						MyAgent agent = agents[i];
						/*Here the only problem is that the this optInfo contains the 
						 * objective value of the overall solution and not what the agent's is for this community solution. 
						 * To find the agents utility for this solution
						 */
						
						String optID = Const_ER.COMMUNITY_FLOW;				
						MaxUtilOptModel_ER optimiser = new MaxUtilOptModel_ER(optID, agents[i]);
						OptResult offerOptInfo = optimiser.evaluateOffer(proposedLinkForEachAgent.get(i));
						double util = offerOptInfo.getObjValue();
						
						//Now we know the utility of the agent for the link flow that community optimisation suggested, therefore
						optInfo.setObjValue(util);
						agent.getOptInfoMap().put(optID, offerOptInfo);
					}				
			}
			
			else
			{
				System.err.println("Solution is not solvable");
			}
			cplex.end();
		}
		catch (Exception e) {
			System.out.println("exception:"+e);
			e.printStackTrace();
		}	
		
	}
	
	public ArrayList<double[]> getLinks() {	return proposedLinkForEachAgent;}
	public double[] getCommunityLink(){return proposedCommunityLink;}
	public MyAgent[] getAgents(){return agents;}
	

	
}