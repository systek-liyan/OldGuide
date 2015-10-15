package com.systek.guide.receiver;

import com.systek.guide.common.utils.NetworkUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkStateChangedReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		NetworkUtil.checkNet(context);
	}
}
