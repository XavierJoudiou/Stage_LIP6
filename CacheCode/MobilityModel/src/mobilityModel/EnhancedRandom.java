package mobilityModel;

import java.util.Random;

/**
 * A class having the bounded "nextLong" method (Random only has nextInt bounded)
 * @author Sergey Legtchenko
 */
public class EnhancedRandom extends Random {

	private static final long serialVersionUID = 1L;

	private long seed;

	EnhancedRandom(){
		super();
	}

	public EnhancedRandom(long seed) {

		super(seed);
		this.seed = seed;
	}

	/**
	 * generates a uniformly distributed bounded random long value
	 * @param n the upper bound
	 * @return the random value
	 */
	public long nextLong(long n) {

		if (n<=0)
			throw new IllegalArgumentException("n must be positive");

		if ((n & -n) == n)  // i.e., n is a power of 2
		{	
			return nextLong()&(n-1);
		}

		long bits, val;
		do
		{
			bits = (nextLong()>>>1);
			val = bits % n;
		}
		while(bits - val + (n-1) < 0);

		return val;
	}

	public long getSeed() {
		return this.seed;
	}
}
