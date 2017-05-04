package ptwop.common.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Frame extends JFrame {
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;

	public Frame(Component main) {
		this(main, null, "Ptwop - dev");
	}

	public Frame(Component main, String title) {
		this(main, null, title);
	}

	public Frame(Component main, JPanel sideBar) {
		this(main, sideBar, "Ptwop - dev");
	}

	public Frame(Component main, JPanel sideBar, String title) {
		setTitle(title);
		setSize(new Dimension(800, 600));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		contentPane = new JPanel();
		setContentPane(contentPane);

		contentPane.setLayout(new GridBagLayout());
		contentPane.add(main, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		if (sideBar != null)
			contentPane.add(sideBar, new GridBagConstraints(1, 0, 1, 1, 0, 1, GridBagConstraints.CENTER,
					GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));

		Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
		setIconImage(image);

		setVisible(true);
		setLocationRelativeTo(null);
	}
}
