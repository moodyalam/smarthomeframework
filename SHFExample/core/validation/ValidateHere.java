package validation;

import generators.IGenerator;

import java.util.ArrayList;

import loadevents.AbstractDeferrableEvent;
import loadevents.AbstractLoadEvent;
import loadevents.AbstractNonDeferrableEvent;
import models.ElectricVehicle;
import storage.IStorage;
import util.Config;

public class ValidateHere {
	
	private static int timeslots = Config.timeslots;

	public static boolean isNew(ArrayList<IStorage> list, IStorage obj)
	{
		for(IStorage o:list)
			if(o.getName().equals(obj.getName()))
			{
				System.err.println("Duplicate Object: "+obj+" not allowed");
				return false;
			}
		
		return true;		
	}
	
	public static boolean isNew(ArrayList<ElectricVehicle> list, ElectricVehicle obj)
	{
		for(ElectricVehicle o:list)
			if(o.getName().equals(obj.getName()))
				{
					System.err.println("Duplicate Object: "+obj+" not allowed");
					return false;
				}
		return true;
		
	}
	
	public static boolean isNew(ArrayList<IGenerator> list, IGenerator obj)
	{
		for(IGenerator o:list)
			if(o.getName().equals(obj.getName()))
				{
					System.err.println("Duplicate Object: "+obj+" not allowed");
					return false;
				}
		return true;
		
	}
	
	
	public static boolean isNew(ArrayList<AbstractNonDeferrableEvent> list, AbstractNonDeferrableEvent obj)
	{
		for(AbstractLoadEvent o:list)
			if(o.getName().equals(obj.getName()))
				{
					System.err.println("Duplicate Object: "+obj+" not allowed");
					return false;
				}
		return true;		
	}
	
	public static boolean isNew(ArrayList<AbstractDeferrableEvent> list, AbstractDeferrableEvent obj)
	{
		for(AbstractLoadEvent o:list)
			if(o.getName().equals(obj.getName()))
				{
					System.err.println("Duplicate Object: "+obj+" not allowed");
					return false;
				}
		return true;		
	}
	
	public static boolean validTimeslot(int timeslot)
	{
		if( !(timeslot>=0 && timeslot<timeslots))
		{
			System.err.println("Invalid Timesslot: Timeslot-"+timeslot);
			return false;
		}
		
		return true;
		
	}
	
	
	public static boolean validLoadEventTimes(int startTime, int endTime, int runLength)
	{
		if(!(validTimeslot(startTime) && validTimeslot(endTime) && validTimeslot(runLength))) 
		{
			System.err.println("Invalid Times for Load event: StartTime:"+startTime+" EndTime:"+endTime +" runLength:"+runLength);
			return false;
		}
			
		
		if((startTime+runLength)>=endTime)
		{
			System.err.println("Invalid Times for Load event: StartTime:"+startTime+" EndTime:"+endTime +" runLength:"+runLength);
			return false;
		}
		
		return true;
		
	}
	
	
}

