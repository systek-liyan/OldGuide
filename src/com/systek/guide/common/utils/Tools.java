package com.systek.guide.common.utils;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;

public class Tools {
	
	 public static  byte[] BitmapToBytes(Bitmap bm) {
		          ByteArrayOutputStream baos = new ByteArrayOutputStream();
		          bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		         return baos.toByteArray();
		      }
}
