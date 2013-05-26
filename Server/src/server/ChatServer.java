package server;


import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ChatServer
{
	/*
	 * m_threads��һ��Vector��̬����,��ά������Server����
	 * ServerThreadʵ����ͨ���ñ���������������Internet
	 * ��Applet�㲥��Ϣ
	 */
	//Chat Server����������ڡ�
	//�÷�������Chat Applet�����󣬲�Ϊ�����ӵ�
	//Applet����һ�������߳�
	public static void main(String args[])
	{
        ServerSocket socket=null;
        Vector<ServerThread> m_threads=new Vector<ServerThread>();
        System.out.println("�����������������ڵȴ��ͻ�������...");
        try
        {
            //����Server�����˿ں�Ϊ1234, ������ֱ���
            //�ͳ���ChatClient�е�port����һ�¡�
            socket=new ServerSocket(1234);
        }
        catch(Exception e)
        {
            System.out.println("����ӿڽ���ʧ��!");
            return;
        }
        try
        {
            int nid=0;
            while(true)
            {
                //�����Ƿ�����Chat Applet���ӵ�Server,
                //�߳����е������������ֱ�����µ����Ӳ�����
                Socket s=socket.accept();
                //����һ���µ�ServerThread.
                ServerThread st = new ServerThread(s, m_threads);
                //Ϊ���߳�����һ��ID�š�
                st.setID(nid++);
                //�����̼߳��뵽m_threads Vector�С�
                m_threads.addElement(st);
                //���������̡߳�
                new Thread(st).start();
                //֪ͨ����Chat Applet��һ���µ����Ѽ��롣
                /*for(int i=0;i<m_threads.size();i++)
                {
                    ServerThread st1=(ServerThread)m_threads.elementAt(i);
                    st1.write("<������>��ӭ "+st.getID()+"�����ѽ���������!");
                }*/
                System.out.println("����"+st.getID()+"�ſͻ�����");
                System.out.println("�����ȴ������ͻ�������...\n");
            }
        }
        catch(Exception e)
        {
            System.out.println("�������ѹر�...");
        }
    }
}
