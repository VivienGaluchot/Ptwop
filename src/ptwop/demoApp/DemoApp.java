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
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
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
import ptwop.p2p.core.*;
import ptwop.p2p.routing.*;

public class DemoApp {

	private P2P p2p;
	private JTextPane console;
	private JScrollPane scrollSole;
	private PrintStream stream;
	private Frame frame;
	private DefaultListModel<P2PUser> userListModel;
	private JList<P2PUser> userList;
	private JLabel infoP2P;
	private JTextField listenPort;
	private String defaultIp;
	private String defaultPort;
	private JTextField name;
	private JTextPane message;
	private JButton start;
	private JButton join;
	private JButton stop;

	public DemoApp(int id) {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBackground(Color.white);

		p2p = null;
		stream = new PrintStream(new ConsoleOutputStream());

		// Fields
		listenPort = new JTextField(Integer.toString(919 + id), 4);
		name = new JTextField("Patrick " + id, 6);
		defaultIp = "127.0.0.1";
		defaultPort = "919";

		infoP2P = new JLabel("disconnected");
		infoP2P.setFont(new Font("default", Font.PLAIN, 12));

		message = new JTextPane();
		message.setFont(new Font("Consolas", Font.PLAIN, 12));
		message.setBorder(BorderFactory.createLineBorder(Color.darkGray));
		// message.setPreferredSize(new Dimension(200,20));
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
							if (e.isShiftDown()) {
								message.setText(msg + '\n');
							} else {
								if (msg.endsWith("\n"))
									msg = msg.substring(0, msg.length() - 1);
								processText(msg);
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
		scrollSole.setPreferredSize(new Dimension(300, 300));
		scrollSole.setBorder(BorderFactory.createLineBorder(Color.black));

		// Buttons
		start = new JButton(".start()");
		join = new JButton(".connectTo()");
		stop = new JButton(".stop()");
		join.setEnabled(false);
		stop.setEnabled(false);

		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					NServent manager = new TcpNServent(Integer.parseInt(listenPort.getText()));
					p2p = new CoreV1(name.getText());
					p2p.setP2PHandler(new Handler());
					p2p.start(manager, new StockasticRouter());
					infoP2P.setText(p2p + ", listenning...");
					listenPort.setEditable(false);
					name.setEditable(false);
					stop.setEnabled(true);
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
					String ip = Dialog.IPDialog(frame, "Enter pair's ip address", defaultIp);
					if (ip == null)
						return;
					Integer port = Dialog.PortDialog(frame, "Enter pair's listen port", defaultPort);
					if (port == null)
						return;
					p2p.connectTo(new TcpNAddress(InetAddress.getByName(ip), port));
					stop.setEnabled(true);
				} catch (Exception e) {
					Dialog.displayError(mainPanel, e.getMessage());
				}
			}
		});

		stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (p2p != null)
					p2p.stop();
				infoP2P.setText("disconnected");
				listenPort.setEditable(true);
				name.setEditable(true);
				join.setEnabled(false);
				start.setEnabled(true);
				stop.setEnabled(false);
			}
		});

		// Layout
		int line = 0;
		int subLine = 0;

		/* P2P */
		JPanel subPanel = new JPanel();
		subPanel.setOpaque(false);
		subPanel.setLayout(new GridBagLayout());
		subPanel.setBorder(BorderFactory.createTitledBorder("P2P"));
		subLine = 0;
		subPanel.add(new JLabel("Info"), new GridBagConstraints(0, subLine, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		subPanel.add(infoP2P, new GridBagConstraints(1, subLine, 3, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		subLine++;
		subPanel.add(new JLabel("Name"), new GridBagConstraints(0, subLine, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		subPanel.add(name, new GridBagConstraints(1, subLine, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0));
		subPanel.add(new JLabel("Port"), new GridBagConstraints(2, subLine, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		subPanel.add(listenPort, new GridBagConstraints(3, subLine, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0));

		mainPanel.add(subPanel, new GridBagConstraints(0, line, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(4, 4, 4, 4), 0, 0));

		/* Command */
		subPanel = new JPanel();
		subPanel.setOpaque(false);
		subPanel.setLayout(new GridBagLayout());
		subPanel.setBorder(BorderFactory.createTitledBorder("Command"));
		subLine = 0;
		subPanel.add(start, new GridBagConstraints(0, subLine, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		subPanel.add(join, new GridBagConstraints(1, subLine, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		subPanel.add(stop, new GridBagConstraints(2, subLine, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));

		mainPanel.add(subPanel, new GridBagConstraints(1, line++, 1, 1, 0, 0, GridBagConstraints.EAST,
				GridBagConstraints.BOTH, new Insets(4, 4, 4, 4), 0, 0));

		/* Messages */
		subPanel = new JPanel();
		subPanel.setOpaque(false);
		subPanel.setLayout(new GridBagLayout());
		subPanel.setBorder(BorderFactory.createTitledBorder("Messages"));
		subLine = 0;
		subPanel.add(scrollSole, new GridBagConstraints(0, subLine, 1, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(4, 4, 4, 4), 0, 0));
		subLine++;
		subPanel.add(message, new GridBagConstraints(0, subLine, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(4, 4, 4, 4), 0, 0));

		mainPanel.add(subPanel, new GridBagConstraints(0, line++, 2, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 4, 4, 4), 0, 0));

		/* Sidepanel */
		JPanel sidePanel = new JPanel();
		sidePanel.setBackground(Color.white);
		sidePanel.setLayout(new GridBagLayout());
		line = 0;

		/* Users */
		subPanel = new JPanel();
		subPanel.setOpaque(false);
		subPanel.setLayout(new GridBagLayout());
		subPanel.setBorder(BorderFactory.createTitledBorder("Users"));

		userList = new JList<>();
		userList.setMinimumSize(new Dimension(150, 150));
		userList.setPreferredSize(new Dimension(150, 150));
		userList.setOpaque(false);
		userListModel = new DefaultListModel<>();
		userList.setModel(userListModel);
		subPanel.add(userList, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(4, 4, 4, 4), 0, 0));

		sidePanel.add(subPanel, new GridBagConstraints(0, line++, 2, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(4, 4, 4, 4), 0, 0));

		// Window
		frame = new Frame(mainPanel, sidePanel);
		frame.setTitle("PtwoP - Demo App");
		frame.pack();
		frame.setMinimumSize(frame.getSize());
		// frame.setBounds(frame.getWidth() * id, frame.getY(),
		// frame.getWidth(), frame.getHeight());
	}

	private void processText(String msg) {
		if (msg.startsWith("-h")) {
			stream.println("-h    : avialable commands");
			stream.println("-ls   : list connected users");
			stream.println("-ping : show ping with each users");
		} else if (msg.startsWith("-ls")) {
			if (p2p != null) {
				stream.println("Utilisateurs");
				for (P2PUser u : p2p.getUsers()) {
					stream.println("- " + u.toString());
				}
			} else {
				stream.println("WARNING : p2p not initialized");
			}
		} else if (msg.startsWith("-ping")) {
			stream.println("me : " + msg);
			if (p2p != null) {
				stream.println("Latence");
				for (P2PUser u : p2p.getUsers()) {
					stream.println("- " + u.toString() + " " + u.getBindedNPair().getLatency() + " ms");
				}
			} else {
				stream.println("WARNING : p2p not initialized");
			}
		} else {
			stream.println("me : " + msg);
			if (p2p != null)
				p2p.broadcast(msg);
			else {
				stream.println("WARNING : p2p not initialized");
			}
		}
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
			frame.toFront();
		}

		@Override
		public void handleConnection(P2PUser user) {
			stream.println(user + " connected");
			userListModel.addElement(user);
		}

		@Override
		public void handleUserDisconnect(P2PUser user) {
			stream.println(user + " disconnected");
			userListModel.removeElement(user);
		}

		@Override
		public void handleUserUpdate(P2PUser user) {
			stream.println("update of " + user);
			userList.repaint();
		}
	}

	public static void main(String[] args) {
		new DemoApp(0);
		// new DemoApp(1);
		// new DemoApp(2);
		// new DemoApp(3);
	}

}
