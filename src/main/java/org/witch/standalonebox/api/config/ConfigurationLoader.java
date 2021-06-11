package org.witch.standalonebox.api.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.witch.standalonebox.api.plugin.Plugin;
import org.witch.standalonebox.utils.ResourceStreamUtil;

import com.google.common.base.Charsets;

public abstract class ConfigurationLoader
{
	
	public abstract byte[] saveConfiguration(Object data) throws IOException;
	
	public abstract <T> T loadConfiguration(byte[] data, Class<T> type) throws IOException;
		
	public <T> T loadConfiguration(String configString, Class<T> type) throws IOException
	{
		byte[] data = configString.getBytes(Charsets.UTF_8);
		return loadConfiguration(data, type);
	}
	
	public <T> T loadConfiguration(InputStream configStream, Class<T> type) throws IOException
	{
		byte[] data = IOUtils.toByteArray(configStream);
		return loadConfiguration(data, type);
	}
	
	public <T> T loadConfiguration(File configFile, Class<T> type) throws IOException 
	{
		byte[] data = Files.readAllBytes(Paths.get(configFile.toURI()));
		return loadConfiguration(data, type);
	}
	
	public void exportDefaultConfig(String defaultConfigPath, Plugin plugin, File destination) throws IOException 
	{
		try(InputStream input = ResourceStreamUtil.getResourceAsStream(defaultConfigPath, plugin))
		{
			FileUtils.copyToFile(input, destination);
		}
	}
	
	public <T> T loadOrDefaultConfiguration(File configFile, String defaultConfigPath, Plugin plugin, Class<T> type) throws IOException
	{
		if(!configFile.exists())
		{
			exportDefaultConfig(defaultConfigPath, plugin, configFile);
		}
		return loadConfiguration(configFile, type);
	}
	
}
