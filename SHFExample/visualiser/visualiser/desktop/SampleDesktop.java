package visualiser.desktop;
/*
Java Swing, 2nd Edition
By Marc Loy, Robert Eckstein, Dave Wood, James Elliott, Brian Cole
ISBN: 0-596-00408-7
Publisher: O'Reilly 
 */

// SampleDesktop.java
//Another example that shows how to do a few interesting things using
//JInternalFrames, JDesktopPane, and DesktopManager.
//

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import loadevents.AbstractDeferrableEvent;
import loadevents.AbstractNonDeferrableEvent;
import models.MyAgent;
import models.OptResult;
import visualiser.VisualHelper;

public class SampleDesktop extends JFrame {

	private JDesktopPane desk;
	private OptResult optInfo;
	private MyAgent agent;
	
	HashMap<String, Component> agentChartsMap; 
	HashMap<String, Component> optChartsMap;
	private HashMap<String, JInternalFrame>  framesMap= new HashMap<String, JInternalFrame>();
	JButton verticalButton = new JButton("Vertical"); 
	JButton horizontalButton = new JButton("Horizontal");
	
	public SampleDesktop(String title, OptResult optInfo) {
		super(title);
		this.optInfo = optInfo;
		this.agent = optInfo.getAgent();

		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Create a desktop and set it as the content pane. Don't set the
		// layered pane, since it needs to hold the menu bar too.
		desk = new JDesktopPane();
		setContentPane(desk);

		// Install our custom desktop manager.
		desk.setDesktopManager(new SampleDesktopManager());

		initFrames();
		createMenuBar();
		loadBackgroundImage();
	}
	
	private void initFrames()
	{
		ArrayList<AbstractDeferrableEvent> deferrableEvents = agent.getDeferrableEvents();
		ArrayList<AbstractNonDeferrableEvent> nonDeferrableEvents = agent.getNonDeferrableEvents();
		VisualHelper visualHelper = new VisualHelper();

		agentChartsMap = visualHelper.getAgentRelatedCharts(optInfo); 
		optChartsMap = visualHelper.getOptimisationRelatedCharts(optInfo, nonDeferrableEvents, deferrableEvents);
		
		for(String s: agentChartsMap.keySet())
			framesMap.put(s, createInternalFrames(s, agentChartsMap.get(s)));
		
		for(String s: optChartsMap.keySet())
			framesMap.put(s, createInternalFrames(s, optChartsMap.get(s)));		
		
	}
	
	private JInternalFrame createInternalFrames(String title, Component chart)
	{
		JInternalFrame f = new JInternalFrame(title, true, true, true, true);
		f.add(chart);
		desk.add(f, 0);
		f.setBounds(0, 0, 600, 400);
		f.setDefaultCloseOperation(HIDE_ON_CLOSE);
		f.setVisible(true);
		return f;
	}

	// Create a menu bar to show off a few things.
	protected void createMenuBar() {

		JMenu agentMenu = new JMenu("Agent");
		JMenu optMenu = new JMenu("Optimisiation");
		
		for(String title: agentChartsMap.keySet())
		{
			JMenuItem item = new JMenuItem(title);
			item.addActionListener(new MenuActionHanlder());
			agentMenu.add(item);			
		}

		for(String title: optChartsMap.keySet())
			{
			JMenuItem item = new JMenuItem(title);
			item.addActionListener(new MenuActionHanlder());
			optMenu.add(item);
			}
		
		JMenuBar mb = new JMenuBar();
		setJMenuBar(mb);
		mb.add(agentMenu);
		mb.add(optMenu);
		

		verticalButton.addActionListener(new TileVerticalAction(desk)) ;
		horizontalButton.addActionListener(new TileHorizontalAction(desk)) ;
		
		mb.add(Box.createHorizontalGlue());
		mb.add(horizontalButton);
		mb.add(verticalButton);
		
		horizontalButton.getActionListeners()[0].actionPerformed(null);
		
	}
	
	private class MenuActionHanlder extends AbstractAction 
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			String title = ((JMenuItem)e.getSource()).getText();
			JInternalFrame frame =framesMap.get(title);  
			
			if(frame.isVisible()==true)
				{
					frame.moveToFront();
					return;
				}
			
			frame.setBounds(0, 0, 600, 400);
			frame.setVisible(true);
		}		
	}

	// Here we load a background image for our desktop.
	protected void loadBackgroundImage() {
		ImageIcon icon = new ImageIcon("images/matterhorn.gif");
		JLabel l = new JLabel(icon);
		l.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());

		// Place the image in the lowest possible layer so nothing
		// can ever be painted under it.
		desk.add(l, new Integer(Integer.MIN_VALUE));
	}
	
	
	
	
}





