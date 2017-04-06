package ptwop.network.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import ptwop.network.NAddress;
import ptwop.network.NPair;
import ptwop.network.NPairHandler;

public class TcpNPair implements NPair, Runnable {
	private Socket socket;
	private DataOutputStream out;
	private DataInputStream in;
	NPairHandler handler;

	private Thread runner;
	private boolean run;

	private int pairListeningPort;

	private long lastPingStart; // in ms
	private int latency; // in ms
	private int pingDelay = 5000; // // in ms, 5s
	private int pingValue;
	private boolean initierPing;
	private Timer pingTimer;
	
	private static enum MessageType {
		PING, DATA;

		private byte b = 0;

		MessageType() {
			if ((b = (byte) this.ordinal()) > Byte.MAX_VALUE)
				throw new RuntimeException("Can't create more than " + Byte.MAX_VALUE + " ByteEnum");
		}

		public byte value() {
			return b;
		}
	}

	public TcpNPair(int listeningPort, Socket socket, NPairHandler handler, boolean incomming) throws IOException {
		this.socket = socket;
		this.handler = handler;
		this.initierPing = !incomming;
		out = new DataOutputStream(socket.getOutputStream());
		in = new DataInputStream(socket.getInputStream());

		// sharing listening port
		out.writeInt(new Integer(listeningPort));
		pairListeningPort = in.readInt();
		System.out.println("Pair listening port : " + pairListeningPort);

		latency = 0;
		lastPingStart = System.currentTimeMillis();
		Random c = new Random();
		pingValue = c.nextInt();

		runner = new Thread(this);
		runner.setName("TcpNetworkUser runner " + getAddress().toString());
	}

	// Ping

	private void sendPing() throws IOException {
		sendPing(pingValue);
	}

	private synchronized void sendPing(int code) throws IOException {
		out.writeByte(MessageType.PING.value());
		out.writeInt(code);
	}

	private void handlePingMessage(int code) {
		try {
			if (initierPing) {
				if (code == pingValue) {
					pingValue++;
					latency = (int) (System.currentTimeMillis() - lastPingStart);
					sendPing(code);

				}
			} else {
				if (pingValue != code) {
					pingValue = code;
					sendPing(code);
					lastPingStart = System.currentTimeMillis();
				} else {
					latency = (int) (System.currentTimeMillis() - lastPingStart);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// NPair

	@Override
	public void start() {
		// Timer Ping
		if (initierPing) {
			pingTimer = new Timer(true);
			pingTimer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					try {
						sendPing();
						lastPingStart = System.currentTimeMillis();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}, pingDelay, pingDelay);
		}
		runner.start();
	}

	@Override
	public synchronized void send(byte[] bytes) throws IOException {
		out.writeByte(MessageType.DATA.value());
		out.writeInt(bytes.length);
		out.write(bytes);
	}

	@Override
	public void disconnect() {
		run = false;
		if (pingTimer != null)
			pingTimer.cancel();
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public NAddress getAddress() {
		return new TcpNAddress(socket.getInetAddress(), pairListeningPort);
	}

	@Override
	public void run() {
		run = true;
		while (run) {
			try {
				byte messageType = in.readByte();
				if (messageType == MessageType.DATA.value()) {
					int length = in.readInt();
					byte[] bytes = new byte[length];
					handler.incommingMessage(this, bytes);
				} else if (messageType == MessageType.PING.value()) {
					int code = in.readInt();
					handlePingMessage(code);
				}
			} catch (IOException e) {
				disconnect();
			}
		}
		handler.pairQuit(this);
	}

	@Override
	public String toString() {
		return getAddress().toString();
	}

	@Override
	public int hashCode() {
		return getAddress().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof TcpNPair) && ((TcpNPair) o).getAddress().equals(getAddress());
	}

	@Override
	public int getLatency() {
		return latency;
	}
}
