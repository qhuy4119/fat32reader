import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

public class Fat32Reader{
	private BootSector bootSector;
	private Fat fat;
	private DataArea dataArea;
	private DirectoryTree dirTree;

	public Fat32Reader(String filesystemPath) throws IOException {
		bootSector = new BootSector(filesystemPath);
		fat = new Fat(filesystemPath, bootSector.getFields());
		dataArea = new DataArea(filesystemPath, bootSector.getFields());
		dirTree = new DirectoryTree(new Directory(null, null, bootSector, fat, dataArea, null));

	}

	public int changeDirectory(String dirName) throws IOException {
		return dirTree.changeDirectory(dirName);
	}

	public String[] getCurrentDirContent() throws IOException {
		return dirTree.getCurrentDirContent();
	}

	public Map<String, Object> getFileSystemInfo(){
		return bootSector.getFields();
	}

	public String getNameCurrentWorkingDirectory() {
		return dirTree.getCurrentDir().getFullPath();
	}

	public void printHelp() {
		String fmt = "%15s: %s\n";
		System.out.println("List of commands:");
		System.out.printf(fmt, "help", "print this help ");
		System.out.printf(fmt, "ls", "print contents of current directory");
		System.out.printf(fmt, "cd <dirName>", "change working directory into dirName");
		System.out.printf(fmt, "fsinfo", "print metadata about the filesystem");
		System.out.printf(fmt, "pwd", "print name of current working directory");
		System.out.printf(fmt, "q", "quit the program");
	}


	public void cli() throws IOException{
		System.out.println("Type help for list of available commands");
		try (Scanner in = new Scanner(System.in)){
			while (true) {
				System.out.print(">>>>");
				String[] userInput = in.nextLine().split(" ");
				String cmd = userInput[0];
				String arg = userInput.length > 1 ? userInput[1] : "";

				switch (cmd){
					case "help":
						printHelp();
						break;
					case "ls":
						System.out.println(Arrays.toString(getCurrentDirContent()));
						break;
					case "cd":
						changeDirectory(arg);
						break;
					case "fsinfo":
						System.out.println(getFileSystemInfo());
						break;
					case "pwd":
						System.out.println(getNameCurrentWorkingDirectory());
						break;
					case "q":
						return;
					default:
						System.out.printf("Command %s not found\n", cmd);
						printHelp();
				}

				
			}

		}
		
	}

	public static void main(String[] args) throws IOException, FileNotFoundException {
		String filesystemPath = args[0];
		Fat32Reader fat32Reader = new Fat32Reader(filesystemPath);
		fat32Reader.cli();
	}
}
