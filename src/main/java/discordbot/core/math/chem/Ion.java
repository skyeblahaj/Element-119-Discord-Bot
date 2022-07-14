package discordbot.core.math.chem;

public class Ion {

	private int[] ions;
	
	public Ion(int... ion) {
		this.ions = ion;
	}
	
	public Ion() {
		this.ions = null;
	}
	
	public int[] getIons() {
		return this.ions;
	}
	
}