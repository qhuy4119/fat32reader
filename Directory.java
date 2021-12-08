import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class Directory extends File {
	private Directory parentDir;
	private int addrFirstCluster;

	public Directory(DirectoryEntry dirEntry, LFNDirectoryEntry[] lfnEntries,
			BootSector bootSector, Fat fat, DataArea dataArea, Directory parentDir) {
		super(dirEntry, lfnEntries, bootSector, fat, dataArea);
		this.parentDir = parentDir;
		if (dirEntry == null) {
			addrFirstCluster = (Integer) bootSector.getFields().get("rootDirStartCluster");
		}
		else {
			addrFirstCluster = dirEntry.getAddrFirstCluster();
		}
	}	

	@Override
	public String getFilename(){
		if (dirEntry == null) {
			return java.io.File.separator;
		}
		else {
			return super.getFilename();
		}
	}
	public String getFullPath() {
		Directory current = this;
		Path fullPath = Paths.get(getFilename());
		while (current.parentDir != null) {
			fullPath = Paths.get(current.parentDir.getFilename(), fullPath.toString());
			current = current.parentDir;
		}
		return fullPath.toString();
	}

	public DirectoryEntry[] getEntries() throws FileNotFoundException, IOException{
		int[] clusterChain = fat.getClusterChain(addrFirstCluster);
		byte[] clusters = dataArea.getClusters(clusterChain);
		int i = 0;
		ArrayList<DirectoryEntry> entries = new ArrayList<DirectoryEntry>();
		while (i < clusters.length) {
			byte[] entryData = Arrays.copyOfRange(clusters, i, i+32);
			if ((entryData[0] != 0xe5) && (entryData[0] != 0x00)) {
				if (entryData[11] == 0x0f) {
					entries.add(new LFNDirectoryEntry(entryData));
				}
				else {
					entries.add(new DirectoryEntry(entryData));
				}
			}
			i += 32;
		}
		return entries.toArray(new DirectoryEntry[0]);
	}

	public File[] getFiles() throws IOException{
		ArrayList<File> files = new ArrayList<File>();
		int i = 0;
		DirectoryEntry[] entries = getEntries();
		LFNDirectoryEntry[] lfnEntries = new LFNDirectoryEntry[0];
		while (i < entries.length) {
			if (entries[i].getClass() == LFNDirectoryEntry.class){
				LFNDirectoryEntry entry = (LFNDirectoryEntry) entries[i];
				if (entry.isLastLFN()) {
					byte mask = 0x40;
					byte statusByte = (byte) entry.getFields().get("seqnumAndAllocationStatus");
					int numOfLFNs = statusByte - mask;
					lfnEntries = new LFNDirectoryEntry[numOfLFNs];
					for (int j = 0; j < numOfLFNs; j++) {
						 lfnEntries[j] = new LFNDirectoryEntry(entries[i + numOfLFNs - j - 1]);
					}
					i += numOfLFNs;
				}
			}
			else {
				
				if (isEntryForDirectory(entries[i])) {
					files.add(new Directory(entries[i], lfnEntries, bootSector, fat, dataArea, this));
				}
				else {
					files.add(new File(entries[i], lfnEntries, bootSector, fat, dataArea));
				}
				lfnEntries = new LFNDirectoryEntry[0];
				i += 1;
			}
		}
		return files.toArray(new File[0]);
	}

	public boolean isDirTreeRoot() {
		return dirEntry == null;
	}
	public Directory getParentDir() {
		return parentDir;
	}
	private boolean isEntryForDirectory(DirectoryEntry dirEntry) {
		if (dirEntry == null) {
			return true;
		}
		byte fileAttributes = (Byte)dirEntry.getFields().get("fileAttributes");
		return (fileAttributes & 0x10) != 0;
	}

	public static void main(String[] args) throws IOException, FileNotFoundException {
		BootSector bs = new BootSector(args[0]);
		// System.out.println(bs.getFields());
		Fat fat  = new Fat(args[0], bs.getFields());
		DataArea dataArea  = new DataArea(args[0], bs.getFields());
		Directory dir = new Directory(null, null, bs, fat, dataArea, null);
		File[] files = dir.getFiles();
		for (File f : files) {
			String filename = f.getFilename();
			System.out.println(filename);
		}
	}

}
