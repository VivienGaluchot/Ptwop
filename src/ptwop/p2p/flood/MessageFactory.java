package ptwop.p2p.flood;

import ptwop.network.NAddress;

public class MessageFactory {
	private static enum ByteEnum {
		CONNECT_TO, DATA, MY_NAME_IS, ADD_TO_NEIGHOURS;

		private byte b = 0;

		ByteEnum() {
			if ((b = (byte) this.ordinal()) > Byte.MAX_VALUE)
				throw new RuntimeException("Can't create more than " + Byte.MAX_VALUE + " ByteEnum");
		}

		public byte value() {
			return b;
		}
	}
	
	public static byte[] createConnectToMessage(NAddress pair){
		byte[] res = new byte[pair.byteSize()+1];
		res[0] = ByteEnum.CONNECT_TO.value();
		pair.serialize(1, res);
		return res;
	}
	
	public static byte[] createDataMessage(byte[] bytes){
		byte[] res = new byte[bytes.length+1];
		res[0] = ByteEnum.DATA.value();
		pair.serialize(1, res);
		return res;
	}
}
