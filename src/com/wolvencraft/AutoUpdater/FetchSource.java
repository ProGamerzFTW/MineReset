package com.wolvencraft.AutoUpdater;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import com.wolvencraft.MineReset.util.Message;

public class FetchSource
{
	
	protected static String fetchSource()
	{
		URL url = null;
		try
		{
			url = new URL("http://update.wolvencraft.com/MineReset/");
		}
		catch(MalformedURLException ex)
		{
			ex.printStackTrace();
			return null;
		}	
		
		InputStream is = null;
	    String source = "";

		try
		{
			is = url.openStream();
		}
		catch (IOException ex)
		{
			Message.log("Unable to connect to the update server!");
			return null;
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new DataInputStream(new BufferedInputStream(is))));
		
		try
		{
			source = reader.readLine();
		}
		catch (IOException ex)
		{
			Message.log("Error reading input stream!");
			return null;
		}
		
		try
		{
			is.close();
		}
		catch (IOException ioe)
		{
			Message.log("Error closing URL input stream!");
			return null;
		}
         
		return source;
	}
	
	
}
