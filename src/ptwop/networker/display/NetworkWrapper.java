package ptwop.networker.display;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;

import ptwop.common.Animable;
import ptwop.common.gui.SpaceTransform;
import ptwop.common.math.Vector2D;
import ptwop.networker.model.Network;
import ptwop.networker.model.Node;

public class NetworkWrapper implements Animable, MouseListener, MouseMotionListener {

	private Network network;
	private SpaceTransform spaceTransform;
	private HashMap<Node, NodeWrapper> nodes;

	public NetworkWrapper(Network network, SpaceTransform spaceTransform) {
		this.network = network;
		this.spaceTransform = spaceTransform;
		nodes = new HashMap<>();
		for (Node n : network.getNodes())
			addNode(n);
	}

	private void addNode(Node n) {
		nodes.put(n, new NodeWrapper(n, this));
	}

	public NodeWrapper getWrapper(Node n) {
		return nodes.get(n);
	}

	@Override
	public void paint(Graphics g) {
		long time = network.getTime();
		g.drawString("time " + time, -9, -9);
		for (Node n : nodes.keySet())
			nodes.get(n).paint(g);
	}

	@Override
	public void animate(long timeStep) {
		network.doTimeStep();

		for (Node n : nodes.keySet())
			nodes.get(n).animate(timeStep);
	}

	// Mouse listener

	private NodeWrapper selected = null;
	private Color oldColor = null;

	private void setSelected(NodeWrapper selected) {
		if (this.selected != null) {
			this.selected.setDrawColor(oldColor);
		}
		this.selected = selected;
		if (this.selected != null) {
			oldColor = selected.getDrawColor();
			this.selected.setDrawColor(Color.red);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Vector2D mousePos = spaceTransform.transformMousePosition(e.getPoint());
		if (selected != null)
			selected.setPos(mousePos.x, mousePos.y);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Vector2D mousePos = spaceTransform.transformMousePosition(e.getPoint());
		for (Node n : nodes.keySet()) {
			if (nodes.get(n).getTranslatedShape().contains(mousePos.x, mousePos.y)) {
				setSelected(nodes.get(n));
				break;
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		setSelected(null);
	}

}
