# WitchBox Bootstrap

Simple bootstrap that I use in my projects with Bukkit-style module support and event bus.
You probably don't need it.

## Dependency:

Use [jitpack](https://jitpack.io/ "jitpack"):

```xml
	<dependency>
	    <groupId>com.github.Master-chan</groupId>
	    <artifactId>WitchBox</artifactId>
	    <version>master-SNAPSHOT</version>
	</dependency>
```
	
## Features:

* Bukkit-style module support. Loads .jar files from /module/ folder with simple dependency resolution.
* Event bus based on https://github.com/Techcable/Event4J
* slf4j & j.u.l logger with log rotation.
* Loading .json and .yml files with one line of code.
* Am I really writing a doc for something nobody except me will use? Great.