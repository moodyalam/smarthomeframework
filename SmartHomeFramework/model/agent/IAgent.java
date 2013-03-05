package agent;

import electricvehicle.AbstractElectricVehicle;
import generators.IGenerator;
import generators.IRenewableGenerator;

import java.util.ArrayList;

import loadevents.AbstractDeferrableEvent;
import loadevents.AbstractLoadEvent;
import loadevents.AbstractNonDeferrableEvent;
import loadevents.AbstractOtherLoads;
import storage.IStorage;

public interface IAgent {
	String getID(); 
	void addEvent(AbstractLoadEvent event);
	void addGenerator(IGenerator generator);
	void addStorage(IStorage battery);
	void addElectricVehical(AbstractElectricVehicle ev);
	void setBaseLoad(AbstractOtherLoads baseLoad);
	AbstractOtherLoads getBaseLoad();
	ArrayList<AbstractDeferrableEvent> getDeferrableEvents();
	ArrayList<AbstractNonDeferrableEvent> getNonDeferrableEvents();
	ArrayList<IRenewableGenerator> getRenewableGenerators();
	ArrayList<IGenerator> getGenerators();
	ArrayList<IStorage> getStorage();
}
