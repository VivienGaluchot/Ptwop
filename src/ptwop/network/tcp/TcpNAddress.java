package ptwop.network.tcp;

import java.net.InetAddress;

import ptwop.network.NAddress;

public class TcpNAddress extends NAddress {
	private static final long serialVersionUID = 1L;
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
		return ip.hashCode() + port;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof TcpNAddress) {
			TcpNAddress a = (TcpNAddress) o;
			return ip.equals(a.ip) && port == a.port;
		}
		return false;
	}
}
