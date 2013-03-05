package visualiser;

import generators.IGenerator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import loadevents.AbstractDeferrableEvent;
import loadevents.AbstractLoadEvent;
import loadevents.AbstractNonDeferrableEvent;
import models.MyAgent;
import models.NonDeferrableLoadEvent;
import models.OptResult;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StackedXYBarRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimePeriodValue;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.data.time.TimeTableXYDataset;

import storage.IMobileStorage;
import storage.IStorage;
import util.CommonMethod;
import util.Config;
import appliances.extended.HVACSystem.ElectricHeater;



public class VisualHelper {

	Color[] colorChoice = new Color[]{Color.blue, Color.GREEN, Color.ORANGE, Color.pink, Color.cyan, Color.gray, Color.magenta, Color.YELLOW};
	Color baseLoadColor = Color.red;
	Color otherLoadColor = Color.black;
	Color thermalLoadColor = Color.black;
	Color unsatisfiedEventsColor = Color.white;
	
	Calendar calendar = new GregorianCalendar();
	int timeslots = Config.timeslots;
	int oneTimeslotInMinutes = (int) (60.0d/(timeslots/24.0d)); 
	HashMap<AbstractLoadEvent, Color> loadColorMap = new HashMap<AbstractLoadEvent, Color>();
	TimePeriod[] periods = new TimePeriod[Config.timeslots];
	final DateAxis timeDomainAxis = new DateAxis("Time");
	
	public VisualHelper() {

		calendar.clear();
		calendar.set(2012, 04, 12, 0, 0);
		for(int i=0; i<timeslots; i++)
		{
			Date start = calendar.getTime();
			calendar.add(Calendar.MINUTE, oneTimeslotInMinutes);
			Date end = calendar.getTime();
			periods[i] = new SimpleTimePeriod(start, end);
		}
		
		timeDomainAxis.setAxisLineVisible(false);
		timeDomainAxis.setLowerMargin(0);
		timeDomainAxis.setUpperMargin(0);
	}
	
	
	public HashMap<String, Component> getAgentRelatedCharts(OptResult optInfo)
	{
		ArrayList<NonDeferrableLoadEvent> heatingLoadEvents = optInfo.getAgent().getHeatingLoadEvents();
		double[] desiredHeatingLoad = new double[Config.timeslots];
		
		for(int i=0; i<heatingLoadEvents.size(); i++)
			desiredHeatingLoad[heatingLoadEvents.get(i).getStartTimeslot()]=heatingLoadEvents.get(i).getMaxPowerRequired();
		
		HashMap<String, Component> map = new HashMap<String, Component>(); 
		map.put("Generation", createGenerationChart(optInfo)); 
		map.put("Thermal", createThermalDesiredLoadsChart(desiredHeatingLoad));
		return map;		
	}
	
	public HashMap<String, Component> getOptimisationRelatedCharts(OptResult optInfo,
			ArrayList<AbstractNonDeferrableEvent> nonDeferrableEvents, ArrayList<AbstractDeferrableEvent> deferrableEvents)
	{
		MyAgent agent = optInfo.getAgent();
		HashMap<String, Component> map = new HashMap<String, Component>(); 
		map.put("Load Execution Plan", createLoadExcecutionChart(optInfo));
		map.put("Preference Chart", createPreferencesCharts(optInfo, agent.getNonDeferrableEvents(), agent.getDeferrableEvents()));
		for(IStorage storage: agent.getStorage())
			map.put(storage.getName(), 
					createStorageCharts(storage,
							optInfo.getChargeMapping().get(storage),
							optInfo.getDischargeMapping().get(storage), 
							optInfo.getStoredEnergyMapping().get(storage),
							optInfo.getEVUsageEnergyMapping().get(storage)
							));
		
		return map;		
	}
	
	
	public ChartPanel createThermalDesiredLoadsChart(double[] thermalLoads)
	{
		XYPlot plot =createDefaultXYPlot("Thermal Load (kWh)", thermalLoads);
		//plot.getRangeAxis().setRange(-Config.maxLink, Config.maxLink);
		plot.setDomainAxis(timeDomainAxis);
		
		String title = "Thermal Loads";
		final JFreeChart chart = new JFreeChart(
				title, new Font("SansSerif", Font.BOLD, 12),
				plot, false);
		return new ChartPanel(chart);
		
	}
	
