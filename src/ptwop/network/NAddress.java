package ptwop.network;

public abstract class NAddress{
	public abstract int byteSize();
	public abstract void serialize(int start, byte[] bytes);
	public abstract void deserialize(byte[] bytes);
}
