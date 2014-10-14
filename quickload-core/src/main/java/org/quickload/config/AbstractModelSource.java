package org.quickload.config;

import java.lang.reflect.Method;
import com.google.common.base.Optional;
import com.google.common.base.Function;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.quickload.model.ModelManager;
import org.quickload.model.ModelAccessor;

public class AbstractModelSource
{
    protected final ModelManager modelManager;
    private final Function<Method, Optional<String>> jsonKeyMapper;
    protected final ObjectNode source;

    public AbstractModelSource(ModelManager modelManager, ObjectNode source)
    {
        this(modelManager, source, null);
    }

    public AbstractModelSource(ModelManager modelManager, ObjectNode source,
            Function<Method, Optional<String>> jsonKeyMapper)
    {
        this.modelManager = modelManager;
        this.source = source;
        this.jsonKeyMapper = jsonKeyMapper;
    }

    public <T extends ModelAccessor> T load(Class<T> iface)
    {
        if (jsonKeyMapper == null) {
            return modelManager.readModelAccessor(source, iface);
        } else {
            return modelManager.readModelAccessor(source, iface, jsonKeyMapper);
        }
    }

    /**
     * visible for TaskSerDe
     */
    ObjectNode getSource()
    {
        return source;
    }

    @Override
    public String toString()
    {
        return source.toString();
    }
}
