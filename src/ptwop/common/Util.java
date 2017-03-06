package ptwop.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class Util {

	public static byte[] serialize(Object o){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] res = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(o);
			out.flush();
			res = bos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bos.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return res;
	}
}
