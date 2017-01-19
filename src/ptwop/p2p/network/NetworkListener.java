package ptwop.p2p.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkListener {
	ServerSocket listener;
	Thread runner;

	boolean stop;

	public NetworkListener(int port, SocketHandler handler) {
		try {
			listener = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("NewtorkListener error \"" + e.getMessage() + "\"");
			return;
		}

		runner = new Thread() {
			public void run() {
				System.out.println("Listener running...");
				while (!stop) {
					try {
						Socket newSocket = listener.accept();
						System.out.println("Listener : new socket on " + newSocket.getInetAddress() + " "
								+ newSocket.getLocalPort() + " " + newSocket.getPort());
						handler.handleSocket(newSocket);
					} catch (IOException e) {
						stop = true;
					}
				}
				System.out.println("Listener stopped");
			}
		};
	}

	public void start() {
		if (runner != null) {
			stop = false;
			runner.start();
		}
	}

	public void close() {
		if (runner != null) {
			try {
				listener.close();
				stop = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Test function : launch a listener handling new sockets on 123 port
	 * Connection are created and reply when a message is received
	 */
	public static void main(String[] args) {
		NetworkListener listener = new NetworkListener(123, new SocketHandler() {
			@Override
			public void handleSocket(Socket socket) {
				try {
					Connection c = new Connection(socket, new ConnectionHandler() {
						@Override
						public void handleMessage(Connection connection, Object o) {
							System.out.println("Listener side : New message");
							if (o instanceof String) {
								System.out.println(((String) o));
								try {
									System.out.println("Listener side : replying...");
									connection.send(new String("réponse de la part du serveur"));
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}

						@Override
						public void connectionClosed(Connection connection) {
							System.out.println("Listener side : Closed");
						}
					});
					c.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		listener.start();
	}
}
