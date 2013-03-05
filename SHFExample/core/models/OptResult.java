package models;

import generators.IGenerator;

import java.util.Arrays;
import java.util.HashMap;

import loadevents.AbstractLoadEvent;
import optresults.IOptResult;
import storage.IStorage;
import util.CommonMethod;


public class OptResult implements IOptResult{
	
	public enum Keys {objValue, baseLoad, waste, link, generatorUsageMap, generatorCostMap, generatorCarbonMap,
		loadPowerMapping, satisfiedLoadPowerMapping, chargeMapping, dischargeMapping, storedEnergyMapping, evUsageEnergyMapping};
	
	
	private final HashMap<Keys, Object> map = new HashMap<OptResult.Keys, Object>();
		
	private final String OptID;
	private final MyAgent agent;
	private final boolean isSolved;

	public void put(Keys key, Object value)
	{
		map.put(key, value);
	}
	
	public Object get(Keys key)
	{
		return map.get(key);
	}
	
	
	public OptResult(String optID, MyAgent agent, boolean isSolved) {			
		this.OptID = optID;
		this.agent = agent;
		this.isSolved = isSolved;

		
		//This bit ensures that the battery charge hits zero at some point, which  
		//has no impact on the results apart from the fact that the graph looks nicer!!
		/*
			for(IStorage storage: storedEnergyMapping.keySet())
			{
				double[] storedEnergy = storedEnergyMapping.get(storage);
				double minBatteryUsage = CommonMethod.min(storedEnergy);
				storedEnergyMapping.put(storage,CommonMethod.sum(storedEnergy, -minBatteryUsage));
			}
		 */
		
	}	

	private HashMap<AbstractLoadEvent, double[]> findSatisfiedLoadPowerMapping(HashMap<AbstractLoadEvent, double[]> loadPowerMapping){
		
		HashMap<AbstractLoadEvent, double[]> temp = new HashMap<AbstractLoadEvent, double[]>();
		for (AbstractLoadEvent event: loadPowerMapping.keySet())
			if(CommonMethod.sum(loadPowerMapping.get(event))>0)
				temp.put(event, loadPowerMapping.get(event));
		return temp;		
	}
		
	
    @Override
    public String toString() {
    
		String s = "Agent:"+agent.getID()+"\n";
		
		s+= "BaseLoad.[sum="+CommonMethod.sum(getBaseLoad())+"]: "+Arrays.toString(getBaseLoad())+"\n";
		s+= "Waste[sum="+CommonMethod.sum(getWaste())+"]: "+Arrays.toString(getWaste())+"\n";
		s+= "link[sum="+CommonMethod.sum(getLink())+"]: "+Arrays.toString(getLink())+"\n";
		
		for (IGenerator generator : getGeneratorUsageMap().keySet())
			s+= generator.getName()+"[sum="+CommonMethod.sum(getGeneratorUsageMap().get(generator))+"]: "+Arrays.toString(getGeneratorUsageMap().get(generator))+"\n";
				
		for (AbstractLoadEvent load: getSatisfiedLoadPowerMapping().keySet())
			s+= load.getName()+"[sum="+CommonMethod.sum(getSatisfiedLoadPowerMapping().get(load))+"]: "+Arrays.toString(getSatisfiedLoadPowerMapping().get(load))+"\n";
		
		return s;
    	
    }
    
    public HashMap<IGenerator, double[]> getGeneratorUsageMap(){return (HashMap<IGenerator, double[]>) get(Keys.generatorUsageMap);}
    public HashMap<IGenerator, double[]> getGeneratorCostMap(){return (HashMap<IGenerator, double[]>) get(Keys.generatorCostMap);}
    public HashMap<IGenerator, double[]> getGeneratorCarbonMap(){return (HashMap<IGenerator, double[]>) get(Keys.generatorCarbonMap);}
    public HashMap<AbstractLoadEvent, double[]> getSatisfiedLoadPowerMapping() {return (HashMap<AbstractLoadEvent, double[]>) get(Keys.loadPowerMapping);}
	public HashMap<IStorage, double[]> getChargeMapping() {return (HashMap<IStorage, double[]>) get(Keys.chargeMapping);}
	public HashMap<IStorage, double[]> getDischargeMapping() {return (HashMap<IStorage, double[]>) get(Keys.dischargeMapping);}
	public HashMap<IStorage, double[]> getStoredEnergyMapping() {return (HashMap<IStorage, double[]>) get(Keys.storedEnergyMapping);}
	public HashMap<IStorage, double[]> getEVUsageEnergyMapping() {return (HashMap<IStorage, double[]>) get(Keys.evUsageEnergyMapping);}
	public double[] getBaseLoad() {return (double[]) get(Keys.baseLoad);}
	public double[] getWaste() {return (double[]) get(Keys.waste);}
	public double[] getLink() {return (double[]) get(Keys.link);	}
	public double getObjValue(){return (double) get(Keys.objValue);}
		
	public boolean isSolved(){return isSolved;}	
	public MyAgent getAgent(){return agent;}
	public String getOptID(){return OptID;}
	
	//This method is needed because in the community level optimisation this value needs to be set 
	//from the point of view of the community
	public void setObjValue(double objValue){put(Keys.objValue, objValue);}

	@Override
	public double getObjectiveFunctionValue() {
		return 0;
	}

	@Override
	public Object getValue(String key) {
		return null;
	}

	
}
