package com.systek.guide.common.utils;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

public class ImageLoaderUtil {
	private static ImageLoaderConfiguration configuration;
	private static ImageLoaderConfiguration newConfiguration(Context context)
	{
		if (configuration==null)
		{
			configuration=new ImageLoaderConfiguration.Builder(context).diskCacheSize(1024*1024*100).diskCacheFileCount(100).build();
		}
		return configuration;
	}
	public static void displayNetworkImage(Context context,final String imageUrl,final ImageView imageView)
	{
		try {
			ImageLoader imageLoader=ImageLoader.getInstance();
			configuration=newConfiguration(context);
			imageLoader.init(configuration);
			imageLoader.displayImage(imageUrl, imageView);
			imageLoader.displayImage(imageUrl, imageView, new ImageLoadingListener() {
				
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onLoadingFailed(String imageUri, View view,
						FailReason failReason) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					// TODO 此处可存储图片至本地sdcard
				}
				
				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					// TODO Auto-generated method stub
					
				}
			});
		} catch (Exception e) {
			ExceptionUtil.handleException(e);
		}
	}
	public static void displaySdcardImage(Context context, String filePathName,
			ImageView ivImage) {
		ImageLoader imageLoader=ImageLoader.getInstance();
		configuration=newConfiguration(context);
		imageLoader.init(configuration);
		imageLoader.displayImage("file:///"+filePathName, ivImage);
	}

}
