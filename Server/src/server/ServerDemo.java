package server;

import encryption.RSA;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import client.ClientNode;

public class ServerDemo
{
	  private static final int PORT = 1234; // 端口号
	  public static List<ServerNode> list = new ArrayList<ServerNode>(); // 保存连接对象
	  private ExecutorService exec;
	  private ServerSocket server;
	  public static void main(String[] args)
	  {
		  new ServerDemo();
	  }
	  public ServerDemo()
	  {
		  try
		  {
			  server = new ServerSocket(PORT);
			  exec = Executors.newCachedThreadPool();
			  System.out.println("服务器已启动！");
			  System.out.println(InetAddress.getLocalHost().getHostAddress());
			  Socket client = null;
			  while (true)
			  {
				  client = server.accept(); // 接收客户连接
				  ObjectInputStream is = new ObjectInputStream(client.getInputStream());
				  for(ServerNode sn: list)
				  {
					  if(sn.socket.equals(client))
					  {
						  is = sn.is;
						  break;
					  }
				  }
				  Object object;
				  if((object = is.readObject())!= null)
				  {
					  ClientNode clientNode = (ClientNode)object;
					  switch(clientNode.type)
					  {
					      case 1: regist(clientNode, client, is);break;
					      case 2: login(clientNode, client, is); break;
					      case -1: echoMessage(clientNode);break;
					  }
				  }
			  }
		  }
		  catch(IOException e)
		  {
			  e.printStackTrace();
		  }
		  catch (ClassNotFoundException e)
		  {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	  public void regist(ClientNode clientNode, Socket client, ObjectInputStream ir)
	  {
		  try
		  {
			  ObjectOutputStream os = new ObjectOutputStream(client.getOutputStream());
			  ServerNode serverNode = new ServerNode(clientNode.clientName, clientNode.message, client, ir, os, clientNode.key);
			  list.add(serverNode);
			  System.out.println(clientNode.clientName);
			  ClientNode cn = new ClientNode(0,clientNode.clientName,"ack",clientNode.clientName,null);
			  os.writeObject(cn);
		  }
		  catch(IOException e)
		  {
			  // TODO Auto-generated catch block
			  e.printStackTrace();
		  }
	  }
	  public void login(ClientNode clientNode, Socket client, ObjectInputStream ir)
	  {
		  System.out.println();
		  System.out.println(clientNode.clientName);
		  System.out.println();
		  for(ServerNode sn: list)
		  {
			  System.out.println(sn.clientName);
		  }
		  /*for(ServerNode sn: list)
		  {
			  if(sn.clientName.equals(clientNode.clientName))
			  {
				  System.out.println(sn.clientName);
				  String clientName2;
				  try
				  {
					  //clientName2 = RSA.decrypt(clientNode.message, sn.key);
					  //if(clientName2.equals(clientNode.clientName))
					  {
						  ArrayList<String> clientNames = new ArrayList<String>();
						  for(ServerNode sn2: list)
						  {
							  clientNames.add(sn2.clientName);
						  }
						  sn.os.writeObject(clientNames);
					  }
				  }
				  catch(Exception e)
				  {
					// TODO Auto-generated catch block
					e.printStackTrace();
				  }
				  break;
			  }
		  }*/
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