	private NumberAxis createDefaultNumberAxis(String title)
	{
		NumberAxis numberAxis = new NumberAxis(title);
		numberAxis.setRange(0, 5);
		return numberAxis;
	}
	
	public CategoryPlot createFairnessPlot(String title, double[] val)
	{
		final XYBarRenderer barRenderer = new XYBarRenderer();
		barRenderer.setSeriesPaint(0, Color.red);

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		double[][] data = new double[val.length][2];
		
		for(int i=0; i<data.length; i++)
			{
				data[i][0] = i;
				data[i][1] = val[i];
				dataset.addValue(val[i], "Fairness1", ""+i);
			}
		
		JFreeChart chart = ChartFactory.createBarChart("Fairness", 
				"Agents", "Reduction in BatteryUsage (kWh)", dataset, PlotOrientation.VERTICAL, false, false, false);
		return (CategoryPlot) chart.getPlot();		
	}
	
	public XYPlot createDefaultXYPlot(String title, double[] val)
	{
		return createDefaultXYPlot(title, val, Color.red);		
	}
	
	public XYPlot createDefaultXYPlot(String title, double[] val, Color barRendererColor)
	{
		final XYBarRenderer barRenderer = new XYBarRenderer();
		barRenderer.setSeriesPaint(0, barRendererColor);
		
		TimePeriodValues values = new TimePeriodValues(title);
	    
		for(int i=0; i<val.length; i++)
			values.add( new TimePeriodValue(periods[i], val[i]));
	    TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
	    dataset.addSeries(values);
	    
		return new XYPlot(dataset, timeDomainAxis, createDefaultNumberAxis(title), barRenderer);		
	}
	
	public ChartPanel createStorageCharts(IStorage storage, double[] charge, double[] discharge, double[] storedEnergy, double[] evConsumption)
	{
		
		final XYBarRenderer chargeBarRenderer = new XYBarRenderer();
		chargeBarRenderer.setSeriesPaint(0, Color.red);
		//chargeBarRenderer.setShadowVisible(false);
		
		final XYBarRenderer dischargeBarRenderer = new XYBarRenderer();
		dischargeBarRenderer.setSeriesPaint(0, Color.red);
		
		final XYBarRenderer evUsageBarRenderer = new XYBarRenderer();
		evUsageBarRenderer.setSeriesPaint(0, Color.green);
		
		//dischargeBarRenderer.setShadowVisible(false);
		
		final XYLineAndShapeRenderer lineRenderer = new XYLineAndShapeRenderer();
		lineRenderer.setSeriesPaint(0, Color.blue);

		TimePeriodValues values = new TimePeriodValues("Charge");
		for(int i=0; i<charge.length; i++)
			values.add( new TimePeriodValue(periods[i], charge[i]));
	    TimePeriodValuesCollection chargeDataset = new TimePeriodValuesCollection();
	    chargeDataset.addSeries(values);
		
		values = new TimePeriodValues("Discharge");
		for(int i=0; i<discharge.length; i++)
			values.add( new TimePeriodValue(periods[i], discharge[i]));
	    TimePeriodValuesCollection dischargeDataset = new TimePeriodValuesCollection();
	    dischargeDataset.addSeries(values);
	    
	    
		values = new TimePeriodValues("EVUsage");
		for(int i=0; i<evConsumption.length; i++)
			values.add( new TimePeriodValue(periods[i], evConsumption[i]));
	    TimePeriodValuesCollection evDataset = new TimePeriodValuesCollection();
	    evDataset.addSeries(values);
	    
	    
	    values = new TimePeriodValues("Battery Charge");
		for(int i=0; i<storedEnergy.length; i++)
			values.add( new TimePeriodValue(periods[i], storedEnergy[i]));
	    TimePeriodValuesCollection storedEnergyDataset = new TimePeriodValuesCollection();
	    storedEnergyDataset.addSeries(values);
	    
	    
	    final NumberAxis chargeNDischargeRangeAxis= new NumberAxis("Chrg/Dischg(kWh)");
	    final ValueAxis storedEnergyRangeAxis = new NumberAxis("Stored Energy (kWh)");
	    
	    
	    chargeNDischargeRangeAxis.setRange(
	    		- Math.max(storage.getMaxDischarge(), -CommonMethod.min(evConsumption)),
	    		Math.max(storage.getMaxCharge(), CommonMethod.max(evConsumption)));
	    
	    storedEnergyRangeAxis.setRange(0, storage.getCapacity());
	    
	    
	    final XYPlot plot =  new XYPlot(chargeDataset, null, chargeNDischargeRangeAxis, chargeBarRenderer);
	    plot.mapDatasetToRangeAxis(1, 1);	    
	    plot.setRangeAxis(1, storedEnergyRangeAxis);
	    plot.setDataset(1, storedEnergyDataset);
	    plot.setRenderer(1, lineRenderer);
	    
	    plot.setDataset(2, dischargeDataset);
	    plot.setRenderer(2, dischargeBarRenderer);
		
	    if(storage instanceof IMobileStorage)
	    {	
	    	plot.setDataset(3, evDataset);
	    	plot.setRenderer(3, evUsageBarRenderer);
	    }
		
	    plot.setForegroundAlpha(0.7f);
		
		plot.setDomainAxis(timeDomainAxis);
		
		String stats = "Battery Stats [sum(charge)="+CommonMethod.truncatedSum(charge,2)+", sum(discharge)="+CommonMethod.truncatedSum(discharge,2)
				+". min(stored)="+CommonMethod.truncate(CommonMethod.min(storedEnergy),2)+", max(stored)="+CommonMethod.truncate(CommonMethod.max(storedEnergy),2)+"]";

		final JFreeChart chart = new JFreeChart(
				stats, new Font("SansSerif", Font.BOLD, 12),
				plot, true);

		return new ChartPanel(chart);


	}
	
