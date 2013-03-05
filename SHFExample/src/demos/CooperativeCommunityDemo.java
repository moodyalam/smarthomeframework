package demos;

import models.AgentsFactory;
import models.MyAgent;
import optimisation.extended.MaxUtilOptModel;
import util.Const_ER;
import visualiser.CommunityVisualiser;
import cooperativeoptimisation.MinimizeBatteryUsage;

public class CooperativeCommunityDemo {

	public static void main(String[] args) {
		try{
			demo();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void demo() {
		try{
			int population =10;

			AgentsFactory agentsPool = new AgentsFactory();
			MyAgent[] agents = agentsPool.getAgents(population);

			for (int i = 0; i < population; i++) 	
			{
				MyAgent agent = agents[i];

				String optID = Const_ER.NO_FLOW;
				MaxUtilOptModel optimiser = new MaxUtilOptModel(optID, agent);
				agent.addOptInfo(optID, optimiser.optimise());
				//OptInfoVisualiser.showPlot(agent, agent.getOptInfo(optID));	
			}

			//Now let's make sure that the agents are connect 
			for (int i = 0; i < population; i++)
				agents[i].setConnected(true);

			MinimizeBatteryUsage comOpt = new MinimizeBatteryUsage(agents);
			comOpt.optimise();
			new CommunityVisualiser(comOpt);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}










/*
for (int i = 0; i < population; i++){
  if(population==2)

	{
		new CompareOptInfo("Optimisation Comparison", 
				agents[i], agents[i].getOptInfo(Const_ER.NO_FLOW), 
				agents[i], agents[i].getOptInfo(Const_ER.COMMUNITY_FLOW));


	}			
}

if(population==2)
OptInfoVisualiser.showPlot(agent, agent.getOptInfo(optID));	
OptInfoVisualiser.showPlot(agents[i], agents[i].getOptInfo(LinkFlowStatus.COMMUNITY_FLOW));
 */

