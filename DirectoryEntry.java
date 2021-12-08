import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

class DirectoryEntry{
	protected ByteBuffer bytes;
	protected HashMap<String, Object> fields = new HashMap<String, Object>();

	public DirectoryEntry(byte[] b){
		bytes = ByteBuffer.wrap(b);
		bytes.order(ByteOrder.LITTLE_ENDIAN);
		initFields();
	}

	public Map<String, Object> getFields(){
		return new HashMap<String, Object>(fields);
	}
	public String getFilename(){
		return (String)fields.get("firstCharOfFilename") + (String)fields.get("char2To11Filename");
	}

	public int getAddrFirstCluster() {
		byte[] highBytes = (byte[]) fields.get("highBytesAddrFirstCluster");
		byte[] lowBytes = (byte[]) fields.get("lowBytesAddrFirstCluster");
		byte[] buf = new byte[4];
		for (int i = 0; i < 2; i++){
			buf[i] = highBytes[1 - i];
			buf[i+2] = lowBytes[1 - i];
		}
		ByteBuffer byteBuffer = ByteBuffer.wrap(buf);
		byteBuffer.order(ByteOrder.BIG_ENDIAN);
		return byteBuffer.getInt();
	}

	protected void initFields(){
		fields.put("firstCharOfFilename", bytes.get(0));
		fields.put("char2To11Filename", getAsciiString(1, 10));
		fields.put("fileAttributes", bytes.get(11));
		fields.put("highBytesAddrFirstCluster", getBytes(20, 2));
		fields.put("lowBytesAddrFirstCluster", getBytes(26, 2));
		fields.put("fileSize", bytes.getInt(28));
	}
	private String getAsciiString(int index, int size){
		byte[] data = new byte[size];
		bytes.get(index, data);
		return new String(data, StandardCharsets.US_ASCII);
	}
	private byte[] getBytes(int index, int size){
		byte[] b = new byte[size];
		bytes.get(b);
		return b;
	}
}
