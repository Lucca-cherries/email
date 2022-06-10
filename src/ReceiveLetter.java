package src;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;


public class ReceiveLetter {
	
	private int port=110; // POP3端口
	private String server; // pop3邮件服务器
	private String account; // 邮箱账户
	private String password; // 授权码
	
	private Socket socket;
	private BufferedReader socketIn;
	private BufferedWriter socketOut;
	
	private static BufferedReader stdIn;

	// 统一采用utf-8编码防止中文乱码现象
	static {
		try {
			stdIn = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private static PrintWriter stdOut;

	static {
		try {
			stdOut = new PrintWriter(new OutputStreamWriter(System.out, "UTF-8"),true);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private static PrintWriter stdErr;

	static {
		try {
			stdErr = new PrintWriter(new OutputStreamWriter(System.out, "UTF-8"),true);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	// Constructor
	public ReceiveLetter(String server, String account, String password) 
	{
		this.server = server;
		this.account = account;
		this.password = password;
	}

	// Getter and Setter
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	//初始化端口和输入输出流
	private boolean initialSocket()
	{
		boolean flag=true;
		
		if(server==null)
		{
			return false;
		}
		
		try 
		{
			// 建立socket连接
			socket=new Socket(server,port);
			// 创建输入输出流
			socketIn=new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			socketOut=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"));
		}
		catch(UnknownHostException e)
		{
			stdOut.println("未知主机错误");
			e.printStackTrace();
			flag=false;
		}
		catch (IOException e) {
			stdOut.println("IO错误");
			e.printStackTrace();
			flag=false;
		}
		return flag;
	}
	
	private boolean getMail()
	{
		try {
			String response;
			response=socketIn.readLine();
			
			stdOut.println(response);
			
			//输入用户名
			socketOut.write("user "+account+"\r\n");
			socketOut.flush();
			response=socketIn.readLine();
			stdOut.println(response);
			
			//输入密码
			socketOut.write("pass "+password+"\r\n");
			socketOut.flush();
			response=socketIn.readLine();
			stdOut.println(response);
			
			//请求列出所有邮件
			socketOut.write("stat\r\n");
			socketOut.flush();
			response=socketIn.readLine();
			stdOut.println(response);
		
			//这里获得邮件总数 e.g. +OK 22 54423
			StringTokenizer stringTokenizer=new StringTokenizer(response," ");
			stringTokenizer.nextToken();
			int num=Integer.parseInt(stringTokenizer.nextToken());
			
			//展示每一封邮件
			for (int i = 1; i <= num; i++) 
			{
				stdOut.println("第"+i+"封邮件内容");
				socketOut.write("retr "+i+"\r\n");
				socketOut.flush();
				
				while (true) 
				{
					String line=socketIn.readLine();
					stdOut.println(line);
					
					if(line.equals("."))
					{
						break;
					}
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  true;
	}

	/**
	 * 检查服务器返回的提示是否以"+OK"开头
	 * @param response 从socket中获得的服务器端返回信息
	 * @return 是，返回true，表示可以输入下一行命令，否则返回false，表示上一个指令需要重新输入
	 */
	private boolean checkResponse(String response){

		return true;
	}

	/**
	 * 接收邮件
	 * @return 接收成功返回true，接受失败返回false
	 */
	public boolean run()
	{
		initialSocket();
//		stdErr.println("server=" + server);
//		stdErr.println("account=" + account);
//		stdErr.println("password=" + password);
		getMail();
		return true;
	}

}
