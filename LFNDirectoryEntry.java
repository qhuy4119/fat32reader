import java.nio.charset.StandardCharsets;

public class LFNDirectoryEntry extends DirectoryEntry{
	public LFNDirectoryEntry(byte[] b){
		super(b);
	}

	@Override	
	protected void initFields(){
		fields.put("seqnumAndAllocationStatus", bytes.get());
		fields.put("char1To5Filename", getUTF16String(1, 10));
		fields.put("fileAttributes", bytes.get(11));
		fields.put("checksum", bytes.get(13));
		fields.put("char6To11Filename", getUTF16String(14, 12));
		fields.put("char12To13Filename", getUTF16String(28, 4));
	}

	@Override
	public String getFilename(){
		return (String)fields.get("char1To5Filename") + 
			(String)fields.get("char6To11Filename") + 
			(String)fields.get("char12To13Filename");
	}


	public boolean isLastLFN() {
		byte status = (Byte)fields.get("seqnumAndAllocationStatus");
		return status == (status | 0x40);
	}

	private String getUTF16String(int index, int size){
		byte[] buf = new byte[size];
		bytes.get(index, buf);
		return new String(buf, StandardCharsets.UTF_16LE);
	}

}
