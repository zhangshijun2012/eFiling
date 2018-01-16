package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
public class Test {
	public static void main(String[] args) {
		try {
			String txt = "01790110    5016010000002327              1601072219412016010700017622045200112832105653063000224                                                                      2723270C1301171F";
			Socket socket = new Socket("10.132.1.75", 8191); // 服务端的IP和端口
			OutputStream out = socket.getOutputStream();

			System.out.println(txt);
			out.write(txt.getBytes("GBK"));
			out.flush();

			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "GBK"));

			String line = null;
			while ((line = in.readLine()) != null) {
				System.out.println(line);
			}

			out.close();
			in.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}
	}
}
