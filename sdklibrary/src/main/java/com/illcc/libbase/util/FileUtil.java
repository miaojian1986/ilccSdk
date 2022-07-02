package com.illcc.libbase.util;


import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class FileUtil {




	public static File getSaveFile(Context context) {
		File file = new File(context.getFilesDir(), "pic.jpg");
		return file;
	}



	public static String getRootPath() {
		String root = "";
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			root = Environment.getExternalStorageDirectory().getPath();
		}else {
			root = Environment.getRootDirectory().getPath();
		}
		return root;
	}


	/**
	 * 获取指定文件大小
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static long getFileSize(File file) throws IOException
	{
		long size = 0;
		if (file.exists()){
			FileInputStream fis = null;
			fis = new FileInputStream(file);
			size = fis.available();
			fis.close();
		}
		else{
			file.createNewFile();
		}
		return size;
	}




	public interface CallBack{
		void finishDo(String path, String name);

	}
	public static void copyAssetFileToCache(String filename, Context context, CallBack callBack ){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					//下载路径，如果路径无效了，可换成你的下载路径
					String path = context.getCacheDir().getAbsolutePath();

					InputStream is =context.getAssets().open(filename);
					if (is == null) throw new RuntimeException("stream is null");
					File file1 = new File(path);
					if(!file1.exists()){
						file1.mkdirs();
					}
					//把数据存入路径+文件名
					FileOutputStream fos = new FileOutputStream(path+"/"+filename);
					byte buf[] = new byte[1024];

					do{
						//循环读取
						int numread = is.read(buf);
						if (numread == -1)
						{
							break;
						}
						fos.write(buf, 0, numread);
						//更新进度条
					} while (true);

					if(callBack!=null){
						callBack.finishDo("",filename);
					}

					is.close();
				} catch (Exception ex) {

				}
			}
		}).start();
	}


}
