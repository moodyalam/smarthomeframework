package dataloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.StringTokenizer;

import util.Config;

public class LoadLoader {

	private File dataFile;	
	private String[] labels=new String[Config.timeslots];
	private double[] load=new double[Config.timeslots];
	
	public LoadLoader(String filePath) {
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
						load[index-1] = Double.parseDouble(tokens.nextToken());
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


	public double[] getLoad() {
		return load;
	}
}
