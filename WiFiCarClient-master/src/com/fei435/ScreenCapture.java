package com.fei435;

import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import com.fei435.FileUtils;
import com.fei435.FileUtils.NoSdcardException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//ÎÄ¼þËø
import java.nio.channels.FileLock;
import java.nio.channels.FileChannel;
import java.io.RandomAccessFile;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.googlecode.javacv.FFmpegFrameRecorder;
import com.googlecode.javacv.FrameRecorder.Exception;
import com.googlecode.javacv.cpp.opencv_core;
/**
 * convertFromBitmaptoVideo
 * @author yanjiaqi  qq:985202568
 * modified by feifei435
 */

public class ScreenCapture {
	private static int switcher = 0;//Â¼Ïñ¼ü
	private static boolean isPaused = false;//ÔÝÍ£¼ü
	private static double RECORD_FPS = 10f;
	
	private static String video_path_name = null;
	
	
	//½ØÈ¡ÊÓÆµÒ»Ö¡²¢±£´æ  ×¢Òâ bitNameÎªÂ·¾¶+ÎÄ¼þÃû
    public static int saveBitmapToFile(Bitmap mBitmap, String bitName){
    	FileOutputStream fOut = null;
    	Log.i("ScreenCapture", "saveBitmapToFile enter");
    	if (null == bitName || bitName.length() <= 4) {
    		return Constant.CAM_RES_FAIL_FILE_NAME_ERROR;
    	}
    	
    	File f = new File(bitName);
    	Log.i("ScreenCapture", "saveBitmapToFile, fname =" + f);
    	try {
	    	f.createNewFile();
	    	Log.i("ScreenCapture", "saveBitmapToFile, createNewFile success, f=" + f);
	    	fOut = new FileOutputStream(f);
	    	Log.i("ScreenCapture", "saveBitmapToFile, FileOutputStream success, fOut=" + fOut);
    	} catch (IOException e) {
    		Log.i("ScreenCapture", "exception, err=" + e.getMessage());
    		return Constant.CAM_RES_FAIL_FILE_WRITE_ERROR;
    	}
    	
    	mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
    	
    	try {
    		fOut.flush();
    		fOut.close();
    	} catch (IOException e) {
    		e.printStackTrace();
    		return Constant.CAM_RES_FAIL_BITMAP_ERROR;
    	}
    	
    	return Constant.CAM_RES_OK;
    }
	

	public static void start(){
		
		video_path_name = FileUtils.generateFileName("VID_");
		switcher = 1;
		
		new Thread(){
			public void run(){
				Log.i("ScreenCapture", "ScreenCaptureÏß³ÌÒÑÆô¶¯");
				try {
					new FileUtils().creatSDDir(FileUtils.FILE_PATH);
				
					//TODO:¿ÉÑ¡µÄ·½°¸£º°ÑÕâÀïµÄ640 480¸ÄÎªÏÈ¶ÁÈ¡assetsÖÐµÄÊ¾ÀýÍ¼Æ¬µÄ¿í¸ß£¬Ò²¿ÉÒÔÈ·¶¨Â¼Ïñ²ÎÊý
					FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(
							video_path_name, 640, 480);
					Log.i("ScreenCapture", "recorderÒÑ´´½¨£¬"+"width:"+recorder.getImageHeight()+"height:"+recorder.getImageHeight());
			
					recorder.setFormat("mp4");
					recorder.setFrameRate(RECORD_FPS);//Â¼ÏñÖ¡ÂÊ
					recorder.start();
				
					while(switcher!=0){
						if(!isPaused){
							
							//TODO:ÅÐ¶ÏÊÇ·ñ¸úÉÏÒ»Ö¡ÖØ¸´  ±£Ö¤Ö¡ÂÊ
							//ËäÈ»ÕâÀïÓÉcvLoadImageÖ±½Ó¸ù¾ÝÂ·¾¶¶ÁÈ¡Í¼Ïñ£¬µ«ÊÇÎªÁËÊ¹ÓÃjavaÖÐÎÄ¼þËø±£³Ö»¥³â£¬»¹ÊÇÒª¶¨ÒåÒ»¸öFile¶ÔÏó
							
						    if(!FileUtils.frameFileLocked) {
						        if(new FileUtils().isFileExist(FileUtils.TMP_FRAME_NAME, FileUtils.FILE_PATH)){
						        	FileUtils.frameFileLocked = true;//¼ÓËø
							        Log.i("filelock", "recorder:ÒÑ½«"+FileUtils.TMP_FRAME_NAME+"¼ÓËø");
							        
						        	FileUtils.frameFileLocked = true;//¼ÓËø
							        Log.i("filelock", "recorder:ÒÑ½«"+FileUtils.TMP_FRAME_NAME+"¼ÓËø");
						        	
							        opencv_core.IplImage image = cvLoadImage(new FileUtils().getSDCardRoot()+ FileUtils.FILE_PATH + File.separator+FileUtils.TMP_FRAME_NAME);
									Log.i("ScreenCapture", "recorderÕýÔÚ½«Ö¡"+System.currentTimeMillis()+"±£´æµ½MP4ÎÄ¼þ");
									recorder.record(image);
									
									//½âËøÎÄ¼þ
							        FileUtils.frameFileLocked = false;
							        Log.i("filelock", "recorder:ÒÑ½«"+FileUtils.TMP_FRAME_NAME+"½âËø");
							        
							        try {//Â¼ÍêÒ»Ö¡ÐÝÏ¢Ò»ÏÂ
										sleep(200);
										Log.i("filelock", "recorder:sleep some time");
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
							        
						        } else {
						        	Log.i("ScreenCapture", "µÈ´ýtmpframe.jpg");
						        	try {
										sleep(200);
										Log.i("filelock", "recorder:sleep some time");
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
						    } else {
						        //MjpegViewÏß³ÌÕýÔÚÐ´jpg£¬·ÅÆú¶Á
						    	Log.i("ScreenCapture", "MjpegViewÏß³ÌÕýÔÚÐ´"+FileUtils.TMP_FRAME_NAME+",·ÅÆú±£´æÍ¼Ïñ");
						    	try {
									sleep(200);
									Log.i("filelock", "recorder:sleep some time");
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
						    }
						}
					}
					recorder.stop();
					
					Log.i("ScreenCapture", "recorderÒÑÍ£Ö¹");
				}catch(FileUtils.NoSdcardException e){
					e.printStackTrace();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	//·µ»ØÖµÎªÊÓÆµÂ·¾¶
	public static String stop(){
		switcher = 0;
		isPaused = false;
		return video_path_name;
	}
	public static void pause(){
		if(switcher==1){
			isPaused = true;
		}
	}
	public static void restart(){
		if(switcher==1){
			isPaused = false;
		}
	}
	public static boolean isStarted(){
		if(switcher==1){
			return true;
		}else{
			return false;
		}
	}
	public static boolean isPaused(){
		return isPaused;
	}
		
	private static Bitmap getImageFromFile(String filename){
		Bitmap image = null;
		try{
			image = BitmapFactory.decodeFile(
					new FileUtils().getSDCardRoot() + 
					FileUtils.FILE_PATH + File.separator + filename
					);
		}catch (NoSdcardException e) {
			e.printStackTrace();
		}
		return image;
	}
}

