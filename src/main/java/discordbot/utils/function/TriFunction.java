package discordbot.utils.function;

@FunctionalInterface
public interface TriFunction<A, B, C, D> {

	public A apply(B a, C b, D c);
	
}