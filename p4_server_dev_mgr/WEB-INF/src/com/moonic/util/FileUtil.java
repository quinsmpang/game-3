package com.moonic.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 文件工具集
 */
public class FileUtil {
	
	/**
	 * 追加内容到TXT
	 */
	public void addToTxt(String savepath, String content){
		writeToTxt(savepath, content, true);
	}
	
	/**
	 * 写入新内容到TXT(原内容被删除)
	 */
	public void writeNewToTxt(String savepath, String content){
		writeToTxt(savepath, content, false);
	}
	
	/**
	 * 写入内容到TXT
	 * @param addto 是否追加内容到文件
	 */
	private void writeToTxt(String savepath, String content, boolean addto){
		OutputStream os = null;
		try {
			String dirPath = savepath.substring(0, savepath.lastIndexOf("/")+1);
			File dir = new File(dirPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			boolean firstwrite = false;
			String filePath = savepath;
			File file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
				firstwrite = true;
			}
			os = new FileOutputStream(file, addto);
			if(firstwrite || !addto){
				byte[] head = new byte[]{(byte)0xef, (byte)0xbb, (byte)0xbf};
				os.write(head);	
			}
			StringBuffer sb = new StringBuffer();
			sb.append(content);
			os.write(sb.toString().getBytes("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(os != null){
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 删除文件夹
	 */
	public boolean deleteDirectory(String sPath) {
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} // 删除子目录
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		// 删除当前目录
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 删除文件
	 */
	public boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}
	
	/**
	 * 存文件
	 */
	public void save(String savepath, byte[] filedata) throws Exception {
		File savedir = new File(savepath.substring(0, savepath.lastIndexOf("/")+1));
		if(!savedir.exists()){
			savedir.mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(savepath);
		fos.write(filedata);
		fos.close();
	}
	
	/**
	 * 文件另存为
	 */
	public void saveAs(String sourcepath, String savepath) {
		try {
			File savedir = new File(savepath.substring(0, savepath.lastIndexOf("/")+1));
			if(!savedir.exists()){
				savedir.mkdirs();
			}
			FileInputStream fis = new FileInputStream(sourcepath);
			FileOutputStream fos = new FileOutputStream(savepath);
			byte[] data = new byte[1024];
			int len = 0;
			while((len=fis.read(data))!=-1){
				fos.write(data, 0, len);
			}
			fis.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		FileUtil fileUtil = new FileUtil();
		fileUtil.saveAs("F:\\1\\a.jpg", "F:\\1\\b.jpg");
	}
}
