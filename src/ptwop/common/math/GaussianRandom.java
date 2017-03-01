package ptwop.common.math;

import java.util.Random;

public class GaussianRandom {
	private Random r;
	private double min;
	private double max;
	private double moy;
	private double et;

	/**
	 * @param min
	 *            valeur minimale
	 * @param max
	 *            valeur maximale
	 * @param moy
	 *            moyenne
	 * @param et
	 *            equart type
	 */
	public GaussianRandom(double min, double max, double moy, double et) {
		this.min = min;
		this.max = max;
		this.moy = moy;
		this.et = et;
		r = new Random();
	}

	public double nextDouble() {
		double res = r.nextGaussian() * et + moy;
		res = Math.min(res, max);
		res = Math.max(res, min);
		return res;
	}

	public long nextLong() {
		return Math.round(nextDouble());
	}

	public float nextFloat() {
		return (float) nextDouble();
	}

	public int nextInt() {
		return (int) nextLong();
	}
}
