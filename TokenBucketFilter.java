import java.util.HashSet;
import java.util.Set;
/*
 * Imagine you have a bucket that gets filled with tokens at the rate of 1 token per second. 
 * The bucket can hold a maximum of N tokens. Implement a thread-safe class that lets threads
 *  get a token when one is available. If no token is available, then the token-requesting
 *   threads should block.

The class should expose an API called getToken that various threads can call to get
 a token
 */


public class TokenBucketFilter {
	private int MAX_TOKENS;
	// variable to note down the latest token request.
	private long lastRequestTime = System.currentTimeMillis();
	long possibleTokens = 0;

	public TokenBucketFilter(int maxTokens) {
		MAX_TOKENS = maxTokens;
	}

	synchronized void getToken() throws InterruptedException {
		// Divide by a 1000 to get granularity at the second level.
		possibleTokens += (System.currentTimeMillis() - lastRequestTime) / 1000;

		if (possibleTokens > MAX_TOKENS) {
			possibleTokens = MAX_TOKENS;
		}
		if (possibleTokens == 0) {
			Thread.sleep(1000);
		} else {
			possibleTokens--;
		}
		lastRequestTime = System.currentTimeMillis();
		System.out.println(
				"Granting " + Thread.currentThread().getName() + " token at " + (System.currentTimeMillis() / 1000));
	}
	public static void runTestMaxTokenIs1() throws InterruptedException {

        Set<Thread> allThreads = new HashSet<Thread>();
        final TokenBucketFilter tokenBucketFilter = new TokenBucketFilter(1);

        for (int i = 0; i < 10; i++) {

            Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        tokenBucketFilter.getToken();
                    } catch (InterruptedException ie) {
                        System.out.println("We have a problem");
                    }
                }
            });
            thread.setName("Thread_" + (i + 1));
            allThreads.add(thread);
        }

        for (Thread t : allThreads) {
            t.start();
        }

        for (Thread t : allThreads) {
            t.join();
        }
    }
	public static void runTestMaxTokenIsTen() throws InterruptedException {

        Set<Thread> allThreads = new HashSet<Thread>();
        final TokenBucketFilter tokenBucketFilter = new TokenBucketFilter(5);

        // Sleep for 10 seconds.
        Thread.sleep(10000);

        // Generate 12 threads requesting tokens almost all at once.
        for (int i = 0; i < 12; i++) {

            Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        tokenBucketFilter.getToken();
                    } catch (InterruptedException ie) {
                        System.out.println("We have a problem");
                    }
                }
            });
            thread.setName("Thread_" + (i + 1));
            allThreads.add(thread);
        }

        for (Thread t : allThreads) {
            t.start();
        }

        for (Thread t : allThreads) {
            t.join();
        }
    }
	public static void main(String[] args) throws InterruptedException {
		TokenBucketFilter.runTestMaxTokenIsTen();
	}

}
