package ptwop.p2p;

import java.io.IOException;
import java.net.Socket;

import ptwop.p2p.network.Connection;
import ptwop.p2p.network.NetworkListener;
import ptwop.p2p.network.SocketHandler;
import ptwop.p2p.network.ConnectionHandler;

public class TestUnit {

	public static void main(String[] args) {
		launchNewtorkListener();
		launchClientSendMessageAndCloseOnReception();
	}

	private static void launchNewtorkListener() {
		NetworkListener listener = new NetworkListener(123, new SocketHandler() {
			@Override
			public void handleSocket(Socket socket) {
				System.out.println("Listener side : New Connection " + socket.getInetAddress());
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

	private static void launchClientSendMessageAndCloseOnReception() {
		try {
			Connection c = new Connection(new Socket("localhost", 123), new ConnectionHandler() {
				@Override
				public void handleMessage(Connection connection, Object o) {
					System.out.println("Client side : New message");
					if (o instanceof String) {
						System.out.println(((String) o));
					}

					connection.disconnect();
				}

				@Override
				public void connectionClosed(Connection connection) {
					System.out.println("Client side : Closed");
				}
			});
			c.start();
			System.out.println("Client side : connected");

			System.out.println("Client side : sending message...");
			c.send(new String("message du client"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
