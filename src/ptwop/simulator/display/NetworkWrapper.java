package ptwop.simulator.display;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.HashMap;

import ptwop.common.gui.Animable;
import ptwop.common.gui.SpaceTransform;
import ptwop.common.math.Vector2D;
import ptwop.simulator.Command;
import ptwop.simulator.model.Link;
import ptwop.simulator.model.Network;
import ptwop.simulator.model.Node;
import ptwop.simulator.model.Steppable;
import ptwop.simulator.model.TimedData;

public class NetworkWrapper implements Animable, MouseListener, MouseMotionListener, Steppable, MouseWheelListener {

	private SpaceTransform spaceTransform;
	private Command command;

	private Network network;
	private HashMap<Node, NodeWrapper> nodes;
	private HashMap<Link, LinkWrapper> links;
	private HashMap<TimedData, DataWrapper> datas;

	private boolean animated;

	// Mouse
	private HCS hovered = null;
	private HCS clicked = null;
	private HCS selected = null;

	private Vector2D lastMousePos;

	private Color color;
	private Color hoveredColor;
	private Color clickedColor;
	private Color selectedColor;

	public NetworkWrapper(Network network, SpaceTransform spaceTransform) {
		animated = false;

		this.network = network;
		this.network.setWrapper(this);
		this.spaceTransform = spaceTransform;
		this.command = null;
		nodes = new HashMap<>();
		links = new HashMap<>();
		datas = new HashMap<>();
		for (Node n : network.getNodes()) {
			addNode(n);
			for(Link l : n.getLinks())
				addLink(l);
		}
		lastMousePos = null;

		color = Color.darkGray;
		clickedColor = new Color(40, 70, 150);
		hoveredColor = new Color(80, 140, 200);
		selectedColor = Color.darkGray;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public synchronized void addNode(Node n) {
		nodes.put(n, new NodeWrapper(n, this));
	}

	public synchronized void removeNode(Node n) {
		nodes.remove(n);
	}

	public synchronized void addLink(Link l) {
		links.put(l, new LinkWrapper(l, this));
	}

	public synchronized void removeLink(Link l) {
		links.remove(l);
		for (TimedData d : l.getTransitingDatas()) {
			if (datas.containsKey(d))
				removeData(d);
		}
	}

	public synchronized void addData(TimedData d, Link l) {
		LinkWrapper wrapper = links.get(l);
		if (wrapper != null)
			datas.put(d, new DataWrapper(d, links.get(l), this));
		else
			throw new IllegalArgumentException("Unkwnown link : " + l);
	}

	public synchronized void removeData(TimedData d) {
		datas.remove(d);
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

	public Network getNetwork() {
		return network;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getHoveredColor() {
		return hoveredColor;
	}

	public void setHoveredColor(Color hoveredColor) {
		this.hoveredColor = hoveredColor;
	}

	public Color getClickedColor() {
		return clickedColor;
	}

	public void setClickedColor(Color clickedColor) {
		this.clickedColor = clickedColor;
	}

	public Color getSelectedColor() {
		return selectedColor;
	}

	public void setSelectedColor(Color selectedColor) {
		this.selectedColor = selectedColor;
	}

	@Override
	public synchronized void paint(Graphics g) {
		for (Link l : links.keySet())
			links.get(l).paint(g);

		for (Node n : nodes.keySet())
			nodes.get(n).paint(g);

		ArrayList<DataWrapper> toPaintLast = new ArrayList<>();
		for (TimedData d : datas.keySet()) {
			DataWrapper dw = datas.get(d);
			if (dw.isHovered() || dw.isSelected())
				toPaintLast.add(dw);
			else
				datas.get(d).paint(g);
		}
		for (DataWrapper dw : toPaintLast)
			dw.paint(g);
	}

	public boolean isAnimated() {
		return animated;
	}

	public void setAnimated(boolean animated) {
		this.animated = animated;
	}

	@Override
	public synchronized void animate(long timeStep) {
		if (animated)
			network.doTimeStep();
	}

	@Override
	public synchronized void doTimeStep() {
		network.doTimeStep();
	}

	// Mouse listener

	private synchronized HCS getHCSAtPos(Vector2D pos) {
		try {
			for (Node n : nodes.keySet()) {
				if (nodes.get(n).getShape().contains(pos.x, pos.y)) {
					return nodes.get(n);
				}
			}
			for (TimedData d : datas.keySet()) {
				if (datas.get(d).getShape().contains(pos.x, pos.y)) {
					return datas.get(d);
				}
			}
			for (Link l : links.keySet()) {
				if (links.get(l).getShape().contains(pos.x, pos.y)) {
					return links.get(l);
				}
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	private void setHovered(HCS hovered) {
		if (this.hovered != null) {
			this.hovered.setHovered(false);
		}
		this.hovered = hovered;
		if (this.hovered != null) {
			this.hovered.setHovered(true);
		}
	}

	private void setClicked(HCS clicked) {
		if (this.clicked != null) {
			this.clicked.setClicked(false);
		}
		this.clicked = clicked;
		if (this.clicked != null) {
			this.clicked.setClicked(true);
		}
	}

	private void setSelected(HCS selected) {
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
		if (clicked != null && clicked instanceof NodeWrapper) {
			Vector2D newPos = ((NodeWrapper) clicked).getPos().add(deltaMousPos);
			((NodeWrapper) clicked).setPos(newPos);
		} else {
			spaceTransform.updateMouseDrag(e.getPoint());
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Vector2D mousePos = spaceTransform.transformMousePosition(e.getPoint());
		HCS hcs = getHCSAtPos(new Vector2D(mousePos.x, mousePos.y));
		setHovered(hcs);
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
		if (command == null)
			return;

		Vector2D mousePos = spaceTransform.transformMousePosition(e.getPoint());
		lastMousePos = mousePos;

		HCS hcs = getHCSAtPos(new Vector2D(mousePos.x, mousePos.y));
		setClicked(hcs);

		if (hcs != null && hcs instanceof NodeWrapper) {
			command.displayNode(((NodeWrapper) hcs).getNode());
		} else {
			spaceTransform.startMouseDrag(e.getPoint());
			command.displayNode(null);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		setSelected(clicked);
		setClicked(null);
		mouseMoved(e);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int rotation = e.getWheelRotation();
		spaceTransform.zoom(rotation);
		mouseMoved(e);
	}

}
