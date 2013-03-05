package agent;

public abstract class AbstractSocialAgent extends AbstractAgent implements ISocialAgent{
	
	protected double maxLink;
	protected boolean isConnected = false;	
	
	public AbstractSocialAgent(String id) {super(id);}
	
	public void setMaxLink(double maxLink){this.maxLink =maxLink;}
	public double getMaxLink()	{return maxLink;}
	public boolean isConnected() {return isConnected;}
	public void setConnected(boolean linkStatus){this.isConnected = linkStatus;}
	
}
