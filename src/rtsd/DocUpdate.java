package rtsd;

import java.io.Serializable;

public class DocUpdate implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public static enum UpdateType {
		INSERT, REMOVE, CHANGE
	}
	
	public UpdateType type;
	public int offset;
	public int length;
	public String txt;
	
	public DocUpdate(UpdateType type, int offset, int length, String txt){
		this.type = type;
		this.offset = offset;
		this.length = length;
		this.txt = txt;
	}
}
