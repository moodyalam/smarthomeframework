package optimisation.er;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import models.MyAgent;
import models.OptResult;
import optimisation.extended.MaxUtilOptModelCostIncluded;
import util.Const_ER.ExchangeRelatedLinkStatus;

public class MaxUtilOptModel_ER extends MaxUtilOptModelCostIncluded{

	public MaxUtilOptModel_ER(String optID, MyAgent agent) throws IloException {
		super(optID, agent);
	}
	
	public MaxUtilOptModel_ER(String optID, MyAgent agent, IloCplex cplex) throws IloException {	
		super(optID, agent, cplex);		
	}
	
	
	//TODO: Energy Exchange related - move this method
	public OptResult evaluateOffer(double[] offer) throws IloException
	{
		for (int i=0; i<offer.length; i++)
			cplex.addEq(link[i],offer[i]);		
		return optimise(ExchangeRelatedLinkStatus.MUST_MEET_OFFER);
	}

	//TODO: Energy Exchange related - move this method
	public OptResult optimise(ExchangeRelatedLinkStatus linkFlowStatus) throws IloException
	{
		if(!agent.isConnected())
		{
			System.err.println("Exchange relatd optimisation can only be carried out with agent link flow set to true");
			throw new IloException();
		}
		
		//Add link flow constraints
		OptUtil_ER.addLinkFLowConstraints(cplex, linkFlowStatus, link);
		
		return optimise();
	}
	
}
