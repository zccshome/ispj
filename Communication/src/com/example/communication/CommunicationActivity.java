package com.example.communication;

import java.io.ObjectOutputStream;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.HashMap;

import client.ClientNode;
//import encryption.RSA;
import encryption.AES;
import encryption.DES;
import encryption.RSAUtils;

import android.os.Bundle;
import android.os.Looper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

public class CommunicationActivity extends Activity
{
	HashMap<String,String> keyList = new HashMap<String,String>();
	public static String encryptType = "DES";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_communication);
		//final RadioGroup group = (RadioGroup)findViewById(R.id.radioGroup1);
		final Button send = (Button)findViewById(R.id.button2);
		final RadioGroup friends = (RadioGroup)findViewById(R.id.radioGroup2);
		final Button addFriend = (Button)findViewById(R.id.button1);
		final Button logout = (Button)findViewById(R.id.button3);
		final TextView message = (TextView)findViewById(R.id.editText1);
		final Spinner spinner = (Spinner)findViewById(R.id.spinner1);
		ArrayAdapter <CharSequence> adapter = ArrayAdapter.createFromResource(this,  
                R.array.encrypt_array, android.R.layout.simple_spinner_item);  
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);  
        spinner.setPrompt("下拉菜单");  
        spinner.setOnItemSelectedListener(new OnItemSelectedListener()
        {
        		@Override  
        	    public void onItemSelected(AdapterView<?> AdapterView, View view, int position,  
        	            long arg3) {  
        	        // TODO Auto-generated method stub  
        	        String selected = AdapterView.getItemAtPosition(position).toString();
        	        encryptType = selected;
        	    }  
        	  
        	    @Override  
        	    public void onNothingSelected(AdapterView<?> arg0) {  
        	        // TODO Auto-generated method stub  
        	        //System.out.println("NothingSelected");
        	    }
        }
        ); 
		/*ArrayList<String> clientNames = LoginActivity.clientNames;
		for(String s: clientNames)
		{
			RadioButton rb = new RadioButton(this);
			rb.setText(s);
			rb.setChecked(false);
			group.addView(rb);
		}*/
		Runnable runnable = new Runnable()
		{
			@Override
		    public void run()
		    {
		    	try
				{
		    		Object object;
					while((object = RegistActivity.ir.readObject()) != null)
					{
						ClientNode returnNode = (ClientNode)object;
						if(returnNode.type == 3)
						{
							String text = "addFriend";
							String prString = RSAUtils.encryptByPrivateKey(text, RegistActivity.RSAPrKey);
							String puString = RSAUtils.encryptByPublicKey(prString, returnNode.key);
				    		ClientNode clientNode = new ClientNode(4, RegistActivity.usernameS, puString, returnNode.oppositeClientName, null);
							RegistActivity.br.writeObject((Object)clientNode);
						}
						else if(returnNode.type == 4)
						{
							String prString = RSAUtils.decryptByPrivateKey(returnNode.message, RegistActivity.RSAPrKey);
							String puString = RSAUtils.decryptByPublicKey(prString, returnNode.key);
				    		if(puString.equals("addFriend"))
				    		{
				    			returnNode.oppositeClientName = returnNode.clientName;
				    			returnNode.clientName = RegistActivity.usernameS;
				    			RegistActivity.friendList.add(returnNode);
				    			String text = "confirmFriend";
				    			prString = RSAUtils.encryptByPrivateKey(text, RegistActivity.RSAPrKey);
								puString = RSAUtils.encryptByPublicKey(prString, returnNode.key);
				    			ClientNode clientNode = new ClientNode(5, RegistActivity.usernameS, puString, returnNode.oppositeClientName, null);
				    			RegistActivity.br.writeObject((Object)clientNode);
				    			//CommunicationActivity.this.finish();
								Intent intent = new Intent(); // 建立Intent
								intent.setClass(CommunicationActivity.this, CommunicationActivity.class); // 设置活动
								startActivity(intent);
								finish();
								break;
				    		}
						}
						else if(returnNode.type == 5)
						{
							String prString = RSAUtils.decryptByPrivateKey(returnNode.message, RegistActivity.RSAPrKey);
							String puString = RSAUtils.decryptByPublicKey(prString, returnNode.key);
				    		if(puString.equals("confirmFriend"))
				    		{
				    			returnNode.oppositeClientName = returnNode.clientName;
				    			returnNode.clientName = RegistActivity.usernameS;
				    			RegistActivity.friendList.add(returnNode);
				    			//CommunicationActivity.this.finish();
								Intent intent = new Intent(); // 建立Intent
								intent.setClass(CommunicationActivity.this, CommunicationActivity.class); // 设置活动
								startActivity(intent);
								finish();
								break;
				    		}
						}
						else if(returnNode.type == 6)
						{
							Intent intent = new Intent(); // 建立Intent
							intent.setClass(CommunicationActivity.this, RegistActivity.class); // 设置活动
							startActivity(intent);
							finish();
							break;
						}
						else if(returnNode.type == 7)
						{
							returnNode.oppositeClientName = returnNode.clientName;
			    			returnNode.clientName = RegistActivity.usernameS;
			    			RSAPublicKey puKey;
			    			for(ClientNode cn: RegistActivity.friendList)
				    		{
				    			if(cn.oppositeClientName.equals(returnNode.oppositeClientName))
				    			{
				    				puKey = cn.key;
				    				String prString = RSAUtils.decryptByPrivateKey(returnNode.message, RegistActivity.RSAPrKey);
									String puString = RSAUtils.decryptByPublicKey(prString, puKey);
									keyList.put(returnNode.oppositeClientName, puString);
									prString = RSAUtils.encryptByPrivateKey(puString, RegistActivity.RSAPrKey);
									puString = RSAUtils.encryptByPublicKey(prString, puKey);
									ClientNode clientNode = new ClientNode(8, RegistActivity.usernameS, puString, returnNode.oppositeClientName, null);
									RegistActivity.br.writeObject((Object)clientNode);
				    			}
				    		}
						}
						else if(returnNode.type == 8)
						{
							returnNode.oppositeClientName = returnNode.clientName;
			    			returnNode.clientName = RegistActivity.usernameS;
			    			RSAPublicKey puKey;
			    			for(ClientNode cn: RegistActivity.friendList)
				    		{
				    			if(cn.oppositeClientName.equals(returnNode.oppositeClientName))
				    			{
				    				puKey = cn.key;
				    				String prString = RSAUtils.decryptByPrivateKey(returnNode.message, RegistActivity.RSAPrKey);
									String puString = RSAUtils.decryptByPublicKey(prString, puKey);
									keyList.put(returnNode.oppositeClientName, puString);
									if(encryptType.equals("DES"))
									{
										ClientNode clientNode = new ClientNode(11, RegistActivity.usernameS, DES.encrypt(message.getText().toString(), puString), returnNode.oppositeClientName, null);
										RegistActivity.br.writeObject((Object)clientNode);
									}
									else if(encryptType.equals("AES"))
									{
										ClientNode clientNode = new ClientNode(12, RegistActivity.usernameS, AES.encrypt(message.getText().toString(), puString), returnNode.oppositeClientName, null);
										RegistActivity.br.writeObject((Object)clientNode);
									}
				    			}
				    		}
						}
						else if (returnNode.type == 11)
						{
							//Log.v("mytag", "message from "+returnNode.clientName+" : "+returnNode.message);
							String desKey = keyList.get(returnNode.clientName);
							Looper.prepare();
							new AlertDialog.Builder(CommunicationActivity.this)
							.setTitle("来自"+returnNode.clientName+"的消息")
							.setMessage(DES.decrypt(returnNode.message, desKey))
							.setPositiveButton("确定", 
									new DialogInterface.OnClickListener()
									{
						            	public void onClick(DialogInterface dialog, int whichButton)
						            	{
						            		//setResult(RESULT_OK);
						            		Intent intent = new Intent(); // 建立Intent
											intent.setClass(CommunicationActivity.this, CommunicationActivity.class); // 设置活动
											startActivity(intent);
											finish();
						            	}
									})
							.show();
							Looper.loop();
							break;
						}
						else if (returnNode.type == 12)
						{
							//Log.v("mytag", "message from "+returnNode.clientName+" : "+returnNode.message);
							String desKey = keyList.get(returnNode.clientName);
							Looper.prepare();
							new AlertDialog.Builder(CommunicationActivity.this)
							.setTitle("来自"+returnNode.clientName+"的消息")
							.setMessage(AES.decrypt(returnNode.message, desKey))
							.setPositiveButton("确定", 
									new DialogInterface.OnClickListener()
									{
						            	public void onClick(DialogInterface dialog, int whichButton)
						            	{
						            		//setResult(RESULT_OK);
						            		Intent intent = new Intent(); // 建立Intent
											intent.setClass(CommunicationActivity.this, CommunicationActivity.class); // 设置活动
											startActivity(intent);
											finish();
						            	}
									})
							.show();
							Looper.loop();
							break;
						}
					}
				}
				catch(Exception e)
				{
					Log.v("mytag", "C1"+e.toString());
				}
		    }
		};
		new Thread(runnable).start();
		for(ClientNode s: RegistActivity.friendList)
		{
			RadioButton rb = new RadioButton(this);
			rb.setText(s.oppositeClientName);
			rb.setChecked(false);
			friends.addView(rb);
		}
		addFriend.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*int radioButtonId = group.getCheckedRadioButtonId();
				RadioButton rb = (RadioButton)CommunicationActivity.this.findViewById(radioButtonId);
				final String clientName = rb.getText().toString();*/
				final String clientName = message.getText().toString();
				Runnable runnable = new Runnable()
				{
					@Override
				    public void run()
				    {
				    	try
						{
							ClientNode clientNode = new ClientNode(3, RegistActivity.usernameS, "addFriend", clientName, null);
							RegistActivity.br.writeObject((Object)clientNode);
						}
						catch(Exception e)
						{
							Log.v("mytag", "C2"+e.toString());
						}
				    }
				};
				new Thread(runnable).start();
			}
		});
		send.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int radioButtonId = friends.getCheckedRadioButtonId();
				RadioButton rb = (RadioButton)CommunicationActivity.this.findViewById(radioButtonId);
				final String oppoClientName = rb.getText().toString();
				Runnable runnable = new Runnable()
				{
					@Override
				    public void run()
				    {
				    	try
						{
				    		RSAPublicKey puKey;
				    		for(ClientNode cn: RegistActivity.friendList)
				    		{
				    			if(cn.oppositeClientName.equals(oppoClientName))
				    			{
				    				puKey = cn.key;
				    				if(keyList.get(oppoClientName) == null)
						    		{
						    			String key = ""+(new Date().getTime());
						    			String prString = RSAUtils.encryptByPrivateKey(key, RegistActivity.RSAPrKey);
										String puString = RSAUtils.encryptByPublicKey(prString, puKey);
						    			ClientNode clientNode = new ClientNode(7, RegistActivity.usernameS, puString, oppoClientName, null);
										RegistActivity.br.writeObject((Object)clientNode);
						    		}
				    				else
				    				{
				    					String desKey = keyList.get(oppoClientName);
				    					String messageS = message.getText().toString();
						    			/*ClientNode clientNode = new ClientNode(-1, RegistActivity.usernameS, DES.encrypt(messageS,desKey), oppoClientName, null);
										RegistActivity.br.writeObject((Object)clientNode);*/
										if(encryptType.equals("DES"))
										{
											ClientNode clientNode = new ClientNode(11, RegistActivity.usernameS, DES.encrypt(messageS, desKey), oppoClientName, null);
											RegistActivity.br.writeObject((Object)clientNode);
										}
										else if(encryptType.equals("AES"))
										{
											ClientNode clientNode = new ClientNode(12, RegistActivity.usernameS, AES.encrypt(messageS, desKey), oppoClientName, null);
											RegistActivity.br.writeObject((Object)clientNode);
										}
				    				}
				    				break;
				    			}
				    		}
						}
						catch(Exception e)
						{
							Log.v("mytag", "C3"+e.toString());
						}
				    }
				};
				new Thread(runnable).start();
			}
		});
		/*group.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1)
			{
				// TODO Auto-generated method stub
		});*/
		logout.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				String file = RegistActivity.usernameS+".txt";
				try
				{
					ObjectOutputStream ois = new ObjectOutputStream(CommunicationActivity.this.openFileOutput(file, MODE_PRIVATE));
					ois.writeObject(RegistActivity.usernameS);
					ois.writeObject(RegistActivity.RSAPrKey);
					ois.writeObject(RegistActivity.RSAPuKey);
					ois.writeObject(RegistActivity.friendList);
					ois.close();
					ClientNode clientNode = new ClientNode(6, RegistActivity.usernameS, "logout", RegistActivity.usernameS, null);
					RegistActivity.br.writeObject((Object)clientNode);
				}
				catch (Exception e)
				{
						Log.v("mytag","C4"+e.toString());
				}
			}
		});
	}
}
