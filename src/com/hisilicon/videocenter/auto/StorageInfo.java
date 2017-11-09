package com.hisilicon.videocenter.auto;

public class StorageInfo {
	 public String path;  
	    public String state;  
	     public boolean isRemoveable;  
	   public StorageInfo(String path) {  
	        this.path = path;  
	   }  
	    public boolean isMounted() {  
	         return "mounted".equals(state);  
	    }  
	    
	    
	    
	  public String getPath() {
			return path;
		}
		public void setPath(String path) {
			this.path = path;
		}
		public String getName() {
			return path.split("\\/")[2];
		}
		
	@Override  
	    public String toString() {  
       return "StorageInfo [path=" + path + ", state=" + state  
	               + ", isRemoveable=" + isRemoveable + "]";  
	    }  

}
