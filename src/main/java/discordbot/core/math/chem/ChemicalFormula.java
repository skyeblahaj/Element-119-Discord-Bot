package discordbot.core.math.chem;

import discordbot.utils.Functions;

public class ChemicalFormula {

	private static final String REG_EXP_IONS = "/[A-Z][a-z]?\\d*|\\((?:[^()]*(?:\\(.*\\))?[^()]*)+\\)\\d+/g";
	private static final String REG_EXP_CHAR_AND_NUM = "/[a-z]+|[^a-z]+/gi";
	
	private final String formula;
	
	public ChemicalFormula(String formula) {
		this.formula = formula;
	}
	
	public double molarMass() { //OH BOY DO I JUST LOVE STRING PARSING!!!!!!
		double output = 0;
		
		String[] split = this.formula.split(REG_EXP_IONS);
		Functions.Debug.printArray(split);
		for (String s : split) {
			String[] char_num = s.split(REG_EXP_CHAR_AND_NUM);
			
			if (s.contains("(") && s.contains(")")) {
				String format = s.substring(1);
				int indexOf = format.lastIndexOf(")");
				String[] ionMult = {format.substring(0, indexOf), format.substring(indexOf) + 1};
				output += new ChemicalFormula(ionMult[0]).molarMass() * Integer.parseInt(ionMult[1]);
			} else {
				Elements element = null;
				for (Elements e : Elements.values()) {
					if (char_num[0].equals(e.getSymbol())) {
						element = e;
						break;
					}
				}
				if (char_num.length == 1) {
					output += element.getMolarMass();
				} else {
					output += element.getMolarMass() * Integer.parseInt(char_num[1]);
				}
			}
		}
		return output;
	}
}