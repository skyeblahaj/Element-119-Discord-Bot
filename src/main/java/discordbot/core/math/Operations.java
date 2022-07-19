package discordbot.core.math;

import discordbot.utils.function.BiCarrier;
import discordbot.utils.function.Carrier;
import discordbot.utils.function.TriCarrier;
import discordbot.utils.function.TriFunction;

public enum Operations {

	ADD(2), SUBTRACT(2), MULTIPLY(2), DIVIDE(2), POWER(2), LOG(2), OTHER(0, true), QUADRATIC(3), CHEMISTRY(2, 1), HYPOTENUSE(2);
	
	private int paramsReq;
	private final int extraParams;
	private final boolean canChange;
	
	Operations(int req) {
		this.paramsReq = req;
		this.canChange = false;
		this.extraParams = 0;
	}
	
	Operations(int req, boolean override) {
		this.paramsReq = req;
		this.canChange = override;
		this.extraParams = 0;
	}
	
	Operations(int req, int ext) {
		this.canChange = false;
		this.paramsReq = req;
		this.extraParams = ext;
	}
	
	public void setParametersNeeded(int i) {
		if (this.canChange) this.paramsReq = i;
		else throw new IllegalArgumentException("Can't change.");
	}
	
	public int paramsNeeded() {
		return this.paramsReq;
	}
	
	public int extraParamsNeeded() {
		return this.extraParams;
	}
	
	public static final Carrier<Double> FAHRENHEIT_TO_CELSIUS = in -> {
		return (in - 32) * (5.0 / 9.0);
	};
	
	public static final Carrier<Double> CELSIUS_TO_FAHRENHEIT = in -> {
		return in * 1.8 + 32;
	};
	
	public static final Carrier<Double> CELSIUS_TO_KELVIN = in -> {
		return in + 273.15;
	};
	
	public static final Carrier<Double> KELVIN_TO_CELSIUS = in -> {
		return in - 273.15;
	};
	
	public static final Carrier<Double> FAHRENHEIT_TO_KELVIN = in -> {
		return CELSIUS_TO_KELVIN.carry(FAHRENHEIT_TO_CELSIUS.carry(in));
	};
	
	public static final Carrier<Double> KELVIN_TO_FAHRENHEIT = in -> {
		return CELSIUS_TO_FAHRENHEIT.carry(KELVIN_TO_CELSIUS.carry(in));
	};
	
	public static final BiCarrier<Double> EXPONENTIAL = (in1, in2) -> {
		return Math.pow(in1, in2);
	};
	
	public static final BiCarrier<Double> BASE_LOG = (in1, in2) -> {
		return Math.log(in1) / Math.log(in2);
	};

	private static final TriCarrier<Double> QUAD_POS = (a, b, c) -> {
		return (-b + (Math.sqrt((b * b) - (4 * a * c)))) / 2 * a;
	};
	
	private static final TriCarrier<Double> QUAD_NEG = (a, b, c) -> {
		return (-b - (Math.sqrt((b * b) - (4 * a * c)))) / 2 * a;
	};
	
	public static final TriFunction<double[], Double, Double, Double> QUADRATICS = (a, b, c) -> {
		double x1 = QUAD_POS.carry(a, b, c);
		double x2 = QUAD_NEG.carry(a, b, c);
		return new double[] {x1, x2};
	};
	
	public static final BiCarrier<Double> HYPOT = (x, y) -> {
		return Math.hypot(x, y);
	};
	
}