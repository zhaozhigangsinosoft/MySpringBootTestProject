package cn.util;

import java.io.File;
import java.util.ArrayList;

public class FileUtils {
	public static ArrayList<File> getFiles(String path,boolean scanSub) throws Exception {
		ArrayList<File> fileList = new ArrayList<File>();
		File file = new File(path);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File fileIndex : files) {
				// 如果这个文件是目录，则进行递归搜索
				if (fileIndex.isDirectory()) {
					if(scanSub) {
						fileList.addAll(getFiles(fileIndex.getPath(),true));
					}
				} else {
					// 如果文件是普通文件，则将文件句柄放入集合中
					fileList.add(fileIndex);
				}
			}
		}
		return fileList;
	}
}
