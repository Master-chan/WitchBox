package org.witch.standalonebox.api.config;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class YamlConfigurationLoader extends ConfigurationLoader
{
	
	private final ObjectMapper ymlMapper;
	
	public YamlConfigurationLoader()
	{
		ymlMapper = new ObjectMapper(new YAMLFactory());
		ymlMapper.findAndRegisterModules();
	}

	@Override
	public <T> T loadConfiguration(byte[] data, Class<T> type) throws IOException
	{
		return ymlMapper.readValue(data, type);
	}
	
	@Override
	public byte[] saveConfiguration(Object data) throws IOException
	{
		return ymlMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(data);
	}
}
