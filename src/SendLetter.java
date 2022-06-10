package src;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.*;

public class SendLetter {
	
	private String sender;//发送方账户
	private String receiver;//接收方账户
	private String password;//发送账户密码
	private int port = 25;//SMTP使用端口25
	private String mailServer;//邮箱服务器
	private String content;//信件内容
	private String subject;// 邮件主题
	
	//控制台输入输出
	private static BufferedReader stdIn;

	// 统一采用utf-8编码防止中文乱码
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

	//socket
	private Socket socket; // socket对象，用于建立socket连接
	private BufferedReader socketIn; // 读取服务器返回的信息
	private BufferedWriter socketOut; // 向服务器发送指令，注意写入指令之后需要flush清空缓冲区才能发送出去

	// Constructor
	public SendLetter(String sender, String receiver, String password, String mailServer) {
		
		this.sender = sender;
		this.receiver = receiver;
		this.password = password;
		this.mailServer = mailServer;
	}

	// Getter and Setter
	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getMailServer() {
		return mailServer;
	}

	public void setMailServer(String mailServer) {
		this.mailServer = mailServer;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * 初始化端口和输入输出流
	 * @return 初始化成功返回true，失败返回false
	 */
	private boolean initialSocket()
	{
		boolean flag=true;

		// 如果服务器为空，则初始化失败
		if(mailServer==null)
		{
			return false;
		}
		
		try 
		{
			// 建立socket连接
			socket=new Socket(mailServer,port);
			// 构建输入输出流
			socketIn=new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			socketOut=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"));
			
			String result=socketIn.readLine();
			// 连接成功返回状态码220
			if(!result.startsWith("220"))
			{
				flag=false;
				stdOut.println("连接出错"+result);
			}
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

	/**
	 * 发送指令、信息，接收服务器响应
	 * 将socket操作封装在这里
	 * @param message 需要发送的指令
	 * @return 返回发送指令之后收到的服务器的回应
	 */
	private String sendAndGetResponse(String message) throws IOException {
		String result=null;
		try 
		{
			// 发送指令
			socketOut.write(message);
			socketOut.flush();

			// 接收响应
			result=socketIn.readLine();
		}catch (IOException e) {
			throw new IOException("发送指令或接收响应过程中产生异常");
		}

		// 返回响应
		return result;
	}
	
	public boolean sendMail() throws IOException {
		//如果初始化socket失败
		if(!initialSocket())
		{
			stdErr.print("1");
			return false;
		}
		// 输入信息不全，必须同时具有发送方、接收方和授权码
		if(sender.isEmpty() || receiver.isEmpty() || password.isEmpty())
		{
			stdErr.println("邮件发送信息填写不全");
			return false;
		}
		
		String response;
		
		//标识用户建立连接
		response=sendAndGetResponse("HELO "+mailServer+"\r\n");//向服务器标识用户身份，返回邮件服务器身份
		
		if(!response.startsWith("250"))//250:要求的邮件操作完成
		{
			stdErr.print("2\t");
			stdErr.println("Response:"+response);
			return false;
		}
		
		//如果是qq邮箱这里还会返回2行 都读掉
		//如果是163邮箱 这里不用读掉
		
		if (sender.contains("@qq.com")) 
		{
			try {
				response=socketIn.readLine();
				//stdErr.println("2-2\t"+response);
				response=socketIn.readLine();
				//stdErr.println("2-3\t"+response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
		//接下来开始登录操作
		
		//auth login 
		
		response=sendAndGetResponse("AUTH LOGIN\r\n");//请求验证身份

		// 没有返回334状态码说明验证身份这一环节出现了问题
		if(!response.startsWith("334"))//334：服务器响应验证Base64字符串 后面的字段表示请求用户名
		{
			stdErr.print("3\t");
			stdErr.println("Response:"+response);
			return false;
		}
		

		//获取base64 encoder 用户名和密码需要使用它来加密
		Base64.Encoder encoder=Base64.getEncoder();
		
		//发送用户名 采用base64加密
		response=sendAndGetResponse(encoder.encodeToString(sender.getBytes())+"\r\n");
		
		if(!response.startsWith("334"))//334：服务器响应验证Base64字符串 后面的字段表示请求密码
		{
			stdErr.print("4\t");
			stdErr.println("Response:"+response);
			return false;
		}
		
		
		//发送密码 采用base64加密
		response=sendAndGetResponse(encoder.encodeToString(password.getBytes())+"\r\n");
		
		if(!response.startsWith("235"))//235：验证成功
		{
			stdErr.print("5\t");
			stdErr.println("Response:"+response);
			return false;
		}
		
		//编辑并发送发件人
		response=sendAndGetResponse("mail from:<"+sender+">"+"\r\n");
		
		if(!response.startsWith("250"))//250:要求的邮件操作完成
		{
			stdErr.print("6\t");
			stdErr.println("Response:"+response);
			return false;
		}
		
		//编辑并发送收件人
		response=sendAndGetResponse("rcpt to:<"+receiver+">\r\n");
		
		if(!response.startsWith("250"))//250:要求的邮件操作完成
		{
			stdErr.print("7\t");
			stdErr.println("Response:"+response);
			return false;
		}

		// 编辑邮件正文内容
		response=sendAndGetResponse("data\r\n");
		
		if(!response.startsWith("354"))//354:开始邮件输入
		{
			stdErr.print("8\t");
			stdErr.println("Response:"+response);
			return false;
		}
		
		String data="";
		data+="From:<" + sender + ">\r\n";
		data+="To:<" + receiver + ">\r\n";
		data+="Subject:" + subject + "\r\n";
//		data+="Content-Type:text/plain;charset=\"UTF-8\"\r\n";

		System.out.println("是否发送HTML格式的文件（默认为纯文本文件）y/n?");
		char choice = (char)stdIn.read();
		if (choice == 'y' || choice == 'Y'){
			data+="Content-Type:text/html;charset=\"UTF-8\"\r\n";
		} else {
			data+="Content-Type:text/plain;charset=\"UTF-8\"\r\n";
		}

		data+="\r\n";
		data+=content;
		data+="\r\n.\r\n";

		// 发送邮件正文
		response=sendAndGetResponse(data);
		stdErr.println(data);
		
		if(!response.startsWith("250"))//250:要求的邮件操作完成
		{
			stdErr.print("9\t");
			stdErr.println("Response:"+response);
			return false;
		}

		// 关闭和smtp服务器的连接
		response=sendAndGetResponse("QUIT \r\n");
		
		if(!response.startsWith("221"))//221:服务关闭传输信道
		{
			stdErr.print("10\t");
			stdErr.println("Response:"+response);
			return false;
		}
		
		//关闭socket
		try
		{
			socketIn.close();
			socketOut.close();
			socket.close();
			
		} catch (IOException e) 
		{
			stdErr.println("关闭socket出错");
			e.printStackTrace();
		}

		return true;
	}

	/**
	 * 发送邮件
	 * @return 发送成功返回true，发送失败返回false
	 * @throws IOException 输入输出异常
	 */
	public boolean run () throws IOException {
		// 清空缓冲区
//		stdIn.readLine();

		stdOut.println("请输入邮件主题：");
		try
		{
			this.subject=stdIn.readLine();
//			stdErr.println("邮件主题：" + subject);
		}
		catch (IOException e)
		{
			stdErr.println("输入邮件主题出错");
			e.printStackTrace();
		}

		stdOut.println("请输入邮件内容：(输入-1邮件结束)");
		try 
		{
			String input = stdIn.readLine();
			content = "";

			// 记录一个小bug：这里必须要用equals判断
			// 如果只用!= -1判断的话会一直判断为true
			// 注：readline()不会读入换行符
			while (!input.equals("-1")){
//				System.err.println("input=" + input);
				content += input + "\n";
//				System.err.println("content=" + content);
				input = stdIn.readLine();
			}

		} 
		catch (IOException e) 
		{
			stdErr.println("输入邮件内容出错");
			e.printStackTrace();
		}

		// 附件
		
		return sendMail();
	
	}
}

