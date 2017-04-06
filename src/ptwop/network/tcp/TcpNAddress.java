package ptwop.network.tcp;

import java.net.InetAddress;

import ptwop.network.NAddress;

public class TcpNAddress extends NAddress {
	public InetAddress ip;
	public int port;

	public TcpNAddress(InetAddress ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	@Override
	public String toString() {
		return ip + ":" + port;
	}

	@Override
	public int hashCode() {
		return ip.hashCode() * 7 - 3 * port;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof TcpNAddress) {
			TcpNAddress a = (TcpNAddress) o;
			return ip.equals(a.ip) && port == a.port;
		}
		return false;
	}


	@Override
	public int byteSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void serialize(int start, byte[] bytes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deserialize(byte[] bytes) {
		// TODO Auto-generated method stub
		
	}
}
