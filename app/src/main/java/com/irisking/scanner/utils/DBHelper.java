package com.irisking.scanner.utils;

import android.content.Context;
import android.util.Log;

import com.irisking.scanner.bean.IrisUserInfo;
import com.irisking.scanner.database.SqliteDataBase;

import java.util.List;

/**
 * Created by Qicj on 2017/10/16 0016.
 */

public class DBHelper {
	
	/**
	 * 获取最小用户缺省值
	 *
	 * @param context
	 * @return
	 */
	public static int getMinimalDefaultValue(Context context) {
		List<IrisUserInfo> usersExists = SqliteDataBase.getInstance(context)
				.queryAll();
		if (usersExists == null || usersExists.size() == 0) {
			return 0;
		}
		int minDefault = -1;
		for (int i = 0; i < 5; i++) {
			boolean hasUser = false;
			for (IrisUserInfo user : usersExists) {
				if (user.m_UserFavicon == i) {
					hasUser = true;
					break;
				}
			}
			if (!hasUser) {
				minDefault = i;
				break;
			}
		}
		return minDefault;
	}
	
	/**
	 * 获取最小用户缺省值
	 *
	 * @return
	 */
	public static int getMinimalDefaultValue(List<IrisUserInfo> users) {
		if (users == null || users.size() == 0) {
			return 0;
		}
		int minDefault = -1;
		for (int i = 0; i < 6; i++) {
			boolean hasUser = false;
			for (IrisUserInfo user : users) {
				Log.i("tony", "default: " + user.m_UserFavicon);
				if (user.m_UserFavicon == i) {
					hasUser = true;
					break;
				}
			}
			if (!hasUser) {
				minDefault = i;
				break;
			}
		}
		return minDefault;
	}
	
	public byte[] AllUserLeftFeature(List<IrisUserInfo> users) {
		if (users == null || users.size() == 0) {
			return null;
		}
		byte[] leftFeature = new byte[users.size() * 9];
		int index = 0;
		for (IrisUserInfo user : users) {
			byte[] leftPerUser = user.m_LeftTemplate;
			for (byte leftUnit : leftPerUser) {
				leftFeature[index] = leftUnit;
				index++;
			}
		}
		return leftFeature;
	}
	
	public byte[] AllUserRightFeature(List<IrisUserInfo> users) {
		if (users == null || users.size() == 0) {
			return null;
		}
		byte[] rightFeature = new byte[users.size() * 9];
		int index = 0;
		for (IrisUserInfo user : users) {
			byte[] rightPerUser = user.m_RightTemplate;
			for (byte leftUnit : rightPerUser) {
				rightFeature[index] = leftUnit;
				index++;
			}
		}
		return rightFeature;
	}
	
}
