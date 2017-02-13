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
import ptwop.network.NManager;
import ptwop.network.tcp.TcpNAddress;
import ptwop.network.tcp.TcpNManager;
import ptwop.p2p.P2P;
import ptwop.p2p.P2PHandler;
import ptwop.p2p.P2PUser;
import ptwop.p2p.flood.FloodV0;

public class DemoApp {

	private static P2P p2p;
	private static JTextPane console;
	private static JScrollPane scrollSole;

	private static class ConsoleOutputStream extends OutputStream {
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

	private static class Handler implements P2PHandler {
		@Override
		public void handleMessage(P2PUser sender, Object o) {
			System.out.println(sender.getName() + " : \n" + o.toString());
		}

		@Override
		public void userConnect(P2PUser user) {
			System.out.println(user + " connected");
		}

		@Override
		public void userDisconnect(P2PUser user) {
			System.out.println(user + " disconnected");
		}

		@Override
		public void userUpdate(P2PUser user) {
			System.out.println("update of " + user);
		}
	}

	public static void main(String[] args) {
		System.setOut(new PrintStream(new ConsoleOutputStream()));

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());

		p2p = null;

		// Fields
		JTextField listenPort = new JTextField("919", 4);
		JTextField name = new JTextField("Patrick", 6);
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
					public void run() {
						if (e.getKeyChar() == '\n') {
							String msg = message.getText();

							if (msg.endsWith("\n")) {
								msg = msg.substring(0, msg.length() - 1);
							}

							if (e.isShiftDown()) {
								message.setText(msg + '\n');
							} else {
								if (msg.compareTo("\\ls") == 0) {
									if (p2p != null) {
										System.out.println("me- " + p2p.getMyself().toString());
										for (P2PUser u : p2p.getUsers()) {
											System.out.println("--- " + u.toString());
										}
									}
								} else {
									System.out.println("me : " + msg);
									if (p2p != null)
										p2p.broadcast(msg);
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
					NManager manager = new TcpNManager(Integer.parseInt(listenPort.getText()));
					p2p = new FloodV0(manager, name.getText());
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
					pairIp.setEditable(false);
					pairPort.setEditable(false);
					disconnect.setEnabled(true);
					join.setEnabled(false);
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
				pairIp.setEditable(true);
				pairPort.setEditable(true);
				join.setEnabled(true);
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
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		subPanel.add(name, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
		subPanel.add(new JLabel("port"), new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		subPanel.add(listenPort, new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		subPanel.add(start, new GridBagConstraints(4, 0, 1, 1, 1, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		mainPanel.add(subPanel, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

		subPanel = new JPanel();
		subPanel.setOpaque(false);
		subPanel.setLayout(new GridBagLayout());
		subPanel.setBorder(BorderFactory.createTitledBorder("Pair"));

		subPanel.add(new JLabel("ip"), new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		subPanel.add(pairIp, new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
		subPanel.add(new JLabel("port"), new GridBagConstraints(4, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		subPanel.add(pairPort, new GridBagConstraints(5, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		subPanel.add(join, new GridBagConstraints(6, 0, 2, 1, 1, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		mainPanel.add(subPanel, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

		mainPanel.add(disconnect, new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

		subPanel = new JPanel();
		subPanel.setOpaque(false);
		subPanel.setLayout(new GridBagLayout());
		subPanel.setBorder(BorderFactory.createTitledBorder("Messages"));

		subPanel.add(scrollSole, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

		subPanel.add(message, new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

		mainPanel.add(subPanel, new GridBagConstraints(0, 3, 1, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

		// Window

		Frame frame = new Frame(mainPanel);
		frame.pack();
	}

}
