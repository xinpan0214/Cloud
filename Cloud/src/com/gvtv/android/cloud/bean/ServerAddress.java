package com.gvtv.android.cloud.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class ServerAddress implements Parcelable{
	private String serverIP;
	private int serverPort;
	private int serverUDPPort;

	public ServerAddress() {
	}
	
	public ServerAddress(String serverIP, int serverPort){
		this.serverIP = serverIP;
		this.serverPort = serverPort;
	}
	

	public String getserverIP() {
		return serverIP;
	}
	public void setserverIP(String serverIP) {
		this.serverIP = serverIP;
	}

	public int getserverPort() {
		return serverPort;
	}
	public void setserverPort(int serverPort) {
		if(serverPort < 0){
			this.serverPort = (short)serverPort & 0x0FFFF;
		}else{
			this.serverPort = serverPort;
		}
	}
	
	/**
	 * @return the serverUDPPort
	 */
	public int getServerUDPPort() {
		return serverUDPPort;
	}

	/**
	 * @param serverUDPPort the serverUDPPort to set
	 */
	public void setServerUDPPort(int serverUDPPort) {
		if(serverUDPPort < 0){
			this.serverUDPPort = (short)serverUDPPort & 0x0FFFF;
		}else{
			this.serverUDPPort = serverUDPPort;
		}
	}

	public ServerAddress(ServerAddress item){
		serverIP = item.serverIP;
		serverPort = item.serverPort;
		serverUDPPort = item.serverUDPPort;
	}
	
	@Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(serverIP);
        out.writeInt(serverPort);
        out.writeInt(serverUDPPort);
    }

    public static final Parcelable.Creator<ServerAddress> CREATOR
            = new Parcelable.Creator<ServerAddress>() {
        public ServerAddress createFromParcel(Parcel in) {
            return new ServerAddress(in);
        }

        public ServerAddress[] newArray(int size) {
            return new ServerAddress[size];
        }
    };
    
    @Override
	public int describeContents() {
		return 0;
	}
    
	private ServerAddress(Parcel in) {
    	serverIP = in.readString();
    	serverPort = in.readInt();
    	serverUDPPort = in.readInt();
    }

}
