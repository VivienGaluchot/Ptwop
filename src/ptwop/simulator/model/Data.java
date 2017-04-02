package ptwop.simulator.model;

import ptwop.common.Util;
import ptwop.p2p.base.MessageToApp;

public class Data {
	public Object object;

	private long creationTime;
	private int size;

	public int part;
	public int nPart;

	private Data() {

	}

	public Data(Object object, long creationTime, int transmissionUnit) {
		this.object = object;
		
		if (isBenchmarkData()) {
			BenchmarkData d = (BenchmarkData) ((MessageToApp) object).msg;
			size = d.size;
		} else {
			byte[] bytes = Util.serialize(object);
			int l = 0;
			if (bytes != null)
				l = bytes.length;
			size = l;
		}
		this.creationTime = creationTime;

		nPart = size / transmissionUnit;
		if (nPart * transmissionUnit < size)
			nPart++;

		this.part = 0;
	}

	public boolean isBenchmarkData() {
		return (object instanceof MessageToApp && ((MessageToApp) object).msg instanceof BenchmarkData);
	}

	public Data getPart(int i) {
		if (i >= nPart)
			throw new IllegalArgumentException("Part number should be in range [0;nPart[");

		Data partData = new Data();
		partData.object = object;
		partData.creationTime = creationTime;
		partData.size = size;
		partData.nPart = nPart;
		partData.part = i;
		return partData;
	}

	public long getEllapsedTime(long currentTime) {
		return currentTime - creationTime;
	}

	public int getSize() {
		return size;
	}

	public boolean isLastPart() {
		return part == nPart - 1;
	}

	@Override
	public String toString() {
		return object.toString() + " " + (part + 1) + "/" + nPart;
	}
}
