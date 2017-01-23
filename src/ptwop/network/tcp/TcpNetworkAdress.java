package ptwop.network.tcp;

import java.net.InetAddress;

import ptwop.network.NetworkAdress;

public class TcpNetworkAdress extends NetworkAdress {
	private static final long serialVersionUID = 1L;
	public InetAddress ip;
	public int port;

	@Override
	public String toString() {
		return ip + ":" + port;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof TcpNetworkAdress) {
			TcpNetworkAdress a = (TcpNetworkAdress) o;
			return ip.equals(a.ip) && port == a.port;
		}
		return false;
	}
}
