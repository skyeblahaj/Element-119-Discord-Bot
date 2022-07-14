package discordbot.utils.function;

@FunctionalInterface
public interface BiCarrier<B> {

	public B carry(B in1, B in2);
	
}