package com.systek.guide.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.graphics.Bitmap;

public class Tools {
	
	 public static  byte[] BitmapToBytes(Bitmap bm) {
		          ByteArrayOutputStream baos = new ByteArrayOutputStream();
		          bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		         return baos.toByteArray();
		      }
	 public static void createOrCheckFolder(String path) {
			File mPath = new File(path);
			if (!mPath.exists()) {
				mPath.mkdirs();
			}
	 }
}
