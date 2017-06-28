package ptwop.demoApp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
	private JPanel userInfo;

	private boolean isDefaultMessage;
	private static String defaultMessage = "Type your message here";

	private static Color backgroundColor = Color.white;
	private static Color msgBackgroundColor = new Color(230, 240, 245);
	private static Color msgForegroundColor = new Color(20, 50, 150);
	private static Color msgCaretColor = new Color(20, 30, 50);
	private static Color msgBorderColor = new Color(150, 200, 230);
	private static Font defaultFont = new Font("default", Font.PLAIN, 12);
	private static Font defaultMessageFont = new Font("default", Font.ITALIC, 12);

	public DemoApp(int id) {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBackground(backgroundColor);

		p2p = null;
		stream = new PrintStream(new ConsoleOutputStream());

		// Fields
		listenPort = new JTextField(Integer.toString(919 + id), 4);
		name = new JTextField("Patrick", 6);
		defaultIp = "127.0.0.1";
		defaultPort = "919";

		infoP2P = new JLabel("disconnected");
		infoP2P.setFont(defaultFont);

		message = new JTextPane();
		message.setFont(defaultFont);
		message.setForeground(msgForegroundColor);
		message.setCaretColor(msgCaretColor);
		message.setOpaque(false);

		isDefaultMessage = true;
		message.setText(defaultMessage);
		message.setFont(defaultMessageFont);

		message.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (e.getKeyChar() == '\n' && !isDefaultMessage) {
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

		message.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				if (isDefaultMessage) {
					message.setFont(defaultFont);
					message.setText("");
				}
				isDefaultMessage = false;
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				isDefaultMessage = message.getText().length() == 0;
				if (isDefaultMessage) {
					message.setFont(defaultMessageFont);
					message.setText(defaultMessage);
				}
			}
		});

		console = new JTextPane();
		console.setEditable(false);
		console.setFont(new Font("Consolas", Font.PLAIN, 12));
		console.setBackground(msgBackgroundColor);
		console.setForeground(msgForegroundColor);
		console.setCaretColor(msgCaretColor);
		scrollSole = new JScrollPane(console);
		scrollSole.setMinimumSize(new Dimension(100, 100));
		scrollSole.setPreferredSize(new Dimension(100, 100));
		scrollSole.setBorder(BorderFactory.createEmptyBorder());

		// Buttons
		start = new JButton("Start");
		join = new JButton("Connect");
		stop = new JButton("Stop");
		join.setEnabled(false);
		stop.setEnabled(false);

		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int port = 0;
				try {
					port = Integer.parseInt(listenPort.getText());
					NServent manager = new TcpNServent(port);
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
					if (e instanceof IOException) {
						if (Dialog.YesNoDialog(mainPanel,
								"Port may be used, do you wan't to try with an other (" + (port + 1) + ") ?")) {
							listenPort.setText(Integer.toString(port + 1));
							this.actionPerformed(arg0);
						}
					}
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
		// subPanel.setBorder(BorderFactory.createTitledBorder("Messages"));
		subLine = 0;
		subPanel.add(new JLabel("Messages"), new GridBagConstraints(0, subLine, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(4, 4, 0, 4), 0, 0));
		subLine++;
		
		JPanel subSubPanel = new JPanel();
		subSubPanel.setOpaque(true);
		subSubPanel.setLayout(new GridBagLayout());
		subSubPanel.setBorder(BorderFactory.createLineBorder(msgBorderColor));
		subSubPanel.setBackground(msgBackgroundColor);
		subSubPanel.add(scrollSole, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		subPanel.add(subSubPanel, new GridBagConstraints(0, subLine, 1, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(4, 4, 4, 4), 0, 0));
		subLine++;

		subSubPanel = new JPanel();
		subSubPanel.setOpaque(true);
		subSubPanel.setLayout(new GridBagLayout());
		subSubPanel.setBorder(BorderFactory.createLineBorder(msgBorderColor));
		subSubPanel.setBackground(msgBackgroundColor);
		subSubPanel.add(message, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		subPanel.add(subSubPanel, new GridBagConstraints(0, subLine, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 4, 4, 4), 0, 0));

		mainPanel.add(subPanel, new GridBagConstraints(0, line++, 2, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 4, 4, 0), 0, 0));

		/* Sidepanel */
		JPanel sidePanel = new JPanel();
		sidePanel.setBackground(backgroundColor);
		sidePanel.setLayout(new GridBagLayout());
		line = 0;

		/* Users */
		subPanel = new JPanel();
		subPanel.setOpaque(false);
		subPanel.setLayout(new GridBagLayout());
		subPanel.setBorder(BorderFactory.createTitledBorder("Users"));
		subLine = 0;

		userList = new JList<>();
		userList.setOpaque(false);
		userListModel = new DefaultListModel<>();
		userList.setModel(userListModel);
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		userList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				updateUserInfo(userList.getSelectedValue());
			}
		});
		userList.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				// do nothing
			}

			@Override
			public void focusLost(FocusEvent e) {
				userList.clearSelection();
			}
		});
		JScrollPane scrollList = new JScrollPane(userList);
		scrollList.setMinimumSize(new Dimension(150, 150));
		scrollList.setPreferredSize(new Dimension(150, 150));
		subPanel.add(scrollList, new GridBagConstraints(0, subLine, 1, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(4, 4, 4, 4), 0, 0));

		subLine++;
		userInfo = new JPanel();
		userInfo.setOpaque(false);
		userInfo.setLayout(new GridBagLayout());
		updateUserInfo(null);
		subPanel.add(userInfo, new GridBagConstraints(0, subLine, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

		sidePanel.add(subPanel, new GridBagConstraints(0, line++, 1, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(4, 4, 4, 4), 0, 0));

		// Window
		frame = new Frame(mainPanel, sidePanel);
		frame.setTitle("PtwoP - Demo App");
		frame.pack();
		frame.setMinimumSize(frame.getSize());
		frame.setSize(new Dimension(500, 500));
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

	private void updateUserInfo(P2PUser selected) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				userInfo.removeAll();
				int line = 0;

				int nbUsers = (p2p != null) ? p2p.getUsers().size() : 0;
				String strNbUsers = (nbUsers > 1) ? nbUsers + " users" : nbUsers + " user";
				JLabel label = new JLabel(strNbUsers + " connected");
				label.setFont(defaultFont);
				userInfo.add(label, new GridBagConstraints(0, line++, 2, 1, 1, 0, GridBagConstraints.WEST,
						GridBagConstraints.NONE, new Insets(4, 4, 8, 4), 0, 0));

				if (selected == null) {
					label = new JLabel("Empty user selection");
					label.setFont(defaultFont);
					userInfo.add(label, new GridBagConstraints(0, line++, 2, 1, 1, 0, GridBagConstraints.WEST,
							GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
				} else {
					label = new JLabel("Name");
					userInfo.add(label, new GridBagConstraints(0, line, 1, 1, 1, 0, GridBagConstraints.WEST,
							GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
					label = new JLabel(selected.getName());
					label.setFont(defaultFont);
					userInfo.add(label, new GridBagConstraints(1, line++, 1, 1, 1, 0, GridBagConstraints.WEST,
							GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));

					label = new JLabel("NAddress");
					userInfo.add(label, new GridBagConstraints(0, line, 1, 1, 1, 0, GridBagConstraints.WEST,
							GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
					label = new JLabel(selected.getAddress().toString());
					label.setFont(defaultFont);
					userInfo.add(label, new GridBagConstraints(1, line++, 1, 1, 1, 0, GridBagConstraints.WEST,
							GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));

					label = new JLabel("Ping");
					userInfo.add(label, new GridBagConstraints(0, line, 1, 1, 1, 0, GridBagConstraints.WEST,
							GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
					label = new JLabel(selected.getBindedNPair().getLatency() + " ms");
					label.setFont(defaultFont);
					userInfo.add(label, new GridBagConstraints(1, line++, 1, 1, 1, 0, GridBagConstraints.WEST,
							GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
				}
				userInfo.revalidate();
			}
		});
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
			updateUserInfo(userList.getSelectedValue());
		}

		@Override
		public void handleUserDisconnect(P2PUser user) {
			stream.println(user + " disconnected");
			userListModel.removeElement(user);
			updateUserInfo(userList.getSelectedValue());
		}

		@Override
		public void handleUserUpdate(P2PUser user) {
			stream.println("update of " + user);
			userList.repaint();
			updateUserInfo(userList.getSelectedValue());
		}
	}

	public static void main(String[] args) {
		new DemoApp(0);
		// new DemoApp(1);
		// new DemoApp(2);
		// new DemoApp(3);
	}

}
