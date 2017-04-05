package ptwop.network.tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import ptwop.network.NAddress;
import ptwop.network.NPair;
import ptwop.network.NPairHandler;

public class TcpNPair implements NPair, Runnable {
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
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

	public TcpNPair(int listeningPort, Socket socket, NPairHandler handler, boolean incomming) throws IOException {
		this.socket = socket;
		this.handler = handler;
		this.initierPing = !incomming;
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());

		// sharing listening port
		out.writeObject(new Integer(listeningPort));
		try {
			pairListeningPort = (Integer) in.readObject();
			System.out.println("Pair listening port : " + pairListeningPort);

			latency = 0;
			lastPingStart = System.currentTimeMillis();
			Random c = new Random();
			pingValue = c.nextInt();

			runner = new Thread(this);
			runner.setName("TcpNetworkUser runner " + getAddress().toString());
		} catch (ClassNotFoundException e) {
			socket.close();
			handler.pairQuit(this);
			throw new IOException("Cant get pair's listening port");
		}
	}

	@Override
	public void start() {
		// Timer Ping
		if (initierPing) {
			pingTimer = new Timer(true);
			pingTimer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					try {
						send(generatePing());
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
	public synchronized void send(Object o) throws IOException {
		out.writeObject(o);
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
				Object o = in.readObject();
				if (o instanceof Ping) {
					Ping p = (Ping) o;
					if (initierPing) {
						if (checkPing(p)) {
							latency = (int) (System.currentTimeMillis() - lastPingStart);
							send(p);
						}
					} else {
						if (pingValue != p.x) {
							pingValue = p.x;
							send(p);
							lastPingStart = System.currentTimeMillis();
						} else {
							latency = (int) (System.currentTimeMillis() - lastPingStart);
						}
					}
				} else {
					handler.incommingMessage(this, o);
				}
			} catch (IOException e) {
				disconnect();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
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

	// PING
	private Ping generatePing() {
		return new Ping(pingValue);
	}

	private boolean checkPing(Ping p) {
		if (p.x == pingValue) {
			pingValue++;
			return true;
		}
		return false;
	}
}
