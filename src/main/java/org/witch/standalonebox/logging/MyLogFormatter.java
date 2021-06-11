package org.witch.standalonebox.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import org.apache.commons.lang3.time.FastDateFormat;

public class MyLogFormatter extends Formatter
{
	private final FastDateFormat date = FastDateFormat.getInstance("dd-MM-yyyy HH:mm:ss");

	public String format(LogRecord record)
	{		
		StringBuilder formatted = new StringBuilder();
		formatted.append(this.date.format(Long.valueOf(record.getMillis())));
		formatted.append(" [");
		formatted.append(record.getLevel().getLocalizedName());
		formatted.append("] ");
		formatted.append(formatMessage(record));
		formatted.append('\n');
		if(record.getThrown() != null)
		{
			StringWriter writer = new StringWriter();
			record.getThrown().printStackTrace(new PrintWriter(writer));
			formatted.append(writer);
		}
		return formatted.toString();
	}
}