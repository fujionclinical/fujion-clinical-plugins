package org.fujionclinical.plugin.flowsheet;

import org.coolmodel.clinical.finding.Condition;

import java.time.OffsetDateTime;

public class ConditionWrapper extends ResourceWrapper<Condition> {

    public ConditionWrapper(Condition resource) {
        super(Condition.class,resource);
    }

    @Override
    protected OffsetDateTime getDateTime() {
        return resource.getRecordedOn();
    }

    @Override
    protected String getFormattedValue() {
        return resource.getCode().getDisplayText();
    }

    @Override
    protected String getLabel() {
        return "Problem";
    }

}
