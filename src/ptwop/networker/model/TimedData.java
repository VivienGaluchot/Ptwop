package ptwop.networker.model;

/**
 * Class used to attach timeStamp along the data *
 */
public class TimedData {
	long outTime;
	Data data;

	public TimedData(long outTime, Data data) {
		this.outTime = outTime;
		this.data = data;
	}
}
