package ptwop.networker.model;

public class Link implements Steppable{
	Network net;
	
	private long latency;
	private float loss;
	
	Node destNode;
	DataBuffer<TimedData> buffer;
	
	public Link(Network net, Node destNode, long latency, float loss, int packetSize) {
		this.net = net;
		this.destNode = destNode;
		this.latency = latency;
		this.loss = loss;
		buffer = new DataBuffer<>(packetSize);
	}

	public boolean isFull() {
		return buffer.isFull();
	}

	public boolean push(Data data) {
		TimedData tdata = new TimedData(net.getTime(), data);
		return buffer.push(tdata);
	}

	@Override
	public void doTimeStep() {
		// TODO push data to node
	}
	
	private class TimedData{
		long inTime;
		Data data;
		
		public TimedData(long inTime, Data data){
			this.inTime = inTime;
			this.data = data;
		}
	}
}
