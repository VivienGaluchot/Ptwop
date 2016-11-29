package ptwop.networker.display;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;

import ptwop.common.Animable;
import ptwop.common.gui.SpaceTransform;
import ptwop.common.math.Vector2D;
import ptwop.networker.Command;
import ptwop.networker.model.Network;
import ptwop.networker.model.Node;
import ptwop.networker.model.Steppable;

public class NetworkWrapper implements Animable, MouseListener, MouseMotionListener, Steppable {

	private SpaceTransform spaceTransform;
	private Command command;

	private Network network;
	private HashMap<Node, NodeWrapper> nodes;

	public NetworkWrapper(Network network, SpaceTransform spaceTransform, Command command) {
		this.network = network;
		this.spaceTransform = spaceTransform;
		this.command = command;
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
		for (Node n : nodes.keySet())
			nodes.get(n).paint(g);
	}

	@Override
	public void animate(long timeStep) {
		for (Node n : nodes.keySet())
			nodes.get(n).animate(timeStep);
	}

	public void doTimeStep() {
		network.doTimeStep();
	}

	public NodeWrapper getNodeAtPos(Vector2D pos) {
		for (Node n : nodes.keySet()) {
			if (nodes.get(n).getTranslatedShape().contains(pos.x, pos.y)) {
				return nodes.get(n);
			}
		}
		return null;
	}

	// Mouse listener

	private NodeWrapper selected = null;
	private NodeWrapper hovered = null;

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
		if (selected != null)
			selected.setPos(Math.round(mousePos.x * 2) / 2, Math.round(mousePos.y * 2) / 2);
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
		NodeWrapper n = getNodeAtPos(new Vector2D(mousePos.x, mousePos.y));
		setSelected(n);
		if (n != null)
			command.displayNode(n.getNode());
		else
			command.displayNode(null);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		setSelected(null);
		mouseMoved(e);
	}

}
