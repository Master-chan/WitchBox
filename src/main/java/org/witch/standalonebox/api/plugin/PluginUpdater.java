package org.witch.standalonebox.api.plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.witch.standalonebox.api.Server;

public class PluginUpdater
{
	
	/**
	 * Check and download new version of jar plugin based on sha-1 hashfile.
	 * @param updateURL
	 * @param jarfile
	 * @return true if jar was redownloaded, false otherwise
	 */
	public boolean updateJar(String updateString, File jarfile)
	{
		try
		{
			URL updateUrl = new URL(updateString);
			URL sha1Url = new URL(updateUrl.toString() + ".sha1");
			
			String localSha1 = DigestUtils.sha1Hex(Files.readAllBytes(Paths.get(jarfile.toURI()))).toLowerCase().trim();
			String remoteSha1 = copyURLToString(sha1Url, StandardCharsets.UTF_8, 2000, 2000).toLowerCase().trim();
						
			if(remoteSha1 != null && remoteSha1.length() > 1 && !localSha1.equals(remoteSha1))
			{
				Server.getInstance().getLogger().info(String.format("Updating plugin file %s", jarfile.getName()));
				FileUtils.copyURLToFile(updateUrl, jarfile, 2000, 2000);
				return true;
			}
		}
		catch(IOException e)
		{
			Server.getInstance().getLogger().log(Level.WARNING, "Exception updating plugin file: " + jarfile.getName(), e);
		}
		return false;
	}
	
	
	private String copyURLToString(URL source, Charset charset, int connectionTimeout, int readTimeout) throws IOException
	{
		final URLConnection connection = source.openConnection();
		connection.setConnectTimeout(connectionTimeout);
		connection.setReadTimeout(readTimeout);
		try(final InputStream stream = connection.getInputStream())
		{
			return new String(IOUtils.toByteArray(stream), charset);
		}
	}
}
