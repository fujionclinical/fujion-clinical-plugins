package org.fujionclinical.plugin.flowsheet;

import edu.utah.kmm.model.cool.clinical.finding.SimpleObservation;

import java.time.LocalDateTime;

public class ObservationWrapper extends ResourceWrapper<SimpleObservation> {

    protected ObservationWrapper(SimpleObservation resource) {
        super(SimpleObservation.class, resource);
    }

    @Override
    protected LocalDateTime getDateTime() {
        return (LocalDateTime) resource.getEffective().getValue();
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
