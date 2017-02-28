package ptwop.networker.model;

public class DataTCP{
	
	public enum Type {SYN, ACK, SYNACK};
	public Type t;

	public DataTCP(Type t) {
		this.t = t;
	}
	
	public boolean isSyn(){
		return (t).equals(Type.SYN);
	}
	
	public boolean isAck(){
		return (t).equals(Type.ACK);
	}
	
	public boolean isSynAck(){
		return (t).equals(Type.SYNACK);
	}
	
	public String toString(){
		return "DataTCP";
	}

}
