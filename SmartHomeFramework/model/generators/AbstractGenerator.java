package generators;

public abstract class AbstractGenerator implements IGenerator{
	final String name;
	final double maxGenCapcacitykW; //kW
	
	public AbstractGenerator(String name, double maxGenCapacitykW)
	{
		this.name = name;
		this.maxGenCapcacitykW = maxGenCapacitykW;		
	}
	
	public String getName() {return	name;}
	public double getMaxGenerationCapacity() {return maxGenCapcacitykW;}
	
}
