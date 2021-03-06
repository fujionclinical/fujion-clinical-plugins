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
package org.fujionclinical.plugin.conditions;

import edu.utah.kmm.model.cool.clinical.finding.Condition;
import edu.utah.kmm.model.cool.mediator.datasource.DataSource;
import org.fujionclinical.sharedforms.controller.ResourceListView;

import java.util.List;

/**
 * Controller for patient conditions display.
 */
public class MainController extends ResourceListView<Condition, Condition, DataSource> {
    
    @Override
    protected void setup() {
        setup(Condition.class, "Conditions", "Condition Detail", "subject={{patient}}", 1, "Condition", "Onset", "Status",
                "Notes");
    }
    
    @Override
    protected void populate(Condition condition, List<Object> columns) {
        columns.add(condition.getCode().getDisplayText());
        columns.add(condition.getOnset().getValue());
        columns.add(condition.getClinicalStatus());
        columns.add(condition.getNotes());
    }
    
    @Override
    protected void initModel(List<Condition> entries) {
        model.addAll(entries);
    }
    
}
