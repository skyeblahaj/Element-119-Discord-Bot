package discordbot.utils.function;

@FunctionalInterface
public interface TriCarrier<C> {

	public C carry(C a, C b, C c);
	
}