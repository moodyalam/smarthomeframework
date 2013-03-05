package dataloader;

import java.util.Arrays;

import loadevents.AbstractOtherLoads;
import models.BaseLoad;
import util.Config;

public class DummyDataFactory {

	private static final int timeslots = Config.timeslots;
	private static final double[] fixedBaseLoad = new double[]{0.3147696324413853, 0.29726200547326875, 0.27351539442805045, 0.28963766496431353, 0.3063164426021458, 0.2098440324678469, 0.20593513611245257, 0.22397200988737584, 0.3457162062129131, 0.3097596034001816, 0.3886299406680671, 0.3422363838901429, 0.31212411653524547, 0.27844245794240596, 0.2854214824563088, 0.2782452478372851, 0.3661188127730983, 0.3326285825970203, 0.22928836994533658, 0.2796062784074224, 0.2346355923415215, 0.20881165159263604, 0.33363611513869457, 0.3796553184466609, 0.3550931913937587, 0.29867218918970284, 0.34848151710706154, 0.3663641621069756, 0.35215781408281516, 0.2626837837870027, 0.2585302548543913, 0.2966159808476, 0.31922078359376455, 0.20554506157758315, 0.3468842453430401, 0.3138134394853166, 0.2815490352359494, 0.20978140101821838, 0.3811231558320933, 0.39260493363374255, 0.22920053971934187, 0.2928057148831429, 0.317762258460642, 0.3565846447723878, 0.22517945732297318, 0.27792288323815034, 0.26659665671928123, 0.29314245054853344};
	
	
	public static AbstractOtherLoads createBaseLoad()
	{
		double[] load = new double[timeslots];
		for (int i=0; i<load.length; i++)
			//load[i]= 0.2+(0.2*Math.random());
			load[i] = fixedBaseLoad[i];
		double[] pref = new double[timeslots];
		Arrays.fill(pref, 1);
		return (AbstractOtherLoads) new BaseLoad(load, pref, true);
	}
	
	public static BaseLoad createSimpleLoad()
	{
		double[] load = new double[timeslots];
		for (int i=0; i<load.length; i++)
			//load[i]= 0.2+(0.2*Math.random());
			load[i] = fixedBaseLoad[i];
		double[] pref = new double[timeslots];
		Arrays.fill(pref, 1);
		return new BaseLoad(load, pref, false);
	}
	
	
	
	
}
