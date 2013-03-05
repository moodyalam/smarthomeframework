package appliances;

public abstract class AbstractAppliance implements IAppliance{

	protected final String name;
	protected final double kW;
	
	public AbstractAppliance(String name, double kW) {
	this.name = name;
	this.kW = kW;
	}	
	public String getName(){return name;}
	public double getMaxPower(){return kW;}		
}
