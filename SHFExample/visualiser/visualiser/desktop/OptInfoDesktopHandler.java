package visualiser.desktop;

import javax.swing.JFrame;

import models.MyAgent;
import models.OptResult;

public class OptInfoDesktopHandler {
	
	private OptResult optInfo;
	private MyAgent agent;
	private SampleDesktop sampleDesktop;
	
	public OptInfoDesktopHandler(OptResult optInfo) {
		
	this.optInfo = optInfo;
	this.agent = agent;
	this.sampleDesktop = new SampleDesktop("Smart Home Simulator", optInfo);
	
	
	//sampleDesktop.setSize(300, 220);
	sampleDesktop.setExtendedState(JFrame.MAXIMIZED_BOTH);
    sampleDesktop.setVisible(true);
	
	}
	
	

}
