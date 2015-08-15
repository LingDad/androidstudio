package net.jianweixu.listennews;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.os.Environment;
import android.util.Log;

public class FileUtils {
	private static DownloadSizeListener mDownloadSizeListener;
	private static CircleProgressBarListener mCircleProgressBarListener;
	private String SDPATH;
	private String TAG = "FILEUTILS";
	private int downloadSize = 0;
	
	public String getSDPATH(){
		Log.e(TAG, "SDPATH IS " + SDPATH);
		return SDPATH;
	}
	
	//constructor
	public FileUtils(){
		SDPATH = Environment.getExternalStorageDirectory() + "/";
	}
	
	public File createSDFile(String fileName) throws IOException{
		File file = new File(SDPATH + fileName);
		file.createNewFile();
		return file;
	}
	
	public File createSDDir(String dirName){
		File dir = new File(SDPATH + dirName);
		dir.mkdir();
		return dir;
	}
	
	public boolean isDirExist(String dir){
		File file = new File(dir);
		return file.exists();
		
	}
	
	public boolean isFileExist(String fileName){
		File file = new File(SDPATH + fileName);
		return file.exists();
	}
	
	public File writeToSDFromInput(String path, String fileName, InputStream input){
		File file = null;
		OutputStream output = null;
		try{
			createSDDir(path);
			file =createSDFile(path + fileName);
			output = new FileOutputStream(file);
			byte buffer[] = new byte[400 * 1024];
			int length;
			long startTime = System.currentTimeMillis();
			while((length=(input.read(buffer))) > 0){
				Log.e(TAG, "length is: " + length);
				downloadSize+=length;
				output.write(buffer, 0, length);
			}
			long endTime = System.currentTimeMillis();
			long usedTime = endTime - startTime;
			if(mDownloadSizeListener!=null){
				Log.e(TAG,"usedTime is: " + usedTime + ". file name is: " + fileName );
				mDownloadSizeListener.OnDownloadSizeChange(downloadSize, usedTime);
			}
			if(mCircleProgressBarListener!=null){
				mCircleProgressBarListener.OnCircleProgressBarUpdate(fileName);
			}
			Log.e(TAG, "download size is: " + downloadSize);
			output.flush();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				output.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return file;
	}
	
	public int getDownloadSize(){
		return downloadSize;
	}
	
	public void setOnDownloadSizeListener(DownloadSizeListener listener){
		Log.e(TAG, "set on download size listener!!!");
		mDownloadSizeListener = listener;
	}
	
	public void setOnCircleProgressBarListener(CircleProgressBarListener listener){
		Log.e(TAG, "set on progressbar listener!!!");
		mCircleProgressBarListener = listener;
	}
}
