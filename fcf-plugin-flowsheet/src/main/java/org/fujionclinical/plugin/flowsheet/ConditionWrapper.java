package org.fujionclinical.plugin.flowsheet;

import edu.utah.kmm.model.cool.clinical.finding.Condition;

import java.time.LocalDateTime;

public class ConditionWrapper extends ResourceWrapper<Condition> {

    public ConditionWrapper(Condition resource) {
        super(Condition.class,resource);
    }

    @Override
    protected LocalDateTime getDateTime() {
        return resource.getRecordedDate();
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
