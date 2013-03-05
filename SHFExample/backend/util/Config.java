package util;

public interface Config{
	
	String PathToDirectory = "C:/Users/ma2/Dropbox/PhDWork/Java/DeferrableLoadsOptimisation/resources";
	//String PathToDirectory = "C:/Users/Moody/Dropbox/PhDWork/Java/DeferrableLoadsOptimisation/resources";
	String PathToDataDirectory = PathToDirectory+ "/data";
	String PathToResultsDirectory = PathToDirectory+ "/results";
	
	String PathToSolarDataFile =  PathToDataDirectory+"/GenerationSolar.csv";
	String PathToWindDataFile =  PathToDataDirectory+"/GenerationWind.csv";
	String PathToTariffFile =  PathToDataDirectory+"/Tariff.csv";
	String PathToLowIncomeLoadFile =  PathToDataDirectory+"/LowIncomeLoadReq.csv";
	
	int timeslots = 48;
	
	//TODO: This should be dynamic and well below the lowest preferences of any load
	//Both should be in negative
	double wasteIncenstive = 0.0001;
	//double batteryUsagePenality = -0.01;
	double batteryUsagePenality = 0;
			
	double maxLink=5;
	double maxCapacity = 20;
	double maxCharge = 5;
	double maxDischarge = 5;
	double maxEnergyLoss = 0.1;
	
}
