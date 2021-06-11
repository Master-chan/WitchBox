package org.witch.standalonebox.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.witch.standalonebox.api.plugin.Plugin;

public class ResourceStreamUtil
{

	public static InputStream getResourceAsStream(String path)
	{
		InputStream stream = ResourceStreamUtil.class.getResourceAsStream(path);
		String[] split = path.split("/");
		path = split[split.length - 1];
		if (stream == null)
		{
			File resource = new File("src\\main\\resources\\" + path);
			if (resource.exists())
			{
				try
				{
					stream = new BufferedInputStream(new FileInputStream(resource));
				}
				catch (IOException ignore)
				{
				}
			}
		}
		return stream;
	}
	
	public static InputStream getResourceAsStream(String path, Plugin plugin)
	{
		InputStream stream = plugin.getClass().getResourceAsStream(path);
		String[] split = path.split("/");
		path = split[split.length - 1];
		if (stream == null)
		{
			File resource = new File("src\\main\\resources\\" + path);
			if (resource.exists())
			{
				try
				{
					stream = new BufferedInputStream(new FileInputStream(resource));
				}
				catch (IOException ignore)
				{
				}
			}
		}
		return stream;
	}
	
}