package qhuy4119.fat32reader;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class BootSector {
	private String filePath;
	private ByteBuffer bytes;
	private HashMap<String, Object> fields = new HashMap<String, Object>();


	public Map<String, Object> getFields() {
		return new HashMap<String, Object>(fields);
	}

	public BootSector(String filePath) throws FileNotFoundException, IOException{
		this.filePath = filePath;
		try(var in = new FileInputStream(this.filePath)){
			bytes = ByteBuffer.wrap(in.readNBytes(512));
			bytes.order(ByteOrder.LITTLE_ENDIAN);
			fields.put("oemName", getAsciiString(3, 8));
			fields.put("bytesPerSector", bytes.getShort(11));
			fields.put("sectorsPerCluster", bytes.get(13));
			fields.put("numReservedSectors", bytes.getShort(14));
			fields.put("numFats", bytes.get(16));
			fields.put("mediaType", bytes.get(21));
			fields.put("sectorsPerTrack", bytes.getShort(24));
			fields.put("numHeads", bytes.getShort(26));
			fields.put("numTotalSectors", bytes.getInt(32));
			fields.put("sectorsPerFat", bytes.getInt(36));
			fields.put("rootDirStartCluster", bytes.getInt(44));
			fields.put("volumeSerialNumber", bytes.getInt(67));
			fields.put("volumeLabel", getAsciiString(71, 11));
			fields.put("optionalFileSystemTypeLabel", getAsciiString(82, 8));
		}
	}

	private String getAsciiString(int index, int size){
		byte[] buf = new byte[size];
		bytes.get(index, buf);
		return new String(buf, StandardCharsets.US_ASCII);
	}


	public static void main(String[] args) throws FileNotFoundException, IOException {
		var bs = new BootSector(args[0]);
		System.out.println(bs.fields);
	}
}
