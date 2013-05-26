package com.example.communication;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.*;
import java.security.interfaces.*;
import java.util.ArrayList;

import client.ClientNode;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class RegistActivity extends Activity {

	private static final int PORT = 1234;
	public static ObjectOutputStream br;
	public static ObjectInputStream ir;
	public static Socket socket;
	public static RSAPrivateKey RSAPrKey;
	public static RSAPublicKey RSAPuKey;
	public static String usernameS;
	public static ArrayList<ClientNode> friendList = new ArrayList<ClientNode>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist);
		final TextView username = (TextView)findViewById(R.id.editText2);
		final TextView password = (TextView)findViewById(R.id.editText1);
		final Button register = (Button)findViewById(R.id.button2);
		final Button toLogin = (Button)findViewById(R.id.button1);
		register.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Runnable runnable = new Runnable()
				{
					@Override
				    public void run()
				    {
				    	try
						{
				    		if(socket == null)
				    			socket = new Socket("169.254.88.163", PORT);
				    		if(br == null)
				    			br = new ObjectOutputStream(socket.getOutputStream());
				    		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
				    		kpg.initialize(1024);
				    		KeyPair kp = kpg.generateKeyPair();
				    		RSAPrKey = (RSAPrivateKey)kp.getPrivate();
				    		RSAPuKey = (RSAPublicKey)kp.getPublic();
				    		usernameS = username.getText().toString();
				    		ClientNode clientNode = new ClientNode(1, username.getText().toString(), password.getText().toString(), "", RSAPuKey);
							br.writeObject((Object)clientNode);
							if(ir == null)
								ir = new ObjectInputStream(socket.getInputStream());
							Object object;
							if((object = ir.readObject()) != null)
							{
								ClientNode cn = (ClientNode)object;
								if(cn.message.equals("ack"))
								{
									Intent intent = new Intent(); // 建立Intent
									intent.setClass(RegistActivity.this, LoginActivity.class); // 设置活动
									startActivity(intent);
									finish();
								}
								else if(cn.message.equals("re-regist"))
								{
									Intent intent = new Intent(); // 建立Intent
									intent.setClass(RegistActivity.this, LoginActivity.class); // 设置活动
									startActivity(intent);
									finish();
								}
								else if(cn.message.equals("has-regist"))
								{
								}
							}
						}
						catch(Exception e)
						{
							Log.v("mytag", "R1"+e.toString());
						}
				    }
				};
				new Thread(runnable).start();
			}
		});
		toLogin.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				Runnable runnable = new Runnable()
				{
					@Override
				    public void run()
				    {
				    	try
						{
				    		if(socket == null)
				    			socket = new Socket("169.254.88.163", PORT);
				    		if(br == null)
				    			br = new ObjectOutputStream(socket.getOutputStream());
							if(ir == null)
								ir = new ObjectInputStream(socket.getInputStream());
							Intent intent = new Intent(); // 建立Intent
							intent.setClass(RegistActivity.this, LoginActivity.class); // 设置活动
							startActivity(intent);
							finish();
						}
						catch(Exception e)
						{
							Log.v("mytag", "R2"+e.toString());
						}
				    }
				};
				new Thread(runnable).start();
			}
		});
	}
}