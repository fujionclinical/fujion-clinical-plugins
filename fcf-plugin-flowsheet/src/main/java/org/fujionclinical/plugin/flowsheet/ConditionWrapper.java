package org.fujionclinical.plugin.flowsheet;

import org.coolmodel.clinical.finding.ProblemListEntry;

import java.time.OffsetDateTime;

public class ConditionWrapper extends ResourceWrapper<ProblemListEntry> {

    public ConditionWrapper(ProblemListEntry resource) {
        super(ProblemListEntry.class,resource);
    }

    @Override
    protected OffsetDateTime getDateTime() {
        return resource.getRecordedOn();
    }

    @Override
    protected String getFormattedValue() {
        return resource.getAssertion().getCode().getDisplayText();
    }

    @Override
    protected String getLabel() {
        return "Problem";
    }

}
