package com.hisilicon.videocenter.auto;


import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtil {
	private static SharedPreferenceUtil instance;
	private static SharedPreferences sp;
	private SharedPreferenceUtil(){}
	public static SharedPreferenceUtil getInstance(Context context){
		if(instance == null){
		sp = context.getSharedPreferences("path", Context.MODE_MULTI_PROCESS);
		instance = new SharedPreferenceUtil();
		}
		
		return instance;
	}

	
	
	public void put(String key, Set value) {
		sp.edit()
		.clear()
		.putStringSet(key, value)
		.commit();
	}
	
	public void putNum(int value){
		sp.edit()
		.putInt("num",value)
		.commit();
	}
	
	public void putString(String key,String value){
		sp.edit()
		.putString(key, value)
		.commit();
	}
	
	public Set get(String key) {
		  return sp.getStringSet(key, null);                                                  
	}
	
	public int getNum(){
		return sp.getInt("num", -1);
	}
	
	public String getSataPath(){
		return sp.getString("sata", "");
	}

	public void clear(){
		sp.edit().clear().commit();
	}
}
