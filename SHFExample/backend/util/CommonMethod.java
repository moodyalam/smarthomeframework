package util;

import java.util.ArrayList;

public class CommonMethod {

	public static double[] multiplyArrayElementsWith(double[] array, double value)
	{
		for (int i = 0; i < array.length; i++) 
			array[i]*= value;
		return array;
	}
	
	public static ArrayList<Integer> getNextIndices(int totalPeriods, int startIndex, int numberOfNextIndices, boolean includeStartIndex)
	{
		ArrayList<Integer> list = new ArrayList<>();
		
		if(!includeStartIndex)
			startIndex++;
			
		for (int i=0; i<numberOfNextIndices; i++)
			list.add((startIndex+i)%totalPeriods);
		
		//if(!includeStartIndex)
			//list.remove(new Integer(startIndex));
		return list;		
	}
	
	public static double sumNegatives(double[] val)
	{
		double sum =0;
		
		for(double d: val)
			if(d<0)
				sum+=d;
		
		return sum;
	}
	
	public static double sumPositives(double[] val)
	{
		double sum =0;
		
		for(double d: val)
			if(d>0)
				sum+=d;
		
		return sum;
	}
	
	public static double[] negate(double[] in)
	{
		double [] out = new double[in.length];
		for (int i = 0; i < in.length; i++) 
			out[i]= -in[i];
		
		return out;
			
	}
	
	public static double truncate(double d, int places)
	{
		long l = Math.round(d*(Math.pow(10,places)));
		
		return (double)(l/Math.pow(10,places));
	}
	
	public static double truncatedSum(double[] values, int places)
	{
		return truncate(sum(values), places);
	}
	
	public static double sum(double[] values)
	{
		double sum =0;
		for(double i: values)
			sum+=i;
		return sum;
	}


	public static double[] sum(double[] val1, double val2)
	{
		double[] sum = new double[val1.length];
				
		for (int i = 0; i < val1.length; i++)
			sum[i] = val1[i]+val2;
		return sum;
	}

	
	public static double[] sum(double[] val1, double[] val2)
	{
		double[] sum = new double[val1.length];
				
		for (int i = 0; i < val2.length; i++)
			sum[i] = val1[i]+val2[i];
		return sum;
	}
	
	
	public static char max(char[] values) {
		char max = Character.MIN_VALUE;
		for(char value : values) {
			if(value > max)
				max = value;
		}
		return max;
	}

	public static byte max(byte[] values) {
		byte max = Byte.MIN_VALUE;
		for(byte value : values) {
			if(value > max)
				max = value;
		}
		return max;
	}

	public static short max(short[] values) {
		short max = Short.MIN_VALUE;
		for(short value : values) {
			if(value > max)
				max = value;
		}
		return max;
	}

	public static int max(int[] values) {
		int max = Integer.MIN_VALUE;
		for(int value : values) {
			if(value > max)
				max = value;
		}
		return max;
	}

	public static long max(long[] values) {
		long max = Long.MIN_VALUE;
		for(long value : values) {
			if(value > max)
				max = value;
		}
		return max;
	}
	
	public static double max(double[] values) {
		double max = Double.MIN_VALUE;
		for(double value : values) {
			if(value > max)
				max = value;
		}
		return max;
	}
	

	public static char maxChar(char... values) {
		return max(values);
	}

	public static byte maxByte(byte... values) {
		return max(values);
	}

	public static short maxShort(short... values) {
		return max(values);
	}

	public static int maxInt(int... values) {
		return max(values);
	}

	public static long maxLong(long... values) {
		return max(values);
	}

	public static char min(char[] values) {
		char min = Character.MAX_VALUE;
		for(char value : values) {
			if(value < min)
				min = value;
		}
		return min;
	}

	public static byte min(byte[] values) {
		byte min = Byte.MAX_VALUE;
		for(byte value : values) {
			if(value < min)
				min = value;
		}
		return min;
	}

	public static short min(short[] values) {
		short min = Short.MAX_VALUE;
		for(short value : values) {
			if(value < min)
				min = value;
		}
		return min;
	}

	public static int min(int[] values) {
		int min = Integer.MAX_VALUE;
		for(int value : values) {
			if(value < min)
				min = value;
		}
		return min;
	}

	public static long min(long[] values) {
		long min = Long.MAX_VALUE;
		for(long value : values) {
			if(value < min)
				min = value;
		}
		return min;
	}

	public static double min(double[] values) {
		double min = Double.MAX_VALUE;
		for(double value : values) {
			if(value < min)
				min = value;
		}
		return min;
	}
	
	public static char minChar(char... values) {
		return min(values);
	}

	public static byte minByte(byte... values) {
		return min(values);
	}

	public static short minShort(short... values) {
		return min(values);
	}

	public static int minInt(int... values) {
		return min(values);
	}

	public static long minLong(long... values) {
		return min(values);
	}
}