	public ChartPanel createBatteryWasteLinkGenerationChart(double[] charge, double[] discharge, double[] batteryCharge, double[] waste, double[] link)
	{
		
		final XYBarRenderer chargeBarRenderer = new XYBarRenderer();
		chargeBarRenderer.setSeriesPaint(0, Color.red);
		//chargeBarRenderer.setShadowVisible(false);
		
		final XYBarRenderer dischargeBarRenderer = new XYBarRenderer();
		dischargeBarRenderer.setSeriesPaint(0, Color.red);
		//dischargeBarRenderer.setShadowVisible(false);
		
		final XYLineAndShapeRenderer lineRenderer = new XYLineAndShapeRenderer();
		lineRenderer.setSeriesPaint(0, Color.blue);

		TimePeriodValues values = new TimePeriodValues("Charge");
		for(int i=0; i<charge.length; i++)
			values.add( new TimePeriodValue(periods[i], charge[i]));
	    TimePeriodValuesCollection chargeDataset = new TimePeriodValuesCollection();
	    chargeDataset.addSeries(values);
		
		values = new TimePeriodValues("Discharge");
		for(int i=0; i<discharge.length; i++)
			values.add( new TimePeriodValue(periods[i], discharge[i]));
	    TimePeriodValuesCollection dischargeDataset = new TimePeriodValuesCollection();
	    dischargeDataset.addSeries(values);
	    
	    values = new TimePeriodValues("Battery Charge");
		for(int i=0; i<batteryCharge.length; i++)
			values.add( new TimePeriodValue(periods[i], batteryCharge[i]));
	    TimePeriodValuesCollection batteryChargeDataset = new TimePeriodValuesCollection();
	    batteryChargeDataset.addSeries(values);
	    
	    
	    final NumberAxis chargeNDischargeRangeAxis= new NumberAxis("Chrg/Dischg(kWh)");
	    final ValueAxis batteryChargeRangeAxis = new NumberAxis("Stored Energy (kWh)");
	    chargeNDischargeRangeAxis.setRange(-Config.maxDischarge, Config.maxCharge);
	    batteryChargeRangeAxis.setRange(0, Config.maxCapacity);
	    
	    
	    final XYPlot chargeNDischargePlot =  new XYPlot(chargeDataset, null, chargeNDischargeRangeAxis, chargeBarRenderer);
	    chargeNDischargePlot.mapDatasetToRangeAxis(1, 1);	    
	    chargeNDischargePlot.setRangeAxis(1, batteryChargeRangeAxis);
	    chargeNDischargePlot.setDataset(1, batteryChargeDataset);
	    chargeNDischargePlot.setRenderer(1, lineRenderer);
	    chargeNDischargePlot.setDataset(2, dischargeDataset);
	    chargeNDischargePlot.setRenderer(2, dischargeBarRenderer);
		chargeNDischargePlot.setForegroundAlpha(0.7f);
		
		
		final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(timeDomainAxis);
		
		plot.add(chargeNDischargePlot, 2);
		//plot.add(createDefaultXYPlot("Genr.(kWh)", generation), 1);
		plot.add(createDefaultXYPlot("Waste(kWh)", waste), 1);
		
		XYPlot linkPlot =createDefaultXYPlot("Link(kWh)", link);
		linkPlot.getRangeAxis().setRange(-Config.maxLink, Config.maxLink);
		plot.add(linkPlot, 1);
		
		plot.setDomainAxis(timeDomainAxis);
		
		String title = "Battery Stats [sum(charge)="+CommonMethod.truncatedSum(charge,2)+", sum(discharge)="+CommonMethod.truncatedSum(discharge,2)
				+". min(stored)="+CommonMethod.truncate(CommonMethod.min(batteryCharge),2)+", max(stored)="+CommonMethod.truncate(CommonMethod.max(batteryCharge),2)+"]";

		final JFreeChart chart = new JFreeChart(
				title, new Font("SansSerif", Font.BOLD, 12),
				plot, false);

		return new ChartPanel(chart);


	}
	
