package discordbot.core.math;

import discordbot.utils.function.BiCarrier;
import discordbot.utils.function.Carrier;

public enum Operations {

	ADD(2), SUBTRACT(2), MULTIPLY(2), DIVIDE(2), POWER(2), LOG(2), OTHER(0, true);
	
	private int paramsReq;
	private final boolean canChange;
	
	Operations(int req) {
		this.paramsReq = req;
		this.canChange = false;
	}
	
	Operations(int req, boolean override) {
		this.paramsReq = req;
		this.canChange = override;
	}
	
	public void setParametersNeeded(int i) {
		if (this.canChange) this.paramsReq = i;
		else throw new IllegalArgumentException("Can't change.");
	}
	
	public int paramsNeeded() {
		return this.paramsReq;
	}
	
	public static final Carrier<Double> FAHRENHEIT_TO_CELSIUS = in -> {
		return (in - 32) * (5.0 / 9.0);
	};
	
	public static final Carrier<Double> CELSIUS_TO_FAHRENHEIT = in -> {
		return in * 1.8 + 32;
	};
	
	public static final BiCarrier<Double> EXPONENTIAL = (in1, in2) -> {
		return Math.pow(in1, in2);
	};
	
	public static final BiCarrier<Double> BASE_LOG = (in1, in2) -> {
		return Math.log(in1) / Math.log(in2);
	};
}