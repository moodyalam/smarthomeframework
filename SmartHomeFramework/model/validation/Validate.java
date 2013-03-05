package validation;

import java.util.ArrayList;

import loadevents.AbstractDeferrableEvent;
import loadevents.AbstractLoadEvent;
import loadevents.AbstractNonDeferrableEvent;
import storage.IStorage;
import electricvehicle.AbstractElectricVehicle;
import generators.IGenerator;

public class Validate {
	
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
	
	public static boolean isNew(ArrayList<AbstractElectricVehicle> list, AbstractElectricVehicle obj)
	{
		for(AbstractElectricVehicle o:list)
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
	
	
}

