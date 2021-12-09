package qhuy4119.fat32reader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class DirectoryTree {
	private Directory currentDir;

	public DirectoryTree(Directory root) {
		this.currentDir = root;
	}

	public String[] getCurrentDirContent() throws IOException{
		File[] files = currentDir.getFiles();
		String[] filenames = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			filenames[i] = files[i].getFilename();
		}
		return filenames;
	}

	public int changeDirectory(String dirName) throws IOException {
		switch (dirName) {
			case "..":
				if (!currentDir.isDirTreeRoot()) {
					currentDir = currentDir.getParentDir();
					return 0;
				}
			case ".":
				return 0;
			default:
				for (File f : currentDir.getFiles()) {
					if (f.getFilename().equals(dirName)) {
						if (f.getClass() == Directory.class) {
							currentDir = (Directory) f;
							return 0;
						}

					}
				}
				System.out.printf("Directory %s doesn't exist\n", dirName);
				return 1;

		}
	}

	public Directory getCurrentDir() {
		return currentDir;
	}

	public static void main(String[] args) throws IOException, FileNotFoundException {
		BootSector bs = new BootSector(args[0]);
		Fat fat  = new Fat(args[0], bs.getFields());
		DataArea dataArea  = new DataArea(args[0], bs.getFields());
		Directory dir = new Directory(null, null, bs, fat, dataArea, null);
		DirectoryTree tree = new DirectoryTree(dir);
		System.out.println(Arrays.toString(tree.getCurrentDirContent()));
		tree.changeDirectory("books");
		System.out.println(Arrays.toString(tree.getCurrentDirContent()));


	}
}
