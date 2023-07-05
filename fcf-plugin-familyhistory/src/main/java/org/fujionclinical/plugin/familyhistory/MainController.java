/*
 * #%L
 * Fujion Clinical Framework
 * %%
 * Copyright (C) 2020 fujionclinical.org
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This Source Code Form is also subject to the terms of the Health-Related
 * Additional Disclaimer of Warranty and Limitation of Liability available at
 *
 *      http://www.fujionclinical.org/licensing/disclaimer
 *
 * #L%
 */
package org.fujionclinical.plugin.familyhistory;

import org.coolmodel.clinical.finding.AssertionOfPresence;
import org.coolmodel.clinical.finding.FamilyMemberHistory;
import org.coolmodel.mediator.datasource.DataSource;
import org.fujionclinical.sharedforms.controller.ResourceListView;

import java.util.List;

/**
 * Controller for patient family history display.
 */
public class MainController extends ResourceListView<FamilyMemberHistory, FamilyMemberHistory, DataSource> {

    @Override
    protected void setup() {
        setup(FamilyMemberHistory.class, "Family History", "Family History Detail", "subject={{patient}}", 1,
                "Relation", "Condition", "Status", "Notes");
    }

    @Override
    protected void populate(
            FamilyMemberHistory relation,
            List<Object> columns) {
        columns.add(relation.getRelationship());
        AssertionOfPresence condition = relation.getCondition();
        columns.add(condition.getCode());
        columns.add(condition.getClinicalStatus());
        columns.add(condition.getNotes());
    }

    @Override
    protected void initModel(List<FamilyMemberHistory> entries) {
        model.addAll(entries);
    }

}
