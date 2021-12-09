package fat32reader;

import java.nio.file.Path;
import java.nio.file.Paths;

public class File {
	protected DirectoryEntry dirEntry;
	protected LFNDirectoryEntry[] lfnEntries;
	protected BootSector bootSector;
	protected Fat fat;
	protected DataArea dataArea;
	protected boolean isDirTreeRoot;

	public File(DirectoryEntry dirEntry, LFNDirectoryEntry[] lfnEntries, 
			BootSector bootSector, Fat fat, DataArea dataArea) {
		this.dirEntry = dirEntry;
		this.lfnEntries = lfnEntries;
		this.bootSector = bootSector;
		this.fat = fat;
		this.dataArea = dataArea;
	}

	public String getFilename() {
		Path filename = Paths.get("");
		if (lfnEntries.length > 0) {
			for (LFNDirectoryEntry lfnEntry : lfnEntries) {
				filename = Paths.get(filename.toString(), lfnEntry.getFilename());
			}
		}
		else {
			filename = Paths.get(dirEntry.getFilename());
		}
		return filename.toString();

	}
}

