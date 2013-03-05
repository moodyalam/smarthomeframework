package visualiser;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import loadevents.AbstractDeferrableEvent;
import loadevents.AbstractNonDeferrableEvent;
import models.MyAgent;
import models.OptResult;

import org.jfree.ui.ApplicationFrame;

import storage.IStorage;


@SuppressWarnings("serial")
public class OptInfoVisualiser extends ApplicationFrame {
	
	public OptInfoVisualiser(MyAgent agent, OptResult info) {
		super(agent.getID()+" - "+info.getOptID());

		ArrayList<AbstractDeferrableEvent> deferrableEvents = agent.getDeferrableEvents();
		ArrayList<AbstractNonDeferrableEvent> nonDeferrableEvents = agent.getNonDeferrableEvents();
		
		
		VisualHelper visualHelper = new VisualHelper();
		
		SpringLayout springLayout = new SpringLayout();
		Container OverallPanel = new JPanel();
		OverallPanel.setLayout(springLayout);

		Component loadChartPanel = visualHelper.createLoadExcecutionChart(info);
		
		//TODO: Remove AAMASVisualHelper later
		Component generationChartPanel = visualHelper.createGenerationChart(info);
		//Component generationChartPanel = new AAMASVisualHelper(visualHelper).createGenerationChart(info);
				
		Component prefChartPanel = visualHelper.createPreferencesCharts(info, nonDeferrableEvents, deferrableEvents);
		
		
		ArrayList<Component> batteryChartPanels = new ArrayList<Component>();
		for(IStorage storage: agent.getStorage())
		{	
			double[] charge = info.getChargeMapping().get(storage); 
			double[] discharge = info.getDischargeMapping().get(storage);
			double[] storedEnergy = info.getStoredEnergyMapping().get(storage);
			
			batteryChartPanels.add(visualHelper.createBatteryWasteLinkGenerationChart(charge, discharge, storedEnergy, info.getWaste(), info.getLink()));
		}
		
		
		JPanel loadPlusGen = new JPanel(new GridLayout(1,2));
		loadPlusGen.add(generationChartPanel);
		loadPlusGen.add(loadChartPanel);
		
		
		JPanel restOfCharts = new JPanel(); 
		restOfCharts.setLayout(new GridLayout(1,2));
		restOfCharts.add(prefChartPanel);
		for(Component c: batteryChartPanels)
			restOfCharts.add(c);
		
		JLabel label = new JLabel(agent.getID()+" - "+info.getOptID());
		
		springLayout.putConstraint(SpringLayout.WEST, label, 0, SpringLayout.WEST, OverallPanel);
		springLayout.putConstraint(SpringLayout.EAST, label, 0, SpringLayout.EAST, OverallPanel);

		springLayout.putConstraint(SpringLayout.NORTH, loadPlusGen, 0, SpringLayout.SOUTH, label);
		springLayout.putConstraint(SpringLayout.WEST, loadPlusGen, 0, SpringLayout.WEST, OverallPanel);
		springLayout.putConstraint(SpringLayout.EAST, loadPlusGen, 0, SpringLayout.EAST, OverallPanel);
		
		springLayout.putConstraint(SpringLayout.NORTH, restOfCharts, 0, SpringLayout.SOUTH, loadPlusGen);
		springLayout.putConstraint(SpringLayout.WEST, restOfCharts, 0, SpringLayout.WEST, OverallPanel);
		springLayout.putConstraint(SpringLayout.EAST, restOfCharts, 0, SpringLayout.EAST, OverallPanel);
		springLayout.putConstraint(SpringLayout.SOUTH, restOfCharts, 0, SpringLayout.SOUTH, OverallPanel);

				
		OverallPanel.add(label);
		OverallPanel.add(loadPlusGen);
		OverallPanel.add(restOfCharts);
		
		//JScrollPane scrollPane = new JScrollPane(OverallPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,  ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		setContentPane(OverallPanel);
		
	}

	public Container getPanel()
	{
		return this.getContentPane();
	}
	
	public static void showPlot(MyAgent agent, OptResult info)
	{
		final OptInfoVisualiser demo = new OptInfoVisualiser(agent, info);
//		demo.pack();
		demo.setVisible(true);
		demo.setPreferredSize(new Dimension(400,400));
		demo.setExtendedState(MAXIMIZED_BOTH);
		// RefineryUtilities.centerFrameOnScreen(demo);
		//setAlwaysOnTop(true);
//		demo.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
	}





}