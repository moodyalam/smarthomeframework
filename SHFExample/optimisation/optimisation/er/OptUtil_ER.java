package optimisation.er;

import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import util.Config;
import util.Const_ER.ExchangeRelatedLinkStatus;
import util.EnergyExchangeProtocol;

public class OptUtil_ER {

	private static int timeslots = Config.timeslots;
	
	public static void addLinkFLowConstraints(IloCplex cplex,  ExchangeRelatedLinkStatus linkFlowStatus, IloNumVar[] link) throws IloException
	{
		//Link flow constraints
		IloNumExpr expr=null;
		switch(linkFlowStatus){

		case SUM_ZERO_ONE_FLOW:
			expr=null;				
			int[][] exchangePeriods = EnergyExchangeProtocol.getAllExchangePeriods();
			//Exchange in one exchange period must be the same
			for(int i=0; i<exchangePeriods.length;i++)
				for(int j=0; j<exchangePeriods[i].length-1;j++)
					cplex.addEq(link[exchangePeriods[i][j]], link[exchangePeriods[i][j+1]]);				
			//NOTE: We don't want to break here because we want to include the SUM_ZERO_ANY_FLOW condition just below:

		case SUM_ZERO_ANY_FLOW:
			expr=null;
			for (int i=0; i<timeslots; i++)
				if(expr==null)
					expr = link[0];				
				else
					expr = cplex.sum(expr,link[i]);				

			cplex.addEq(0, expr);
			break;

		case MUST_MEET_OFFER:
			//Do nothing here - as the bounds of link[] is already set to given offer.
			break;

		case NBS:
			System.out.println("BatteryUsageOptimiser: NBS is not implemented");
			break;

		}
	}
	
	
	
}
