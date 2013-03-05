package dataloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.StringTokenizer;

import util.Config;

public class TariffLoader {

	private File dataFile;	
	private String[] labels=new String[Config.timeslots];
	private double[] cost=new double[Config.timeslots];
	private double[] carbon=new double[Config.timeslots];
	
	public TariffLoader(String filePath) {
		dataFile = new File(filePath);
		loadTariff();
	}

	public void loadTariff()
	{
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(dataFile));
			String nextLine;

			int lineNumber = 0;

			while ((nextLine = reader.readLine()) != null) {

				StringTokenizer tokens = new StringTokenizer(nextLine,",");
				int index =1;

				tokens.nextToken();
				switch (lineNumber) {
				case 0:
					while(tokens.hasMoreTokens())
					{	

						labels[index-1] =new String(tokens.nextToken());
						index++;
					}
					break;

				case 1:
					while(tokens.hasMoreTokens())
					{
						cost[index-1] = Double.parseDouble(tokens.nextToken());
						index++;
					}
					break;

				case 2:
					while(tokens.hasMoreTokens())
					{
						carbon[index-1] = Double.parseDouble(tokens.nextToken());
						index++;
					}
					break;	
				
			}

				lineNumber++;
			}	
		}

		catch (Exception e) {
			System.out.println(e);
		}
	}


	public double[] getCost() {
		return cost;
	}


	public double[] getCarbon() {
		return carbon;
	}
}
