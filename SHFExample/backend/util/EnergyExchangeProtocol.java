package util;

import java.util.ArrayList;
import java.util.Arrays;

//Energy Exchange Protocol (EEP)
public class EnergyExchangeProtocol {
	
	private static int timeslots = Config.timeslots;
	
	private static int exchangeDuration = 24;
	private static int exchangeStartTimeslots = 12;
	
	
	//private static int exchangeDuration = 1;
	//private static int exchangeStartTimeslots = 0;
	
	
	public static int[][] getAllExchangePeriods()
	{
		int[] indices = new int[timeslots];
		int startIndex = exchangeStartTimeslots;
		int duration = exchangeDuration;

		ArrayList<Integer> exchangePeriodOne = new ArrayList<Integer>();  
		ArrayList<Integer> exchangePeriodTwo = new ArrayList<Integer>();

		int endIndex = (startIndex+(duration));
		
		if (endIndex>=timeslots)
			endIndex%=timeslots;

		for (int i = 0; i < indices.length; i++)
			indices[i]=i;
		
		
		if (endIndex>startIndex)
		{
			for (int i = 0; i < indices.length; i++) {

				if (i>=startIndex & i<endIndex)
					exchangePeriodOne.add(indices[i]);
				else
					exchangePeriodTwo.add(indices[i]);
			}
		}

		else

			{
				for (int i = 0; i < indices.length; i++) {
					if (i>=endIndex & i<startIndex)
						exchangePeriodOne.add(indices[i]);
					else
						exchangePeriodTwo.add(indices[i]);
				}
			}
		
		//OK. Java doesn't let you return multiple arguments - so let's play dirty
		
		int[][] exchangePeriods = new int[2][duration];
		
		for (int i = 0; i < duration; i++) {
			exchangePeriods[0][i]=exchangePeriodOne.get(i);
			exchangePeriods[1][i]=exchangePeriodTwo.get(i);
		}
		
		
		return exchangePeriods;		
		
	}

	public static void main(String[] args) {
		int[][] exchangePeriods = EnergyExchangeProtocol.getAllExchangePeriods();
		//Exchange in one exchange period must be the same
		for(int i=0; i<exchangePeriods.length;i++)
			{
				System.out.println(exchangePeriods[i].length);
				System.out.println(Arrays.toString(exchangePeriods[i]));
				
			}
			
	}
	
	
}
