package ptwop.simulator.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class DataTCP implements Serializable {
	private static final long serialVersionUID = 1L;

	public enum Type {
		SYN, ACK, SYNACK
	};

	public Type t;

	public DataTCP(Type t) {
		this.t = t;
	}

	public boolean isSyn() {
		return (t).equals(Type.SYN);
	}

	public boolean isAck() {
		return (t).equals(Type.ACK);
	}

	public boolean isSynAck() {
		return (t).equals(Type.SYNACK);
	}

	@Override
	public String toString() {
		return "DataTCP " + t;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		if(t == Type.SYN)
			out.writeByte(0);
		else if(t == Type.ACK)
			out.writeByte(1);
		else if(t == Type.SYNACK)
			out.writeByte(2);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		byte b = in.readByte();
		if(b == 0)
			t = Type.SYN;
		else if(b == 1)
			t = Type.ACK;
		else if(b == 2)
			t = Type.SYNACK;
	}
}
