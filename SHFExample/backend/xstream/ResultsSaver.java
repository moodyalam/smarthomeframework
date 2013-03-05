package xstream;

import models.OptResult;

import com.thoughtworks.xstream.XStream;

public class ResultsSaver {
	
	private static XStream xstream = new XStream();
	
	public static void saveOptInfo(OptResult info)
	{
		System.out.println(xstream.toXML(info));		
		
		
	}
}
