package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.security.interfaces.RSAPublicKey;

public class ServerNode implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String clientName;
	public String password;
	public Socket socket;
	public ObjectInputStream is;
	public ObjectOutputStream os;
	public RSAPublicKey key;
	public ServerNode(String clientName, String password, Socket socket, ObjectInputStream is, ObjectOutputStream os, RSAPublicKey key)
	{
		this.clientName = clientName;
		this.socket = socket;
		this.is = is;
		this.os = os;
		this.key = key;
		this.password = password;
	}
}
