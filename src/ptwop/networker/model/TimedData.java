package ptwop.networker.model;

/**
 * Class used to attach timeStamp along the data *
 */
public class TimedData {
	public long inTime;
	public long outTime;
	public Data data;

	// used for displaying messages
	public int slide;

	public TimedData(long inTime, long outTime, Data data, int slide) {
		this.inTime = inTime;
		this.outTime = outTime;
		this.data = data;
		this.slide = slide;
	}

	@Override
	public String toString() {
		return data.toString();
	}
}
