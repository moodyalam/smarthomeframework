package agent;

import java.util.ArrayList;

import loadevents.AbstractDeferrableEvent;
import loadevents.AbstractLoadEvent;
import loadevents.AbstractNonDeferrableEvent;
import loadevents.AbstractOtherLoads;
import storage.IStorage;
import validation.Validate;
import electricvehicle.AbstractElectricVehicle;
import generators.IGenerator;
import generators.IRenewableGenerator;

public class AbstractAgent implements IAgent{

	protected final String id;
	protected AbstractOtherLoads baseLoad;
	protected ArrayList<IStorage> storage = new ArrayList<IStorage>();
	protected ArrayList<AbstractElectricVehicle> electricVehicles = new ArrayList<AbstractElectricVehicle>();
	protected final ArrayList<IRenewableGenerator> renewableGenerators =new ArrayList<IRenewableGenerator>();
	protected final ArrayList<IGenerator> generators =new ArrayList<IGenerator>();
	protected final ArrayList<AbstractDeferrableEvent> deferrableEvents =new ArrayList<AbstractDeferrableEvent>();
	protected final ArrayList<AbstractNonDeferrableEvent> nonDeferrableEvents =  new ArrayList<AbstractNonDeferrableEvent>();
		
	public AbstractAgent(String id)
	{		
		this.id=id;
	}
	
	public void addEvent(AbstractLoadEvent event)
	{				
		if (event instanceof AbstractDeferrableEvent )
		{
			if(Validate.isNew(deferrableEvents, (AbstractDeferrableEvent)event))
				deferrableEvents.add((AbstractDeferrableEvent) event);
		}
		
		else if (event instanceof AbstractNonDeferrableEvent)
		{
			if(Validate.isNew(nonDeferrableEvents, (AbstractNonDeferrableEvent)event))
				nonDeferrableEvents.add((AbstractNonDeferrableEvent) event);
		}				
	}
	
	
	public void addGenerator(IGenerator generator){
		if(Validate.isNew(generators, generator))
			generators.add(generator);
		
		if(generator instanceof IRenewableGenerator)
			renewableGenerators.add((IRenewableGenerator) generator);
	}	
	public void addStorage(IStorage battery){
		if(Validate.isNew(storage, battery))
			storage.add(battery);
	}	
	public void addElectricVehical(AbstractElectricVehicle ev){
		if(Validate.isNew(electricVehicles, ev))
		{
			electricVehicles.add(ev);
			addStorage(ev.getStorage());
		}
		
	}
	
	//Getters
	public ArrayList<AbstractDeferrableEvent> getDeferrableEvents(){return deferrableEvents;}
	public ArrayList<AbstractNonDeferrableEvent> getNonDeferrableEvents(){return nonDeferrableEvents;}
	public ArrayList<IRenewableGenerator> getRenewableGenerators() {return renewableGenerators;}
	public ArrayList<IGenerator> getGenerators() {return generators;}
	public ArrayList<IStorage> getStorage() {return storage;}
	public String getID(){return id;}
	public void setBaseLoad(AbstractOtherLoads baseLoad) { this.baseLoad=baseLoad;}
	public AbstractOtherLoads getBaseLoad() {	return baseLoad;}
	
}
