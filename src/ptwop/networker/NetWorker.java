package ptwop.networker;

import ptwop.common.gui.AnimationPanel;
import ptwop.common.gui.Frame;
import ptwop.common.gui.SpaceTransform;

public class NetWorker {
	public static void main(String[] args) {
		SpaceTransform spaceTransform = new SpaceTransform();	
		AnimationPanel mainPanel = new AnimationPanel(spaceTransform);
		spaceTransform.setFather(mainPanel);
		
		Frame frame = new Frame(mainPanel);	
	}
}
