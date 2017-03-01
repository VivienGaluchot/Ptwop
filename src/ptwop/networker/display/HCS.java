package ptwop.networker.display;

import java.awt.Shape;

public interface HCS {
	public void setHovered(boolean hovered);

	public boolean isHovered();

	public void setClicked(boolean clicked);

	public boolean isClicked();

	public void setSelected(boolean selected);

	public boolean isSelected();

	public Shape getShape();
}
