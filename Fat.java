import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Fat{
	private String filePath;
	private int fatStartByte;
	private final byte BYTES_PER_ENTRY = 4;
	private final int EOF_MARKER = 0x0ffffff8;
	private final int DAMAGED_MARKER = 0x0ffffff7;
	private final byte FAT32_FIRST_CLUSTER_ADDRESS = 2;


	public Fat(String filePath, Map<String, Object> bootSectorFields){
		this.filePath = filePath;
		this.fatStartByte = (Short) bootSectorFields.get("numReservedSectors") * 
			(Short) bootSectorFields.get("bytesPerSector");
	}

	public int getEntry(int entryNumber) throws FileNotFoundException, IOException{
		try (var in = new DataInputStream(new FileInputStream(filePath))){
			in.skipBytes(fatStartByte + entryNumber*BYTES_PER_ENTRY);
			return in.readInt();
		}
	}

	public int[] getClusterChain(int startEntry) throws FileNotFoundException, IOException{
		ArrayList<Integer> clusterChain = new ArrayList<Integer>();
		clusterChain.add(startEntry);
		int currentEntry = startEntry;

		while ( true ){
			int nextCluster = getEntry(currentEntry);
			if (nextCluster >= EOF_MARKER || 
				nextCluster < FAT32_FIRST_CLUSTER_ADDRESS || 
				nextCluster == DAMAGED_MARKER){
				return clusterChain.stream().mapToInt(i -> i).toArray();
			}
			else{
				clusterChain.add(nextCluster);
				currentEntry = nextCluster;
			}
		}
	}
	public static void main(String[] args) throws IOException, FileNotFoundException {
		BootSector bs = new BootSector(args[0]);
		System.out.println(bs.getFields());
		Fat fat = new Fat(args[0], bs.getFields());
		System.out.println(Arrays.toString(fat.getClusterChain(Integer.parseInt(args[1]))));
		System.out.println((fat.getEntry(Integer.parseInt(args[1]))));
	}
}
