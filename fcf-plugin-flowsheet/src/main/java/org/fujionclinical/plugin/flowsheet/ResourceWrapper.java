package org.fujionclinical.plugin.flowsheet;

import org.coolmodel.foundation.core.Identifiable;

import java.time.LocalDateTime;

public abstract class ResourceWrapper<T extends Identifiable> {

    protected final T resource;

    protected final Class<T> type;

    protected ResourceWrapper(Class<T> type, T resource) {
        this.type = type;
        this.resource = resource;
    }

    protected abstract LocalDateTime getDateTime();

    protected abstract String getFormattedValue();

    protected abstract String getLabel();

}
