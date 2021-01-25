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
package org.fujionclinical.plugin.orders;

import edu.utah.kmm.model.cool.clinical.action.MedicationRequest;
import edu.utah.kmm.model.cool.clinical.action.ServiceRequest;
import edu.utah.kmm.model.cool.clinical.action.SimpleMedicationRequest;
import edu.utah.kmm.model.cool.foundation.core.Identifiable;
import edu.utah.kmm.model.cool.mediator.datasource.DataSource;
import edu.utah.kmm.model.cool.mediator.query.QueryContext;
import edu.utah.kmm.model.cool.mediator.query.QueryContextImpl;
import edu.utah.kmm.model.cool.mediator.query.service.DAOQueryService;
import org.fujionclinical.sharedforms.controller.AbstractResourceListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for patient medication requests display.
 */
public class MainController extends AbstractResourceListView<Identifiable, Identifiable, DataSource> {

    private static final String QUERY = "subject={{patient}}";

    private final QueryContext queryContext = new QueryContextImpl();

    private DAOQueryService<ServiceRequest> queryServiceRequest;

    private DAOQueryService<MedicationRequest> queryMedicationRequest;

    @Override
    protected void setup() {
        queryServiceRequest = new DAOQueryService<>(getDataSource(), ServiceRequest.class, QUERY);
        queryMedicationRequest = new DAOQueryService<>(getDataSource(), MedicationRequest.class, QUERY);
        setup(Identifiable.class, "Orders", "Order Detail", "", 1, "Type", "Date", "Order", "Notes");
    }

    @Override
    protected void populate(
            Identifiable item,
            List<Object> columns) {
        if (item instanceof ServiceRequest) {
            render((ServiceRequest) item, columns);
        } else if (item instanceof MedicationRequest) {
            render((MedicationRequest) item, columns);
        }
    }

    private void render(
            ServiceRequest request,
            List<Object> columns) {
        columns.add("Procedure");
        columns.add(request.getOrderedOn());
        columns.add(request.getCode());
        columns.add(request.getNotes());
    }

    private void render(
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
    protected void initModel(List<Identifiable> entries) {
        model.addAll(entries);
    }

    @Override
    protected void requestData() {
        startBackgroundThread(map -> map.put("results", fetchData()));
    }

    private List<Identifiable> fetchData() {
        List<Identifiable> results = new ArrayList<>();
        queryContext.setParam("patient", getPatient() == null ? null : getPatient().getDefaultId().getId());
        results.addAll(queryServiceRequest.fetch(queryContext).get());
        results.addAll(queryMedicationRequest.fetch(queryContext).get());
        return results;
    }

}
