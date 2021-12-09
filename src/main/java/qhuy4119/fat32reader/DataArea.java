package qhuy4119.fat32reader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class DataArea{
	private String filePath;
	private int startByte;
	private int bytesPerCluster;

	public DataArea(String filePath, Map<String, Object> bootSectorFields){
		this.filePath = filePath;
		startByte = ((short) bootSectorFields.get("numReservedSectors") + 
				(int) bootSectorFields.get("sectorsPerFat") * 
				(byte) bootSectorFields.get("numFats")) * 
			(short) bootSectorFields.get("bytesPerSector");
		bytesPerCluster = (byte) bootSectorFields.get("sectorsPerCluster") * 
			(short) bootSectorFields.get("bytesPerSector");
	}

	public byte[] getClusters(int[] clusterChain) throws IOException, FileNotFoundException{
		ArrayList<Byte> clusters = new ArrayList<Byte>();
		for (int clusterAddr : clusterChain){
			if (clusterAddr < 2){
				return new byte[0];
			}
			try (RandomAccessFile in = new RandomAccessFile(filePath, "r")){
				in.seek(startByte + (clusterAddr - 2)*bytesPerCluster);
				byte[] buf = new byte[bytesPerCluster];
				in.read(buf);
				for (byte b : buf){
					clusters.add(b);
				}
			}
		}
		byte[] result = new byte[clusters.size()];
		for (int i = 0; i < clusters.size(); i++){
			result[i] = clusters.get(i);
		}
		return result;
	}

	public static void main(String[] args) throws IOException, FileNotFoundException {
		BootSector bs = new BootSector(args[0]);
		System.out.println(bs.getFields());
		DataArea dataArea  = new DataArea(args[0], bs.getFields());
		Fat fat  = new Fat(args[0], bs.getFields());
		int[] clusterChain = fat.getClusterChain(Integer.parseInt(args[1]));
		System.out.println(Arrays.toString(dataArea.getClusters(clusterChain)));
	}
}


