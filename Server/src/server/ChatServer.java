package server;


import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ChatServer
{
	/*
	 * m_threads是一个Vector静态变量,它维护所有Server方的
	 * ServerThread实例，通过该变量能向所有连入Internet
	 * 的Applet广播信息
	 */
	//Chat Server的主方法入口。
	//该方法监听Chat Applet的请求，并为新连接的
	//Applet创建一个服务线程
	public static void main(String args[])
	{
        ServerSocket socket=null;
        Vector<ServerThread> m_threads=new Vector<ServerThread>();
        System.out.println("服务器已启动，正在等待客户的请求...");
        try
        {
            //设置Server监听端口号为1234, 这个数字必须
            //和程序ChatClient中的port参数一致。
            socket=new ServerSocket(1234);
        }
        catch(Exception e)
        {
            System.out.println("服务接口建立失败!");
            return;
        }
        try
        {
            int nid=0;
            while(true)
            {
                //监听是否有新Chat Applet连接到Server,
                //线程运行到该语句会封锁，直到有新的连接产生。
                Socket s=socket.accept();
                //创建一个新的ServerThread.
                ServerThread st = new ServerThread(s, m_threads);
                //为该线程设置一个ID号。
                st.setID(nid++);
                //将该线程加入到m_threads Vector中。
                m_threads.addElement(st);
                //启动服务线程。
                new Thread(st).start();
                //通知所有Chat Applet有一个新的网友加入。
                /*for(int i=0;i<m_threads.size();i++)
                {
                    ServerThread st1=(ServerThread)m_threads.elementAt(i);
                    st1.write("<服务器>欢迎 "+st.getID()+"号朋友进入聊天室!");
                }*/
                System.out.println("接受"+st.getID()+"号客户请求");
                System.out.println("继续等待其他客户的请求...\n");
            }
        }
        catch(Exception e)
        {
            System.out.println("服务器已关闭...");
        }
    }
}
