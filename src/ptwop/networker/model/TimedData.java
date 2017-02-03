package ptwop.networker.model;

/**
 * Class used to attach timeStamp along the data *
 */
public class TimedData {
	public long inTime;
	public long outTime;
	public Data data;

	public TimedData(long inTime, long outTime, Data data) {
		this.inTime = inTime;
		this.outTime = outTime;
		this.data = data;
	}
}
