package tcc.sensor;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;

import org.json.JSONObject;

public class ClientMessageSender {

	/*public static void main(String[]args){
		String USERPATH = System.getProperty("user.home");
		String USER_FILE = USERPATH+"/.user-info/info.txt";	
		JSONObject json = getClient(USER_FILE);
		System.out.println(json);


		String ip = json.getString("ip");
		int port = json.getInt("port");

		String nodeInfo = "adfjdsaljfahsdhNODEEE";

		//sendClientNodeInfo(ip, port, nodeInfo);
	}*/

	public static JSONObject getClient(String filename) {
		// TODO Auto-generated method stub
		JSONObject json = null;
		String s = "";
		try {
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);

			s = br.readLine();

			br.close();
			fr.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		json = new JSONObject(s);

		return json;
	}

	public static void sendClientNodeInfo(String ip, int port, String nodeInfo) {

		Socket socket = null;
		DataOutputStream dataOutputStream = null;

		try {
			socket = new Socket(ip, port);
			dataOutputStream = new DataOutputStream(
					socket.getOutputStream());

			if(nodeInfo != null){
				dataOutputStream.writeUTF(nodeInfo);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (dataOutputStream != null) {
				try {
					dataOutputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

}
