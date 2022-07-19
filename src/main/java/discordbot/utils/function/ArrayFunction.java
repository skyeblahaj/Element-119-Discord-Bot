package discordbot.utils.function;

public interface ArrayFunction<A, B> {

	@SuppressWarnings("unchecked")
	public A apply(B... in);
	
}