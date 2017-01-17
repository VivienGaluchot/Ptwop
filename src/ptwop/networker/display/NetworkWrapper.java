package ptwop.networker.display;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;

import ptwop.common.Animable;
import ptwop.common.gui.SpaceTransform;
import ptwop.common.math.Vector2D;
import ptwop.networker.Command;
import ptwop.networker.model.Network;
import ptwop.networker.model.Node;
import ptwop.networker.model.Steppable;

public class NetworkWrapper implements Animable, MouseListener, MouseMotionListener, Steppable, MouseWheelListener {

	private SpaceTransform spaceTransform;
	private Command command;

	private Network network;
	private HashMap<Node, NodeWrapper> nodes;

	// Mouse
	private NodeWrapper selected = null;
	private NodeWrapper hovered = null;
	private Vector2D lastMousePos;

	public NetworkWrapper(Network network, SpaceTransform spaceTransform, Command command) {
		this.network = network;
		this.spaceTransform = spaceTransform;
		this.command = command;
		nodes = new HashMap<>();
		for (Node n : network.getNodes())
			addNode(n);
		lastMousePos = null;
	}

	private void addNode(Node n) {
		nodes.put(n, new NodeWrapper(n, this));
	}

	public void putInCircle() {
		double space = 1;

		Vector2D[] positions = new Vector2D[nodes.size()];
		for (int i = 0; i < positions.length; i++) {
			double angle = i * 2 * Math.PI / positions.length;
			positions[i] = new Vector2D(Math.cos(angle), Math.sin(angle));
			positions[i] = positions[i].multiply(positions.length * space);
		}

		int i = 0;
		for (Node n : nodes.keySet()) {
			NodeWrapper nw = nodes.get(n);
			nw.setPos(positions[i % positions.length]);
			i++;
		}
	}

	public NodeWrapper getWrapper(Node n) {
		return nodes.get(n);
	}

	@Override
	public void paint(Graphics g) {
		for (Node n : nodes.keySet())
			nodes.get(n).paint(g);
	}

	@Override
	public void animate(long timeStep) {
		for (Node n : nodes.keySet())
			nodes.get(n).animate(timeStep);
	}

	@Override
	public void doTimeStep() {
		network.doTimeStep();
	}

	public NodeWrapper getNodeAtPos(Vector2D pos) {
		NodeWrapper res = null;
		// get the last of the set
		for (Node n : nodes.keySet()) {
			if (nodes.get(n).getTranslatedShape().contains(pos.x, pos.y)) {
				res = nodes.get(n);
			}
		}
		return res;
	}

	// Mouse listener

	private void setSelected(NodeWrapper selected) {
		if (this.selected != null) {
			this.selected.setSelected(false);
		}
		this.selected = selected;
		if (this.selected != null) {
			this.selected.setSelected(true);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Vector2D mousePos = spaceTransform.transformMousePosition(e.getPoint());
		Vector2D deltaMousPos = mousePos.subtract(lastMousePos);
		lastMousePos = mousePos;
		if (selected != null) {
			Vector2D newPos = selected.getPos().add(deltaMousPos);
			selected.setPos(newPos);
		} else {
			spaceTransform.updateMouseDrag(e.getPoint());
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Vector2D mousePos = spaceTransform.transformMousePosition(e.getPoint());
		NodeWrapper n = getNodeAtPos(new Vector2D(mousePos.x, mousePos.y));
		if (n != hovered && hovered != null)
			hovered.setSelected(false);

		hovered = n;
		if (hovered != null)
			hovered.setHovered(true);
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
		lastMousePos = mousePos;

		NodeWrapper n = getNodeAtPos(new Vector2D(mousePos.x, mousePos.y));
		setSelected(n);
		if (n != null)
			command.displayNode(n.getNode());
		else {
			spaceTransform.startMouseDrag(e.getPoint());
			command.displayNode(null);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		setSelected(null);
		mouseMoved(e);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int rotation = e.getWheelRotation();
		spaceTransform.zoom(rotation);
		mouseMoved(e);
	}

}
