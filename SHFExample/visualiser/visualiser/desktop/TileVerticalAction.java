package visualiser.desktop;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

//TileAction.java
//An action that tiles all internal frames when requested.
class TileVerticalAction extends AbstractAction {
	  private JDesktopPane desk; // the desktop to work with

	  public TileVerticalAction(JDesktopPane desk) {
	    super("Tile Frames");
	    this.desk = desk;
	  }

	  public void actionPerformed(ActionEvent ev) 
		  {
			JInternalFrame[] frames = desk.getAllFrames();

			JInternalFrame f = null;

			int numVisibleFrames = 0;
			for (int i = frames.length - 1; i >= 0; i--)
				{
				f = frames[i];
				if (!f.isClosed() && !f.isIcon() && f.isVisible())
					{
					numVisibleFrames++;
					}
				}
			if (numVisibleFrames == 0)
				{
				return;
				}

			int frameW = desk.getWidth();
			int frameH = desk.getHeight() / numVisibleFrames; // new frame height
			int frameY = 0; // new frame position
			for (int i = frames.length - 1; i >= 0; i--)
				{
				f = frames[i];
				if (!f.isClosed() && !f.isIcon() && f.isVisible())
					{
					if (f.isResizable())
						{
						f.reshape(0, frameY, frameW, frameH);
						}
					else
						{
						f.setLocation(0, frameY);
						}
					frameY += frameH;
					}
				}
			
		  }
	}