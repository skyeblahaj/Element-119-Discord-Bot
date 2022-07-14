package discordbot.core.math.chem;

public enum Elements {

	HYDROGEN(1, 1, 1.01f, "H", true, new Ion(1,-1)), HELIUM(2, 4, 4, "He", true, new Ion()), LITHIUM(3, 7, 6.94f, "Li", true, new Ion(1)),
	BERYLLIUM(4, 8, 9.01f, "Be", true, new Ion(2)), BORON(5, 11, 10.81f, "B", true, new Ion()), CARBON(6, 12, 12.01f, "C", true, new Ion()),
	NITROGEN(7, 14, 14.01f, "N", true, new Ion(-3)), OXYGEN(8, 16, 16, "O", true, new Ion(-2)), FLUORINE(9, 19, 19, "F", true, new Ion(-1)),
	NEON(10, 20, 20.18f, "Ne", true, new Ion()), SODIUM(11, 23, 22.99f, "Na", true, new Ion(1)), MAGNESIUM(12, 24, 24.31f, "Mg", true, new Ion(2)),
	ALUMINUM(13, 27, 26.98f, "Al", true, new Ion(3)), SILICON(14, 28, 28.09f, "Si", true, new Ion(0)), PHOSPHORUS(15, 31, 30.97f, "P", true, new Ion(-3)),
	SULFUR(16, 32, 32.07f, "S", true, new Ion(-2)), CHLORINE(17, 35, 35.45f, "Cl", true, new Ion(-1)), ARGON(18, 40, 39.95f, "Ar", true, new Ion(0)),
	POTASSIUM(19, 39, 39.1f, "K", true, new Ion(1)), CALCIUM(20, 40, 40.08f, "Ca", true, new Ion(2)), SCANDIUM(21, 45, 44.96f, "Sc", true, new Ion(3)),
	TITANIUM(22, 48, 47.87f, "Ti", true, new Ion(3,4)), VANADIUM(23, 51, 50.94f, "V", true, new Ion(3,5)), CHROMIUM(24, 52, 52, "Cr", true, new Ion(2,3)),
	MANGANESE(25, 55, 54.94f, "Mn", true, new Ion(2,4)), IRON(26, 56, 55.85f, "Fe", true, new Ion(2,3)), COBALT(27, 59, 58.93f, "Co", true, new Ion(2,3)),
	NICKEL(28, 59, 58.69f, "Ni", true, new Ion(2,3)), COPPER(29, 64, 63.55f, "Cu", true, new Ion(1,2)), ZINC(30, 65, 65.38f, "Zn", true, new Ion(2)),
	GALLIUM(31, 70, 69.72f, "Ga", true, new Ion(3)), GERMANIUM(32, 73, 72.63f, "Ge", true, new Ion(4)), ARSENIC(33, 75, 74.92f, "As", true, new Ion(-3)),
	SELENIUM(34, 79, 78.97f, "Se", true, new Ion(-2)), BROMINE(35, 80, 79.9f, "Br", true, new Ion(-1)), KRYPTON(36, 84, 83.8f, "Kr", true, new Ion()),
	RUBIDIUM(37, 85, 85.47f, "Rb", true, new Ion(1)), STRONTINUM(38, 88, 87.62f, "Sr", true, new Ion(2)), YTTRIUM(39, 89, 88.9f, "Y", true, new Ion(3)),
	ZIRCONIUM(40, 91, 91.22f, "Zr", true, new Ion(4)), NIOBIUM(41, 93, 92.9f, "Nb", true, new Ion(3,5)), MOLYBDENUM(42, 96, 95.95f, "Mo", true, new Ion(6)),
	TECHNETIUM(43, 99, 98.9f, "Tc", false, new Ion(7)), RUTHENIUM(44, 101, 101.7f, "Ru", true, new Ion(3,4)),
	RHODIUM(45, 103, 102.91f, "Rh", true, new Ion(3)), PALLADIUM(46, 106, 106.42f, "Pd", true, new Ion(2,4)),
	SILVER(47, 108, 107.87f, "Ag", true, new Ion(1)), CADMIUM(48, 112, 112.41f, "Cd", true, new Ion(2)), INDIUM(49, 115, 114.82f, "In", true, new Ion(3)),
	TIN(50, 119, 118.71f, "Sn", true, new Ion(2,4)), ANTIMONY(51, 122, 121.76f, "Sb", true, new Ion(3,5)), TELLURIUM(52, 128, 127.6f, "Te", true, new Ion(-2)),
	IODINE(53, 127, 126.9f, "I", true, new Ion(-1)), XENON(54, 131, 131.29f, "Xe", true, new Ion()), CESIUM(55, 133, 132.9f, "Cs", true, new Ion(1)),
	BARIUM(56, 137, 137.33f, "Ba", true, new Ion(2)), LANTHANUM(57, 139, 138.91f, "La", true, new Ion(3)), CERIUM(58, 140, 140.12f, "Ce", true, new Ion(3)),
	PRASEODYMIUM(59, 141, 140.91f, "Pr", true, new Ion(3)), NEODYMIUM(60, 144, 144.24f, "Nd", true, new Ion(3)),
	PROMETHIUM(61, 145, 144.91f, "Pm", false, new Ion(3)), SAMARIUM(62, 150, 150.36f, "Sm", true, new Ion(2,3)),
	EUROPIUM(63, 152, 151.96f, "Eu", true, new Ion(2,3)), GADOLINIUM(64, 157, 157.25f, "Gd", true, new Ion(3)),
	TERBIUM(65, 159, 158.93f, "Tb", true, new Ion(3)), DYSPROSIUM(66, 163, 162.5f, "Dy", true, new Ion(3)),
	HOLMIUM(67, 165, 164.93f, "Ho", true, new Ion(3)), ERBIUM(68, 167, 167.26f, "Er", true, new Ion(3)), THULIUM(69, 169, 168.93f, "Tm", true, new Ion(3)),
	YTTERBIUM(70, 173, 173.06f, "Yb", true, new Ion(2,3)), LUTETIUM(71, 175, 174.97f, "Lu", true, new Ion(3)),
	HAFNIUM(72, 178, 178.49f, "Ha", true, new Ion(4)), TANTALUM(73, 181, 180.95f, "Ta", true, new Ion(5)),
	TUNGSTEN(74, 184, 183.84f, "W", true, new Ion(6)), RHENIUM(75, 186, 186.21f, "Re", true, new Ion(7)), OSMIUM(76, 190, 190.23f, "Os", true, new Ion(4)),
	IRIDIUM(77, 192, 192.22f, "Ir", true, new Ion(4)), PLATINUM(78, 195, 195.09f, "Pt", true, new Ion(2,4)), GOLD(79, 197, 196.97f, "Au", true, new Ion(1,3)),
	MERCURY(80, 201, 200.59f, "Hg", true, new Ion(2,2)), THALLIUM(81, 204, 204.38f, "Tl", true, new Ion(1,3)), LEAD(82, 207, 207.2f, "Pb", true, new Ion(2,4)),
	BISMUTH(83, 209, 208.98f, "Bi", false, new Ion(3,5)), POLONIUM(84, 209, 208.98f, "Po", false, new Ion(2,4)),
	ASTATINE(85, 210, 209.99f, "At", false, new Ion(-1)), RADON(86, 222, 222.02f, "Rn", false, new Ion()),
	FRANCIUM(87, 223, 223.02f, "Fr", false, new Ion(1)), RADIUM(88, 226, 226.03f, "Ra", false, new Ion(2)),
	ACTINIUM(89, 227, 227.03f, "Ac", false, new Ion(3)), THORIUM(90, 232, 232.04f, "Th", false, new Ion(4)),
	PROTACTINIUM(91, 231, 231.04f, "Pa", false, new Ion(4,5)), URANIUM(92, 238, 238.03f, "U", false, new Ion(4,6)),
	NEPTUNIUM(93, 237, 237.05f, "Np", false, new Ion(5)), PLUTONIUM(94, 244, 244.06f, "Pu", false, new Ion(4,6)),
	AMERICIUM(95, 243, 243.06f, "Am", false, new Ion(3,4)), CURIUM(96, 247, 247.07f, "Cm", false, new Ion(3)),
	BERKELIUM(97, 247, 247.07f, "Bk", false, new Ion(3,4)), CALIFORNIUM(98, 251, 251.08f, "Cf", false, new Ion(3,4)),
	EINSTEINIUM(99, 254, 254, "Es", false, new Ion(3)), FERMIUM(100, 257, 257.01f, "Fm", false, new Ion(3)),
	MENDELEVIUM(101, 258, 258.1f, "Md", false, new Ion(2,3)), NOBELLIUM(102, 259, 259.1f, "No", false, new Ion(2,3)),
	LAWRENCIUM(103, 262, 262, "Lr", false, new Ion(3)), RUTHERFORDIUM(104, 261, 261, "Rf", false, new Ion()),
	DUBNIUM(105, 262, 262, "Db", false, new Ion()), SEABORGIUM(106, 266, 266, "Sg", false, new Ion()), BOHRIUM(107, 264, 264, "Bh", false, new Ion()),
	HASSIUM(108, 269, 269, "Hs", false, new Ion()), MEITNERIUM(109, 278, 278, "Mt", false, new Ion()),
	DARMSTADTIUM(110, 281, 281, "Ds", false, new Ion()), ROENGENTIUM(111, 280, 280, "Rg", false, new Ion()),
	COPERNICIUM(112, 285, 285, "Cn", false, new Ion()), NIHONIUM(113, 286, 286, "Nh", false, new Ion()),
	FLEROVIUM(114, 289, 289, "Fl", false, new Ion()), MOSCOVIUM(115, 289, 289, "Mc", false, new Ion()),
	LIVERMORIUM(116, 293, 293, "Lv", false, new Ion()), TENNESSINE(117, 294, 294, "Ts", false, new Ion()),
	OGANESSON(118, 294, 294, "Og", false, new Ion());

