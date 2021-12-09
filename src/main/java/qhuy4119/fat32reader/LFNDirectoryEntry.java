package qhuy4119.fat32reader;

import java.nio.charset.StandardCharsets;

public class LFNDirectoryEntry extends DirectoryEntry{
	public LFNDirectoryEntry(byte[] bytes){
		super(bytes);
	}

	public LFNDirectoryEntry(DirectoryEntry dirEntry) {
		super(dirEntry.bytes);
	}

	@Override	
	protected void initFields(){
		fields.put("seqnumAndAllocationStatus", byteBuffer.get());
		fields.put("char1To5Filename", getUTF16String(1, 10));
		fields.put("fileAttributes", byteBuffer.get(11));
		fields.put("checksum", byteBuffer.get(13));
		fields.put("char6To11Filename", getUTF16String(14, 12));
		fields.put("char12To13Filename", getUTF16String(28, 4));
	}

	@Override
	public String getFilename(){
		String filename = (String)fields.get("char1To5Filename") + 
			(String)fields.get("char6To11Filename") + 
			(String)fields.get("char12To13Filename");
		filename = filename.replace((char)0xffff, Character.MIN_VALUE);
		filename = filename.replace("\0", "");
		return filename;
	}


	public boolean isLastLFN() {
		byte status = (Byte)fields.get("seqnumAndAllocationStatus");
		return status == (status | 0x40);
	}

	private String getUTF16String(int index, int size){
		byte[] buf = new byte[size];
		byteBuffer.get(index, buf);
		return new String(buf, StandardCharsets.UTF_16LE);
	}

}
