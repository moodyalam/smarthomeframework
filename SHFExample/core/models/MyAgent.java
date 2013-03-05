package models;

import java.util.ArrayList;
import java.util.HashMap;

import loadevents.AbstractDeferrableEvent;
import loadevents.AbstractLoadEvent;
import loadevents.AbstractNonDeferrableEvent;
import validation.ValidateHere;
import agent.AbstractSocialAgent;
import appliances.extended.HVACSystem.ElectricHeater;

public class MyAgent extends AbstractSocialAgent
{
	protected BaseLoad simpleLoad=new BaseLoad(false);
	protected final HashMap<String, OptResult> optInfoMap = new HashMap<String, OptResult>();
	protected final ArrayList<NonDeferrableLoadEvent> heatingLoadEvents = new ArrayList<NonDeferrableLoadEvent>();
	
	public MyAgent(String id) {
		super(id);
	}
	
	public void addOptInfo(String id, OptResult optInfo){optInfoMap.put(id, optInfo);}
	public OptResult getOptInfo(String optID) {return optInfoMap.get(optID);}
	public HashMap<String, OptResult> getOptInfoMap(){return optInfoMap;}
	public ArrayList<NonDeferrableLoadEvent> getHeatingLoadEvents() {return heatingLoadEvents;}
	
	public void addEvent(AbstractLoadEvent event)
	{				
		if (event instanceof AbstractDeferrableEvent )
		{
			if(ValidateHere.isNew(deferrableEvents, (AbstractDeferrableEvent)event))
				deferrableEvents.add((AbstractDeferrableEvent) event);
		}
		
		else if (event instanceof AbstractNonDeferrableEvent)
		{
			if(ValidateHere.isNew(nonDeferrableEvents, (AbstractNonDeferrableEvent)event))
				nonDeferrableEvents.add((AbstractNonDeferrableEvent) event);
		}				
		
		if(event.getAppliance() instanceof ElectricHeater)
			heatingLoadEvents.add((NonDeferrableLoadEvent) event);
	}
	
}