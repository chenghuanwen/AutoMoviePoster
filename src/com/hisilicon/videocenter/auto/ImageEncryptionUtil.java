package com.hisilicon.videocenter.auto;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageEncryptionUtil {
	private Context mContext;
	private GetSataPathUtil pathUtil;
	private String unlockBase ;
	private Bitmap  bitmap;
	
	public ImageEncryptionUtil(Context mContext) {
		this.mContext = mContext;
		pathUtil = new GetSataPathUtil(mContext);
		unlockBase = pathUtil.getSataPath()+"/JSLMovie/unlock/";
	}

	private  void unlock(String string, String string2) {
		// TODO Auto-generated method stub
		try {
			FileInputStream in = new FileInputStream(new File(string));
			File unlock = new File(string2);
			if(!unlock.exists())
				unlock.createNewFile();
			FileOutputStream out = new FileOutputStream(unlock);
			
			int available = in.available();
			byte[] buffer = new byte[available];
			int read = in.read(buffer);
			if(read != -1){
				byte data = (byte) (available&0x7F);
				for (int i = 0; i <available ; i++) {
					buffer[i] ^= ((data+i)^i)&0x7F; 
				}
				
			}			
			out.write(buffer);
			
			
			
			
			/*int b = 0;
			while((b=in.read()) != -1 ){
				out.write(b+1);
			}*/
			out.flush();
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void lock(String string, String string2) {
		// TODO Auto-generated method stub
		try {
			FileInputStream in = new FileInputStream(new File(string));
			File unlock = new File(string2);
			if(!unlock.exists())
				unlock.createNewFile();
			FileOutputStream out = new FileOutputStream(unlock);
			int b = 0;
			while((b=in.read()) != -1 ){
				out.write(b-1);
			}
			out.flush();
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public  void unlock(String path,OnUnLockImageFinishedListener listener) {
		// TODO Auto-generated method stub
		try {
			File file = new File(path);
			if(!file.exists()){
				listener.onUnlockFinish(null);
				return ;
			}
				
			FileInputStream in = new FileInputStream(file);
			File unlock = new File(unlockBase+file.getName());
			/*if(unlock.exists())
				unlock.delete();
				unlock.createNewFile();
			FileOutputStream out = new FileOutputStream(unlock);*/
			int available = in.available();
			byte[] buffer = new byte[available];
			int read = in.read(buffer);
			if(read != -1){
				byte data = (byte) (available&0x7F);
				for (int i = 0; i <available ; i++) {
					buffer[i] ^= ((data+i)^i)&0x7F; 
				}
				
			}			
			
		bitmap =null;
		InputStream input = new ByteArrayInputStream(buffer);
		bitmap = BitmapFactory.decodeStream(input);
		//bitmap= BitmapFactory.decodeByteArray(buffer, 0,buffer.length);
			
			
		//	out.write(buffer);
		
			//out.flush();
			input.close();
			in.close();
			input=null;
			in = null;
			//out.close();
			listener.onUnlockFinish(bitmap);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			listener.onUnlockFinish(null);
		}
		
		
	}
	
}
