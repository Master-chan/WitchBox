package org.witch.standalonebox.api.plugin;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PluginDescription
{
    private String name;
    private String main;
    private String version = "Unknown";
    private String author = "Unknown author";
    private Set<String> depends = new HashSet<>();
    private Set<String> softDepends = new HashSet<>();
    private File file = null;
    private String description = null;
    private String updateUrl = null;
    
    private transient boolean annotationLoader = false;
}
