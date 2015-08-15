package net.jianweixu.listennews;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;




import android.util.Log;

public class HttpDownloader {
	
	private String TAG = "HTTPDOWNLOADER";
	private URL url = null;
	private InputStream inputStream;
	
	public void downloadFile(String url, String path, String fileName){
		try{
			FileUtils fileUtils = new FileUtils();
			if(fileUtils.isFileExist(path + fileName)){
				Log.e(TAG, "File exists!");
			}
			else{
				inputStream = getInputStreamFromURL(url);
				File resultFile = fileUtils.writeToSDFromInput(path, fileName, inputStream);
				if(resultFile != null){
					Log.e(TAG, "Start to call OnCircleProgressBarUpdate!");
					
					Log.e(TAG, "Download file successfull!");
				}else{
					Log.e(TAG, "Download file fail!");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			Log.e(TAG, "Exception: Download file fail!");
		}finally{
			try{
				inputStream.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public InputStream getInputStreamFromURL(String urlStr){
		HttpURLConnection urlConnection = null;
		try{
			url = new URL(urlStr);
			Log.e(TAG, "connect url!");
			urlConnection = (HttpURLConnection) url.openConnection();
			inputStream = urlConnection.getInputStream();
		}catch(MalformedURLException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		return inputStream;
	}
	
	
}

