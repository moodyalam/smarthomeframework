package visualiser;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import models.MyAgent;
import models.OptResult;

public class CompareOptInfo extends JFrame {

	public CompareOptInfo(String title, MyAgent[] agents, OptResult[] info) {
		super(title);		
		JPanel mainPanel = new JPanel();
		GridLayout layout = new GridLayout(1,agents.length);
		layout.setHgap(10);
		mainPanel.setLayout(layout);
		
		
		Container[] c = new Container[agents.length]; 
		
		for (int i=0; i<agents.length; i++)
		{
			c[i] = new OptInfoVisualiser(agents[i], info[i]).getPanel();
			mainPanel.add(c[i]);
		}
		
		setContentPane(mainPanel);
		setPreferredSize(new Dimension(400,400));
		setExtendedState(MAXIMIZED_BOTH);
		setVisible(true);
		
	}
	
	public CompareOptInfo(String title, MyAgent agent1, OptResult info1,  MyAgent agent2, OptResult info2) {
		this(title, new MyAgent[]{agent1, agent2}, new OptResult[]{info1, info2});
	}
	
	
}