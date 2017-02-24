package tcc.login.models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name ="Login")
public class LoginModel {
	private String ip;
	private int port;
	

	public String getIp() {
		return ip;
	}


	public void setIp(String ip) {
		this.ip = ip;
	}


	public int getPort() {
		return port;
	}


	public void setPort(int port) {
		this.port = port;
	}


	@Override
	public String toString() {
		return "login JSON";
	}
}
