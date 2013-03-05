package dataloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.StringTokenizer;

import util.Config;

public class DataLoader {

	private File dataFile;
	private double[] generation=new double[Config.timeslots];
	private double[] otherLoad=new double[Config.timeslots];
	private String[] labels=new String[Config.timeslots];
	private double[] prefOverOtherLoad = new double[Config.timeslots];

	public DataLoader(String filePath) {
		dataFile = new File(filePath);
		loadAgent();
	}


	public void loadAgent()
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
						generation[index-1] = Double.parseDouble(tokens.nextToken());
						index++;
					}
					break;

				case 2:
					while(tokens.hasMoreTokens())
					{
						otherLoad[index-1] = Double.parseDouble(tokens.nextToken());
						index++;
					}
					break;	
				
				
			case 3:
				while(tokens.hasMoreTokens())
				{
					prefOverOtherLoad[index-1] = Double.parseDouble(tokens.nextToken());
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


	public double[] getGeneration() {
		return generation;
	}


	public double[] getOtherLoad() {
		return otherLoad;
	}
	public double[] getPrefOverOtherLoad() {
		return prefOverOtherLoad;
	}

	public String[] getLabels() {
		return labels;
	}




}
