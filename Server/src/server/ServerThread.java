package server;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import client.ClientNode;
import encryption.RSA;

/*
* 监听线程，监听对应的Chat Applet是否有信息传来。
*/
public class ServerThread implements Runnable
{
	public static List<ServerNode> list = new ArrayList<ServerNode>(); // 保存连接对象
	Vector<ServerThread> m_threads;
	Socket m_socket = null;
	ObjectInputStream m_in = null;
	ObjectOutputStream m_out = null;
	int m_nid;

	//初始化该线程。
	public ServerThread(Socket s,Vector<ServerThread> threads)
	{
		m_socket = s;
		m_threads = threads;
		try
		{
			m_in = new ObjectInputStream(m_socket.getInputStream());
			m_out = new ObjectOutputStream(m_socket.getOutputStream());
		}
		catch(Exception e)
		{
		}
	}
	public void run()  //线程的执行体。
	{
		System.out.println("等待进程正在运行");
		try
		{
			while(true)
			{
				/*for(ServerNode sn: list)
				{
					if(sn.socket.equals(m_socket))
					{
						m_in = sn.is;
						m_out = sn.os;
						break;
					}
				}*/
				Object object;
				if((object = m_in.readObject())!= null)
				{
					ClientNode clientNode = (ClientNode)object;
					switch(clientNode.type)
					{
						case 1: regist(clientNode, m_socket, m_in);break;
						case 2: login(clientNode, m_socket, m_in); break;
						case 3: acquireKey(clientNode, m_socket, m_in); break;
						case 4: addFriend(clientNode);break;
						case 5: confirmFriend(clientNode);break;
						case 6: logout(clientNode,m_socket);break;
						case 7:
						case 8:
						case -1: echoMessage(clientNode);break;
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		//从m_threads Vector中删除该线程，表示该线程已经离开聊天室。
		/*m_threads.removeElement(this);
		try
		{
			m_socket.close();
		}
		catch (Exception e){}*/
	}
	//将msg送回对应的Applet
	public void write(String msg)
	{
		synchronized(m_out)
		{
			try
			{
				m_out.writeUTF(msg);
			}
			catch(IOException e){}
		}
	}
	public int getID()  //获得该线程的ID.
	{
		return m_nid;
	}
	public void setID(int nid)  // //设置线程的ID.
	{
		m_nid=nid;
	}
	public void regist(ClientNode clientNode, Socket client, ObjectInputStream ir)
	{
		try
		{
			boolean is_Regist = false;
			for(ServerNode sn: list)
			{
				if(sn.socket.equals(client))
				{
					is_Regist = true;
					sn.clientName = clientNode.clientName;
					sn.key = clientNode.key;
					sn.password = clientNode.message;
					System.out.println(clientNode.clientName+" re-registered!");
					ClientNode cn = new ClientNode(0,clientNode.clientName,"re-regist",clientNode.clientName,null);
					m_out.writeObject(cn);
					break;
				}
				else if(sn.clientName.equals(clientNode.clientName))
				{
					is_Regist = true;
					System.out.println(clientNode.clientName+" has registered!");
					ClientNode cn = new ClientNode(0,clientNode.clientName,"has-regist",clientNode.clientName,null);
					m_out.writeObject(cn);
					break;
				}
			}
			if(is_Regist == false)
			{
				ServerNode serverNode = new ServerNode(clientNode.clientName, clientNode.message, client, ir, m_out, clientNode.key);
				list.add(serverNode);
				System.out.println(clientNode.clientName+" registered!");
				ClientNode cn = new ClientNode(0,clientNode.clientName,"ack",clientNode.clientName,null);
				m_out.writeObject(cn);
			}
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void login(ClientNode clientNode, Socket client, ObjectInputStream ir)
	{
		for(ServerNode sn: list)
		{
			//if(sn.clientName.equals(clientNode.clientName))
			if(sn.clientName.equals(clientNode.clientName))
			{
				//System.out.println(sn.clientName);
				String clientName2;
				try
				{
					clientName2 = RSA.decrypt(clientNode.message, sn.key);
					if(clientName2.equals(sn.password))
					{
						/*ArrayList<String> clientNames = new ArrayList<String>();
						for(ServerNode sn2: list)
							clientNames.add(sn2.clientName);
						sn.os.writeObject(clientNames);*/
						System.out.println(clientNode.clientName+" login!");
						ClientNode cn = new ClientNode(0,clientNode.clientName,"ack",clientNode.clientName,null);
						m_out.writeObject(cn);
					}
					else
					{
						System.out.println(clientNode.clientName+" login failed!");
						ClientNode cn = new ClientNode(0,clientNode.clientName,"non-regist",clientNode.clientName,null);
						m_out.writeObject(cn);
					}
				}
				catch(Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
	}
	public void acquireKey(ClientNode clientNode, Socket client, ObjectInputStream ir)
	{
		try
		{
			for(ServerNode sn: list)
			{
				if(sn.clientName.equals(clientNode.oppositeClientName))
				{
					ClientNode cn = new ClientNode(3,clientNode.clientName,"sendPublicKey",clientNode.oppositeClientName,sn.key);
					m_out.writeObject(cn);
					System.out.println(clientNode.clientName + " acquire public key of " + clientNode.oppositeClientName);
					break;
				}
			}
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void addFriend(ClientNode clientNode)
	{	
		try
		{
			for(ServerNode sn: list)
			{
				if(sn.clientName.equals(clientNode.clientName))
				{
					clientNode.key = sn.key;
					break;
				}
			}
			for(ServerNode sn: list)
			{
				if(sn.clientName.equals(clientNode.oppositeClientName))
				{
					System.out.println(clientNode.clientName + " send friend request to " + clientNode.oppositeClientName);
					sn.os.writeObject(clientNode);
					break;
				}
			}
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void confirmFriend(ClientNode clientNode)
	{
		try
		{
			for(ServerNode sn: list)
			{
				if(sn.clientName.equals(clientNode.clientName))
				{
					clientNode.key = sn.key;
					break;
				}
			}
			for(ServerNode sn: list)
			{
				if(sn.clientName.equals(clientNode.oppositeClientName))
				{
					System.out.println(clientNode.clientName + " confirm friend request to " + clientNode.oppositeClientName);
					sn.os.writeObject(clientNode);
					break;
				}
			}
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void logout(ClientNode clientNode, Socket socket)
	{
		try
		{
			for(ServerNode sn: list)
			{
				if(sn.clientName.equals(clientNode.clientName))
				{
					System.out.println(clientNode.clientName + " quit!");
					sn.os.writeObject(clientNode);
					list.remove(sn);
					break;
				}
			}
		}
		catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void echoMessage(ClientNode clientNode)
	{
		try
		{
			for(ServerNode sn: list)
			{
				if(sn.clientName.equals(clientNode.oppositeClientName))
				{
					System.out.println("Sending to: "+sn.clientName);
					sn.os.writeObject(clientNode);
					break;
				}
			}
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
