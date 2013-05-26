package com.example.communication;

import java.io.ObjectInputStream;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;

import encryption.RSA;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import client.ClientNode;

public class LoginActivity extends Activity {

	//public static ArrayList<String> clientNames;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		final TextView username = (TextView)findViewById(R.id.editText2);
		final TextView password = (TextView)findViewById(R.id.editText1);
		final Button login = (Button)findViewById(R.id.button2);
		final Button toRegist = (Button)findViewById(R.id.button1);
		login.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//System.out.println(username.getText());
				Runnable runnable = new Runnable()
				{
					@SuppressWarnings("unchecked")
					@Override
				    public void run()
				    {
				    	try
						{
				    		String resultByte = RSA.encrypt(password.getText().toString(), RegistActivity.RSAPrKey);
				    		ClientNode clientNode = new ClientNode(2, username.getText().toString(), resultByte, "", RegistActivity.RSAPuKey);
				    		RegistActivity.br.writeObject(clientNode);
							Object object;
							if((object = RegistActivity.ir.readObject()) != null)
							{
								//clientNames = (ArrayList<String>) object;
								ClientNode cn = (ClientNode)object;
								if(cn.message.equals("ack"))
								{
									String file = RegistActivity.usernameS+".txt";
									RegistActivity.friendList = new ArrayList<ClientNode>();
									{
										try
										{
											ObjectInputStream ois = new ObjectInputStream(LoginActivity.this.openFileInput(file));
											RegistActivity.usernameS = (String)ois.readObject();
											RegistActivity.RSAPrKey = (RSAPrivateKey)ois.readObject();
											RegistActivity.RSAPuKey = (RSAPublicKey)ois.readObject();
											RegistActivity.friendList = (ArrayList<ClientNode>)ois.readObject();
											ois.close();
										}
										catch (Exception e)
										{
											//Log.v("mytag","L1"+e.toString());
										}
									}
									Intent intent = new Intent(); // 建立Intent
									intent.setClass(LoginActivity.this, CommunicationActivity.class); // 设置活动
									startActivity(intent);
									finish();
								}
								else if(cn.message.equals("non-regist"))
								{
									Intent intent = new Intent(); // 建立Intent
									intent.setClass(LoginActivity.this, RegistActivity.class); // 设置活动
									startActivity(intent);
									finish();
								}
							}
						}
						catch(Exception e)
						{
							Log.v("mytag", "L2"+e.toString());
						}
				    }
				};
				new Thread(runnable).start();
			}
		});
		toRegist.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(); // 建立Intent
				intent.setClass(LoginActivity.this, RegistActivity.class); // 设置活动
				startActivity(intent);
				finish();
			}
		});
	}
}
