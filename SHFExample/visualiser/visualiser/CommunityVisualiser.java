package visualiser;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import models.MyAgent;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleEdge;

import storage.IStorage;
import util.CommonMethod;
import util.Const_ER;
import cooperativeoptimisation.MinimizeBatteryUsage;

@SuppressWarnings("serial")
public class CommunityVisualiser extends ApplicationFrame{
	private final MinimizeBatteryUsage comOpt;
	private final VisualHelper visualHelper = new VisualHelper();
	
	public CommunityVisualiser(MinimizeBatteryUsage comOpt) {
		super("CommunityVisualiser");
		this.comOpt = comOpt;
		
		double[] fairness = new double[comOpt.getAgents().length];
		
		double totalIndividualCharge = 0;
		double totalCommunityCharge = 0;
		
		//Compute the reduction in battery usage
		for (int i = 0; i < fairness.length; i++) {
			MyAgent agent = comOpt.getAgents()[i]; 
			
			IStorage storage = agent.getStorage().get(0);
			double individualCharge = CommonMethod.sum(agent.getOptInfo(Const_ER.NO_FLOW).getChargeMapping().get(storage));
			double communityCharge = CommonMethod.sum(agent.getOptInfo(Const_ER.COMMUNITY_FLOW).getChargeMapping().get(storage));
			fairness[i] = (individualCharge-communityCharge);
			
			totalIndividualCharge+=individualCharge;
			totalCommunityCharge+=communityCharge;
		}
	
		//Create plots
		XYPlot linkPlot = visualHelper.createDefaultXYPlot("", comOpt.getCommunityLink());
		linkPlot.getRangeAxis().setAutoRange(true);
		ChartPanel linkChart = new ChartPanel(new JFreeChart(linkPlot));
		linkChart.getChart().setTitle("Community Link - (sum = "+CommonMethod.truncatedSum(comOpt.getCommunityLink(), 2)+" kWh)");
		linkChart.getChart().getLegend().setPosition(RectangleEdge.TOP); 
		linkChart.getChart().getLegend().setVisible(false);
		
		CategoryPlot fairnessPlot = visualHelper.createFairnessPlot("", fairness);
		
		ChartPanel fairnessChart = new ChartPanel(new JFreeChart(fairnessPlot));
		fairnessChart.getChart().setTitle("Reduction in Battery Usage (kWh) : " +
				CommonMethod.truncate(totalCommunityCharge,2)+"/"+CommonMethod.truncate(totalIndividualCharge,2)+
				"= "+ CommonMethod.truncate(( ((totalIndividualCharge-totalCommunityCharge)/totalIndividualCharge )*100),2)+"%");
		
		fairnessChart.getChart().getLegend().setVisible(false); 
		
		
		//Add plots
		JPanel plotsPanel = new JPanel(new GridLayout(1,2));
		
		plotsPanel.add(linkChart);
		plotsPanel.add(fairnessChart);
		
		//create tables
		CommunityStatsTable NoLinkTable = new CommunityStatsTable(comOpt.getAgents(), Const_ER.NO_FLOW);
		CommunityStatsTable ComLinkTable = new CommunityStatsTable(comOpt.getAgents(), Const_ER.COMMUNITY_FLOW);
		
		//Add tables
		JPanel tablePanel = new JPanel(new GridLayout(1,2));
        tablePanel.setOpaque(true);
        tablePanel.add(NoLinkTable);
        tablePanel.add(ComLinkTable);
		
        Container contentPane = this.getContentPane();
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		JLabel label = new JLabel("Community Optimiser");
		
		//Add panels seperater
		JPanel inBetweenPanel = new JPanel();
		inBetweenPanel.setLayout(new GridLayout(1,2));
		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel l1 = new JLabel("Individual Optimisation");
		l1.setFont(new Font("Dialog", Font.PLAIN, 24));
		p1.setBackground(Color.lightGray);
		p1.add(l1);
		
		
		JPanel p2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel l2 = new JLabel("Community Optimisation");
		l2.setFont(new Font("Dialog", Font.PLAIN, 24));
		p2.setBackground(Color.lightGray);
		p2.add(l2);
		
		inBetweenPanel.add(p1);
		inBetweenPanel.add(p2);
		
		springLayout.putConstraint(SpringLayout.WEST, label, 0, SpringLayout.WEST, contentPane);
		springLayout.putConstraint(SpringLayout.EAST, label, 0, SpringLayout.EAST, contentPane);
		
		springLayout.putConstraint(SpringLayout.NORTH, plotsPanel, 0, SpringLayout.SOUTH, label);
		springLayout.putConstraint(SpringLayout.WEST, plotsPanel, 0, SpringLayout.WEST, contentPane);
		springLayout.putConstraint(SpringLayout.EAST, plotsPanel, 0, SpringLayout.EAST, contentPane);
		
		springLayout.putConstraint(SpringLayout.NORTH, inBetweenPanel, 0, SpringLayout.SOUTH, plotsPanel);
		springLayout.putConstraint(SpringLayout.WEST, inBetweenPanel, 0, SpringLayout.WEST, contentPane);
		springLayout.putConstraint(SpringLayout.EAST, inBetweenPanel, 0, SpringLayout.EAST, contentPane);
		
		
		springLayout.putConstraint(SpringLayout.NORTH, tablePanel, 0, SpringLayout.SOUTH, inBetweenPanel);
		springLayout.putConstraint(SpringLayout.WEST, tablePanel, 0, SpringLayout.WEST, contentPane);
		springLayout.putConstraint(SpringLayout.EAST, tablePanel, 0, SpringLayout.EAST, contentPane);
		springLayout.putConstraint(SpringLayout.SOUTH, tablePanel, 0, SpringLayout.SOUTH, contentPane);
		
		
        contentPane.add(label);
		contentPane.add(plotsPanel);
		contentPane.add(inBetweenPanel);
        contentPane.add(tablePanel);
        
        
		setPreferredSize(new Dimension(400,400));
		setExtendedState(MAXIMIZED_BOTH);
		setVisible(true);
	}
	
	
	
}