	public ChartPanel createGenerationChart(OptResult info)
	{

		TimeTableXYDataset dataset = new TimeTableXYDataset();
		HashMap<IGenerator, double[]> generatorUsageMap = info.getGeneratorUsageMap();
		
		for(int t=0;t<timeslots; t++)
		{
			for(IGenerator generator: generatorUsageMap.keySet())
			{	
				dataset.add(periods[t], generatorUsageMap.get(generator)[t], generator.getName());
			}
			
		}
		
		XYBarRenderer renderer = new StackedXYBarRenderer();
		
		XYPlot plot = new XYPlot(dataset,
		            timeDomainAxis,
		            new NumberAxis("Consumption (kWh)"), 
		            renderer);

		//To generate the title
		String title = "Generators Usage :";
		double totalUsage = 0;
		
		for(IGenerator generator: generatorUsageMap.keySet())
		{
			double usage = CommonMethod.sum(generatorUsageMap.get(generator));
			title+= " "+generator.getName()+"="+CommonMethod.truncate(usage, 2);
			totalUsage+=usage;
		}
		title+= " (Total="+CommonMethod.truncate(totalUsage, 2)+" kWh)";
		
		
		JFreeChart chart = new JFreeChart(title, plot);
		
		chart.setBackgroundPaint(Color.WHITE);
		//chart.setMouseZoomable(true, false);

		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setMouseZoomable(true, false);
		
		return chartPanel;
	}
	
