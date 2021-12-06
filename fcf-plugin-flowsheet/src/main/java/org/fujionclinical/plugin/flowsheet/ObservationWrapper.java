package org.fujionclinical.plugin.flowsheet;

import org.coolmodel.clinical.finding.SimpleObservation;

import java.time.OffsetDateTime;

public class ObservationWrapper extends ResourceWrapper<SimpleObservation> {

    protected ObservationWrapper(SimpleObservation resource) {
        super(SimpleObservation.class, resource);
    }

    @Override
    protected OffsetDateTime getDateTime() {
        return (OffsetDateTime) resource.getEffective().getValue();
    }

    @Override
    protected String getFormattedValue() {
        return resource.getValue().toString();
    }

    @Override
    protected String getLabel() {
        return resource.getCode().getDisplayText();
    }

}
