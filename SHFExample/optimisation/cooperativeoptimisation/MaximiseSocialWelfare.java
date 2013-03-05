package cooperativeoptimisation;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.cplex.IloCplex;

import java.util.ArrayList;

import models.MyAgent;
import optimisation.extended.MaxUtilOptModelCostIncluded;
import util.Config;
import util.Const_ER;


public class MaximiseSocialWelfare {

	
	private int timeslots = Config.timeslots;
	int timeBasedInstances = Config.timeslots;
	
	private MaxUtilOptModelCostIncluded[] agentOptModels;

	private IloCplex cplex;
	private MyAgent[] agents;
	//private final double maxLink = Config.maxLink;
	private ArrayList<IloNumVar[]> links = new ArrayList<IloNumVar[]>();
	private ArrayList<double[]> proposedLinks = new ArrayList<double[]>();
	private double[] proposedCommunityLink = new double[timeslots];
	
	public MaximiseSocialWelfare(MyAgent[] agents) {	
		try{
			this.agents = agents;
			this.cplex = new IloCplex();

			//TODO: Do we need this?
			cplex.setParam(IloCplex.IntParam.SolutionTarget, 2);
			this.agentOptModels = new MaxUtilOptModelCostIncluded[agents.length];
			
			for(int i=0; i<agents.length; i++)
				{	
					agentOptModels[i] =new MaxUtilOptModelCostIncluded(Const_ER.COMMUNITY_FLOW, agents[i], cplex);
					links.add(agentOptModels[i].getLink());
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
				for(int j=0; j<links.size(); j++)
					{
						expr = cplex.sum(expr, links.get(j)[t]);
					}
				
				//sum of all flows at time t must equal the produced community flow for that timeslot
				cplex.addEq(expr,communityLink[t]);
				
			}
			
				//Prepare the objective function
			IloNumExpr objExpr = cplex.numExpr();
				
			for(int i=0; i<agents.length; i++)
				objExpr = cplex.sum(objExpr, agentOptModels[i].prepareUtilityFunction());
			
			
			//Add objective function
			IloObjective obj = cplex.maximize(objExpr);
			cplex.add(obj);		
			
			if ( cplex.solve() ) { 
				cplex.output().println("Solution status = " + cplex.getStatus()); 
				cplex.output().println("Solution value  = " + cplex.getObjValue());
				
				proposedCommunityLink =cplex.getValues(communityLink);
				
				
				for(int i=0; i<links.size(); i++)
					proposedLinks.add(cplex.getValues(links.get(i)));
				
				
			}
			cplex.end();					

		}
		catch (Exception e) {
			System.out.println("exception:"+e);
			e.printStackTrace();
		}	
		
	}
	
	public ArrayList<double[]> getLinks() {	return proposedLinks;}
	public double[] getCommunityLink(){return proposedCommunityLink;}
	public MyAgent[] getAgents(){return agents;}
	

	
}