package org.quickload.plugin;

import java.io.IOException;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.quickload.model.ModelManager;
import org.quickload.model.ModelAccessor;
import org.quickload.config.Config;
import org.quickload.config.ConfigException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * InjectedPluginSource loads plugins bound by Guice.
 * This plugin source is intended to be used in test cases.
 * Plugins need to be bound to Binder with Name annotation as following:
 *
 * // Module
 * public void configure(Binder binder)
 * {
 *     bind(InputPlugin.class)
 *              .annotatedWith(Names.named("my"))
 *              .to(MyInputPlugin.class);
 * }
 *
 */
public class InjectedPluginSource
        implements PluginSource
{
    public static class Task
    {
        private final String name;

        @JsonCreator
        public Task(@JsonProperty("injected") String name)
        {
            this.name = name;
        }

        @JsonProperty("injected")
        public String getName()
        {
            return name;
        }
    }

    private final ModelManager modelManager;
    private final Injector injector;

    @Inject
    public InjectedPluginSource(
            ModelManager modelManager,
            Injector injector)
    {
        this.modelManager = modelManager;
        this.injector = injector;
    }

    public <T> T newPlugin(Class<T> iface, JsonNode typeConfig) throws PluginSourceNotMatchException
    {
        Task task;
        try {
            task = modelManager.readJsonObject(typeConfig, Task.class);
        } catch (RuntimeException ex) {
            // TODO throw PluginSourceNotMatchException if injected field does not exist
            throw ex;
        }

        return injector.getInstance(Key.get(iface, Names.named(task.getName())));
    }
}
