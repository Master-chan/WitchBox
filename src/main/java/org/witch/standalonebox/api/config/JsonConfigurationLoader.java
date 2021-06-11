package org.witch.standalonebox.api.config;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonConfigurationLoader extends ConfigurationLoader
{
	
	private final ObjectMapper jsonMapper;
	
	public JsonConfigurationLoader()
	{
		jsonMapper = new ObjectMapper(new JsonFactory());
		jsonMapper.findAndRegisterModules();
	}

	@Override
	public <T> T loadConfiguration(byte[] data, Class<T> type) throws IOException
	{
		return jsonMapper.readValue(data, type);
	}

	@Override
	public byte[] saveConfiguration(Object data) throws IOException
	{
		return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(data);
	}
}
