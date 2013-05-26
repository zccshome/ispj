package client;

import java.io.Serializable;
import java.security.interfaces.RSAPublicKey;

public class ClientNode implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int type;
	public RSAPublicKey key;
	public String clientName;
	public String message;
	public String oppositeClientName;
	
	public ClientNode(int type, String clientName, String message, String oppositeClientName, RSAPublicKey key)
	{
		this.type = type;
		this.clientName = clientName;
		this.message = message;
		this.oppositeClientName = oppositeClientName;
		this.key = key;
	}
}
