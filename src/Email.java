package src;

import java.io.*;
import java.util.StringTokenizer;


public class Email {
	
	private static BufferedReader stdIn;

	// 统一采用utf-8编码，不然会出现中文乱码现象
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

	/**
	 * 发送邮件
	 * @return 发送成功返回true，发送失败返回false
	 * @throws Exception 可能抛出发送失败的异常
	 */
	public boolean send() throws Exception {
		String sender=null;
		String password=null;
		String receiver=null;
		String mailServer=null;
		
		try
		{
			boolean flag=false;
			// 当flag为false的时候表示用户输入邮箱有误，提示用户一直输入邮箱账户直至输入合法
			while(!flag)
			{
				stdOut.println("请输入您的邮箱账户：");
				sender=stdIn.readLine();
				
				//本程序支持的qq和163邮箱的smtp服务器均为smtp.xx.com形式
				//从sender中截取获得smtp服务器
				StringTokenizer st = new StringTokenizer(sender,"@");
				if (st.countTokens()==2) // xxx@qq.com的形式
				{
					st.nextToken();// qq.com
					mailServer="smtp."+st.nextToken();// smtp.qq.com
					flag=true;//输入合法
				}else {
					stdErr.println("输入的邮箱有误");
				}
			}
			
			stdOut.println("请输入您的密码（授权码）");
			password=stdIn.readLine();
			
			stdOut.println("请输入收信方的邮箱");
			receiver=stdIn.readLine();
			
		} catch (Exception e) {
			throw new Exception("发送邮件时产生未知的异常");
		}
		
		
		SendLetter letter=new SendLetter(sender, receiver, password, mailServer);
		if(letter.run())
		{
			stdOut.println("发送成功");
			return true;
		}else {
			stdErr.println("发送出错");
			return false;
		}	
	}

	/**
	 * 接收邮件
	 * @return 接收成功返回true，接受失败返回false
	 * @throws Exception 可能抛出接收失败的异常
	 */
	public boolean receive() throws Exception {
		String server=null;
		String account=null;
		String password=null;
		
		try {
			boolean flag=false;
			while(!flag)
			{
				stdOut.println("请输入您的邮箱账户：");
				account=stdIn.readLine();
				
				//本程序支持的qq和163邮箱的pop服务器均为pop.xx.com形式
				//从account中截取获得pop服务器
				StringTokenizer st=new StringTokenizer(account,"@");

				if (st.countTokens()==2)
				{
//					st.nextToken();
//					stdErr.println("account1=" + st.nextToken());
					account = st.nextToken();
					server="pop."+st.nextToken();
					flag=true;
				}else {
					stdErr.println("输入的邮箱有误");
				}
			}
			
			stdOut.println("请输入您的密码（授权码）");
			password=stdIn.readLine();
			
		} catch (Exception e) {
			throw new Exception("接收邮件时产生未知的异常");
		}
		
		ReceiveLetter r=new ReceiveLetter(server, account, password);
		stdOut.println();
		stdOut.println("读取完成");
		return r.run();
	}
	
	public static void main(String[] args) throws Exception {
		
		Email email =new Email();
		
		String choice="1";		
		while(!choice.equals("0")) 
		{
			stdOut.println();
			stdOut.println("------------------------------");
			stdOut.println("         请选择服务");
			stdOut.println("         1.发送邮件");
			stdOut.println("         2.收取邮件");
			stdOut.println("         0.退出");
			stdOut.println("------------------------------");
		
			try {
				choice=stdIn.readLine();
			} catch (Exception e) {
				throw new Exception("读取用户选择时产生异常");
			}
			
			if (choice.equals("1")) // 发送邮件
			{
				email.send();
			}
			else if(choice.equals("2")) // 接收邮件
			{
				email.receive();
			}
			
		}
	}
}
