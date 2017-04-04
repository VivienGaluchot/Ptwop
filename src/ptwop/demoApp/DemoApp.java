package ptwop.demoApp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import ptwop.common.gui.Dialog;
import ptwop.common.gui.Frame;
import ptwop.network.NServent;
import ptwop.network.tcp.TcpNAddress;
import ptwop.network.tcp.TcpNServent;
import ptwop.p2p.P2P;
import ptwop.p2p.P2PHandler;
import ptwop.p2p.P2PUser;
import ptwop.p2p.flood.FloodV1;
import ptwop.p2p.routing.StockasticRouter;

public class DemoApp {

	private P2P p2p;
	private JTextPane console;
	private JScrollPane scrollSole;
	private PrintStream stream;

	public DemoApp(int id) {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());

		p2p = null;
		stream = new PrintStream(new ConsoleOutputStream());

		// Fields
		JTextField listenPort = new JTextField(Integer.toString(919 + id), 4);
		JTextField name = new JTextField("Patrick " + id, 6);
		JTextField pairIp = new JTextField("127.0.0.1", 6);
		JTextField pairPort = new JTextField("919", 4);
		JTextPane message = new JTextPane();
		message.setFont(new Font("Consolas", Font.PLAIN, 12));
		message.setBackground(Color.black);
		message.setForeground(Color.white);
		message.setCaretColor(Color.lightGray);
		message.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (e.getKeyChar() == '\n') {
							String msg = message.getText();

							if (msg.endsWith("\n")) {
								msg = msg.substring(0, msg.length() - 1);
							}

							if (e.isShiftDown()) {
								message.setText(msg + '\n');
							} else {
								if (msg.startsWith("-ls")) {
									if (p2p != null) {
										stream.println("Utilisateurs du systeme");
										for (P2PUser u : p2p.getUsers()) {
											stream.println("- " + u.toString());
										}
									} else {
										stream.println("p2p not initialized");
									}
								} else if (msg.startsWith("-ping")) {
									stream.println("me : " + msg);
									if (p2p != null) {
										stream.println("Latence");
										for (P2PUser u : p2p.getUsers()) {
											stream.println("- " + u.toString() + " " + u.getLatency() + " ms");
										}
									} else {
										stream.println("p2p not initialized");
									}
								} else {
									stream.println("me : " + msg);
									if (p2p != null)
										p2p.broadcast(msg);
									else {
										stream.println("p2p not initialized");
									}
								}
								message.setText("");
							}
						}
					}
				});
			}
		});

		console = new JTextPane();
		console.setEditable(false);
		console.setFont(new Font("Consolas", Font.PLAIN, 12));
		console.setBackground(Color.black);
		console.setForeground(Color.white);
		console.setCaretColor(Color.lightGray);
		scrollSole = new JScrollPane(console);
		scrollSole.setMinimumSize(new Dimension(100, 100));
		scrollSole.setPreferredSize(new Dimension(300, 200));

		// Buttons
		JButton start = new JButton("Demarrer");
		JButton join = new JButton("Joindre");
		JButton disconnect = new JButton("Deconnexion");
		join.setEnabled(false);
		disconnect.setEnabled(false);

		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					NServent manager = new TcpNServent(Integer.parseInt(listenPort.getText()));
					p2p = new FloodV1(manager, name.getText(), new StockasticRouter());
					p2p.setMessageHandler(new Handler());
					p2p.start();
					listenPort.setEditable(false);
					name.setEditable(false);
					disconnect.setEnabled(true);
					join.setEnabled(true);
					start.setEnabled(false);
				} catch (NumberFormatException | IOException e) {
					Dialog.displayError(mainPanel, e.getMessage());
				}
			}
		});

		join.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					p2p.connectTo(new TcpNAddress(InetAddress.getByName(pairIp.getText()),
							Integer.parseInt(pairPort.getText())));
					disconnect.setEnabled(true);
				} catch (NumberFormatException | IOException e) {
					Dialog.displayError(mainPanel, e.getMessage());
				}
			}
		});

		disconnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (p2p != null)
					p2p.stop();
				listenPort.setEditable(true);
				name.setEditable(true);
				join.setEnabled(false);
				start.setEnabled(true);
				disconnect.setEnabled(false);
			}
		});

		// Layout

		JPanel subPanel = new JPanel();
		subPanel.setOpaque(false);
		subPanel.setLayout(new GridBagLayout());
		subPanel.setBorder(BorderFactory.createTitledBorder("Local"));

		subPanel.add(new JLabel("nom"), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
		subPanel.add(name, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(2, 2, 2, 2), 0, 0));
		subPanel.add(new JLabel("port"), new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
		subPanel.add(listenPort, new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
		subPanel.add(start, new GridBagConstraints(4, 0, 1, 1, 1, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(2, 2, 2, 2), 0, 0));

		mainPanel.add(subPanel, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));

		subPanel = new JPanel();
		subPanel.setOpaque(false);
		subPanel.setLayout(new GridBagLayout());
		subPanel.setBorder(BorderFactory.createTitledBorder("Pair"));

		subPanel.add(new JLabel("ip"), new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
		subPanel.add(pairIp, new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(2, 2, 2, 2), 0, 0));
		subPanel.add(new JLabel("port"), new GridBagConstraints(4, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
		subPanel.add(pairPort, new GridBagConstraints(5, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
		subPanel.add(join, new GridBagConstraints(6, 0, 2, 1, 1, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(2, 2, 2, 2), 0, 0));
		subPanel.add(disconnect, new GridBagConstraints(8, 0, 1, 1, 1, 0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

		mainPanel.add(subPanel, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));

		subPanel = new JPanel();
		subPanel.setOpaque(false);
		subPanel.setLayout(new GridBagLayout());
		subPanel.setBorder(BorderFactory.createTitledBorder("Messages"));

		subPanel.add(scrollSole, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));

		subPanel.add(message, new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));

		mainPanel.add(subPanel, new GridBagConstraints(0, 3, 1, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));

		// Window

		Frame frame = new Frame(mainPanel);
		frame.pack();
		frame.setBounds(frame.getWidth() * id, frame.getY(), frame.getWidth(), frame.getHeight());
	}

	public class ConsoleOutputStream extends OutputStream {
		private final StringBuilder sb = new StringBuilder();

		@Override
		public void flush() {
		}

		@Override
		public void close() {
		}

		@Override
		public void write(int b) throws IOException {

			if (b == '\r')
				return;

			if (b == '\n') {
				final String text = sb.toString() + "\n";
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						console.setText(console.getText() + text);
						scrollSole.getVerticalScrollBar().setValue(scrollSole.getVerticalScrollBar().getMaximum());
					}
				});
				sb.setLength(0);

				return;
			}

			sb.append((char) b);
		}
	}

	private class Handler implements P2PHandler {
		@Override
		public void handleMessage(P2PUser sender, Object o) {
			stream.println(sender.getName() + " : " + o.toString());
		}

		@Override
		public void userConnect(P2PUser user) {
			stream.println(user + " connected");
		}

		@Override
		public void userDisconnect(P2PUser user) {
			stream.println(user + " disconnected");
		}

		@Override
		public void userUpdate(P2PUser user) {
			stream.println("update of " + user);
		}
	}

	public static void main(String[] args) {
		new DemoApp(0);
		new DemoApp(1);
		new DemoApp(2);
		new DemoApp(3);
	}

}
