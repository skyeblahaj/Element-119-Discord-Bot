package discordbot.utils.function;

@FunctionalInterface
public interface SameThreadRunnable { //just the runnable interface, but without its additional thread creation. using this to stop the code until the process is finished. if the process doesnt finish the thread will time out. i dont feel the need to contribute using CompletableFuture<>

	public void run();
	
}