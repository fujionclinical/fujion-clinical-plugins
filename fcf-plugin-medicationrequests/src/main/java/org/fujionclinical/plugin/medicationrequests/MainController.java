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
package org.fujionclinical.plugin.medicationrequests;

import edu.utah.kmm.model.cool.clinical.action.MedicationRequest;
import edu.utah.kmm.model.cool.clinical.action.SimpleMedicationRequest;
import edu.utah.kmm.model.cool.mediator.datasource.DataSource;
import org.fujionclinical.sharedforms.controller.ResourceListView;

import java.util.List;

/**
 * Controller for patient medication requests display.
 */
public class MainController extends ResourceListView<MedicationRequest, MedicationRequest, DataSource> {

    @Override
    protected void setup() {
        setup(MedicationRequest.class, "Medication Orders", "Order Detail", "subject={{patient}}", 1, "Medication",
                "Date", "Status", "Sig");
    }

    @Override
    protected void populate(
            MedicationRequest medicationRequest,
            List<Object> columns) {
        columns.add(medicationRequest.getMedication().getCode().getDisplayText());
        columns.add(medicationRequest.getDateWritten());
        columns.add(medicationRequest.getStatus());
        String sig = null;

        if (medicationRequest instanceof SimpleMedicationRequest) {
            SimpleMedicationRequest request = (SimpleMedicationRequest) medicationRequest;
            sig = request.hasDosageInstruction() ? request.getDosageInstruction().getText() : null;
        }

        columns.add(sig);
    }

    @Override
    protected void initModel(List<MedicationRequest> entries) {
        model.addAll(entries);
    }

}
