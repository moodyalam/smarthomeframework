package generators;

public interface IRenewableGenerator extends IGenerator{
	double[] getGenerationSD();
	boolean isControllableCapacity();
}

