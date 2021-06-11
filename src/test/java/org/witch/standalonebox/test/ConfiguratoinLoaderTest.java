package org.witch.standalonebox.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.witch.standalonebox.api.config.JsonConfigurationLoader;
import org.witch.standalonebox.api.config.YamlConfigurationLoader;

import lombok.Data;
import lombok.EqualsAndHashCode;

public class ConfiguratoinLoaderTest
{
	
	private JsonConfigurationLoader jsonMapper;
	private YamlConfigurationLoader yamlMapper;
	
	@Before
	public void createMappers()
	{
		jsonMapper = new JsonConfigurationLoader();
		yamlMapper = new YamlConfigurationLoader();
	}
	
	@Test
	public void testJson() throws IOException
	{
		TestContainer container = TestContainer.generate();
		byte[] data = jsonMapper.saveConfiguration(container);
		TestContainer deserialize = jsonMapper.loadConfiguration(data, TestContainer.class);
		Assert.assertEquals(container, deserialize);
	}
	
	public void testYaml() throws IOException
	{
		TestContainer container = TestContainer.generate();
		byte[] data = yamlMapper.saveConfiguration(container);
		TestContainer deserialize = yamlMapper.loadConfiguration(data, TestContainer.class);
		Assert.assertEquals(container, deserialize);
	}

	@EqualsAndHashCode
	@Data
	private static class TestContainer
	{
		private long longValue;
		private double doubleValue;
		private String stringValue;
		
		private Set<String> collectionSet;
		private List<String> collectionList;
		private Map<String, String> map;
		
		public static TestContainer generate()
		{
			TestContainer container = new TestContainer();
			Random rng = ThreadLocalRandom.current();
			
			container.setLongValue(rng.nextLong());
			container.setDoubleValue(rng.nextDouble());
			container.setStringValue(RandomStringUtils.random(32, true, true));
			
			Set<String> set = new HashSet<>();
			List<String> list = new ArrayList<>();
			Map<String, String> map = new HashMap<>();
			for(int i = 0; i < 10; i++)
			{
				set.add(RandomStringUtils.random(32, true, true));
				list.add(RandomStringUtils.random(32, true, true));
				map.put(RandomStringUtils.random(32, true, true), RandomStringUtils.random(32, true, true));
			}
			
			container.setCollectionSet(set);
			container.setCollectionList(list);
			container.setMap(map);
			
			return container;
		}
	}
	
}
