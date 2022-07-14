package discordbot.utils.function;

@FunctionalInterface
public interface Carrier<A> {

	public A carry(A in);
}