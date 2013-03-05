package generators;

public interface IGenerator {

	String getName();
	double getMaxGenerationCapacity();
	double getMaxCarbonCost();
	double getMaxCost();
	
	double[] getGeneration();	
	double[] getCarbonCost();
	double[] getCost();
	
	
}
