package tcc.login;

import java.io.BufferedWriter;
import java.io.FileWriter;

import org.json.JSONObject;

public class LoginCreator {

	
	
	/*public static void main(String[]args){
		String ip = "192.168.1.32";
		int port = 8080;
		String USERPATH = System.getProperty("user.home");
		String USER_FILE = USERPATH+"/user-info/info.txt";	
		saveLogin(ip, port, USER_FILE);
	}*/
	
	public static void saveLogin(String ip, int port, String filename){
		JSONObject json = new JSONObject();
		json.put("ip", ip);
		json.put("port", port);
		
		String login = json.toString();
		
		FileWriter fw;
		try {
			fw = new FileWriter(filename);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(login);
			bw.close();
			fw.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
