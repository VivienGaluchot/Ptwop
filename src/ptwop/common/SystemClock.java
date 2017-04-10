package ptwop.common;

public class SystemClock implements Clock {
	@Override
	public long getTime() {
		return System.currentTimeMillis();
	}
}