	public ChartPanel createLoadExcecutionChart(OptResult info)
	{

		MyAgent agent = info.getAgent();
		TimeTableXYDataset dataset = new TimeTableXYDataset();
		
		ArrayList<AbstractLoadEvent> orderOfEventsForColoring = new ArrayList<>();
		
		ArrayList<AbstractLoadEvent> allLoads = new ArrayList<AbstractLoadEvent>();
		allLoads.addAll(agent.getDeferrableEvents());
		allLoads.addAll(agent.getNonDeferrableEvents());
		//We want to take out thermal events to represent them in a simple way with the same color
		//much like the way we present baseload
		ArrayList<NonDeferrableLoadEvent> thermalEvents = agent.getHeatingLoadEvents();
		allLoads.removeAll(thermalEvents);
		
		double[] thermal = new double[Config.timeslots];
		for (int i = 0; i < thermalEvents.size(); i++) {
			NonDeferrableLoadEvent event = thermalEvents.get(i);
			
			//We don't want unsatisfied thermal loads to show up on this chart
			if(info.getSatisfiedLoadPowerMapping().containsKey(event))
				thermal[event.getStartTimeslot()] = info.getSatisfiedLoadPowerMapping().get(event)[i];
		}
		
		double[] base = info.getBaseLoad();     			
    	
		boolean isBaseLoadPresent = false;
		boolean isThermalLoadPresent = false;
		
		if (base!=null)
			isBaseLoadPresent =  CommonMethod.sum(base)>0.01?true:false;
		if(thermal!=null)
			isThermalLoadPresent = CommonMethod.sum(thermal)>0.01?true:false;
		
		
		for(int i=0;i<timeslots; i++)
		{			
			if(isBaseLoadPresent)
				dataset.add( periods[i], base[i], "BaseLoad");
			if(isThermalLoadPresent)
				dataset.add( periods[i], thermal[i], "ThermalLoad");
			
			for (AbstractLoadEvent event: allLoads)
			{
				//We don't want unsatisfied loads to show up on this chart
				if(!info.getSatisfiedLoadPowerMapping().containsKey(event))
					continue;
				
				//We want all instances of the same event to be colored the same
				if (!orderOfEventsForColoring.contains(event))
					orderOfEventsForColoring.add(event);
				
				//Add this series
				double v = info.getSatisfiedLoadPowerMapping().get(event)[i];
				dataset.add(periods[i], v, event.getName());
				
			}

		}

		
		XYBarRenderer renderer = new StackedXYBarRenderer();
		//Remember we have baseLoad + otherLoad + thermalLoad + allSatisfied loads
		
		int seriesIndex = 0;
		
		if(isBaseLoadPresent)
			renderer.setSeriesPaint(seriesIndex++, baseLoadColor);
		if(isThermalLoadPresent)
			renderer.setSeriesPaint(seriesIndex++, thermalLoadColor);
		
		Color color = null;
		for(int i=0; i<orderOfEventsForColoring.size(); i++)
		{	
			AbstractLoadEvent event = orderOfEventsForColoring.get(i);
			color = colorChoice[(i)%colorChoice.length];
			loadColorMap.put(event, color);
			renderer.setSeriesPaint(seriesIndex++, color);						
		}
				
		
		XYPlot plot = new XYPlot(dataset,
		            timeDomainAxis,
		            new NumberAxis("Consumption (kWh)"), 
		            renderer);

		String title = "Optimal Consumption (Util="+CommonMethod.truncate(info.getObjValue(), 2)+")";
		JFreeChart chart = new JFreeChart(title, plot);
		
		chart.setBackgroundPaint(Color.WHITE);
		//chart.setMouseZoomable(true, false);

		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setMouseZoomable(true, false);
		
		return chartPanel;
	}
	
	public ChartPanel createPreferencesCharts(OptResult info, ArrayList<AbstractNonDeferrableEvent> nonDeferrableEvents,
			ArrayList<AbstractDeferrableEvent> deferrableEvents) {  

		//We don't want the thermal loads to be here so let's exclude them
		ArrayList<AbstractLoadEvent> allLoads = new ArrayList<AbstractLoadEvent>();
		allLoads.addAll(deferrableEvents);
		allLoads.addAll(nonDeferrableEvents);
		
		ArrayList<AbstractLoadEvent> thermalEvents = new ArrayList<AbstractLoadEvent>();
		for(int i=0; i<allLoads.size(); i++)
			if(allLoads.get(i).getAppliance() instanceof ElectricHeater)
				thermalEvents.add(allLoads.get(i));		
		
		allLoads.removeAll(thermalEvents);
		
		int totalEvents = allLoads.size();
		
		final XYBarRenderer[] barRenderers = new XYBarRenderer[totalEvents];
		
		for(int i=0; i<totalEvents; i++)
		{
			barRenderers[i] = new XYBarRenderer();
			barRenderers[i].setDrawBarOutline(true);
			//barRenderers[i].setShadowVisible(false);
		}
				
		  
		NumberAxis numberAxis = createDefaultNumberAxis("Pref.");
		final XYPlot[] plots= new XYPlot[totalEvents]; 		
		
		
		int index=0;
		for (AbstractLoadEvent event: allLoads)
		{	 
			TimePeriodValues values = new TimePeriodValues(event.getName());
			for(int i=0; i<event.getPreferenceVector(timeslots).length; i++)
				values.add( new TimePeriodValue(periods[i], event.getPreferenceVector(timeslots)[i]));
	        
		    TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
		    dataset.addSeries(values);
		    
			Color color = (loadColorMap.containsKey(event)?loadColorMap.get(event):unsatisfiedEventsColor);
			barRenderers[index].setSeriesPaint(0, color);
			plots[index] =  new XYPlot(dataset, null, numberAxis, barRenderers[index]);
			index++;
		}

		CombinedDomainXYPlot plot = new CombinedDomainXYPlot(timeDomainAxis);	
		
		for(XYPlot p: plots)
			plot.add(p);

		final JFreeChart chart = new JFreeChart("Preferences", new Font("SansSerif", Font.BOLD, 12),
				plot, false);

		return new ChartPanel(chart);

	}
	
}