	private final float molarMass;
	private final int atomicNum;
	private final int massNum;
	private final String symbol;
	private final boolean stable;
	private final Ion ion;

	Elements(int atomicNum, int massNum, float molarMass, String symbol, boolean stable, Ion ion) {
		this.molarMass = molarMass;
		this.atomicNum = atomicNum;
		this.massNum = massNum;
		this.symbol = symbol;
		this.stable = stable;
		this.ion = ion;
	}

	Elements() {
		this.molarMass = 0;
		this.atomicNum = 0;
		this.massNum = 0;
		this.symbol = "";
		this.stable = false;
		this.ion = null;
	}

	public float getMolarMass() {
		return this.molarMass;
	}

	public int getAtomicNum() {
		return this.atomicNum;
	}

	public int getMassNum() {
		return this.massNum;
	}

	public String getSymbol() {
		return this.symbol;
	}

	public boolean isStable() {
		return this.stable;
	}

	public Ion getIon() {
		return ion;
	}

	public int getElectrons(int ion) {
		for (int i : this.ion.getIons()) {
			if (ion == this.ion.getIons()[i]) return this.atomicNum - ion;
		}
		return 0;
	}
	
	public int getElectrons() {
		return this.atomicNum;
	}
	
	public int getNeutrons() {
		return this.massNum - this.atomicNum;
	}
	
	public Elements alphaDecay(int times) {
		int newAtomicNum = this.atomicNum - (2 * times);
		for (Elements e : Elements.values()) {
			if (newAtomicNum == e.getAtomicNum()) return e;
		}
		return null;
	}
	
	public Elements betaDecay(int times) {
		int newAtomicNum = this.atomicNum + times;
		for (Elements e : Elements.values()) {
			if (newAtomicNum == e.getAtomicNum()) return e;
		}
		return null;
	}
}
