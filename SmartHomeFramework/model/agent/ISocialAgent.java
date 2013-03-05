package agent;

public interface ISocialAgent {
	void setMaxLink(double maxLink);
	double getMaxLink();
	
	void setConnected(boolean linkStatus);
	boolean isConnected();
}
