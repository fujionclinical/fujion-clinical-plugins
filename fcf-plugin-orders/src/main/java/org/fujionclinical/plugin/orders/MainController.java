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

import org.coolmodel.clinical.action.*;
import org.coolmodel.foundation.core.Identifiable;
import org.coolmodel.mediator.dao.DAOQueryService;
import org.coolmodel.mediator.datasource.DataSource;
import org.coolmodel.mediator.query.QueryContext;
import org.coolmodel.mediator.query.QueryContextImpl;
import org.fujionclinical.sharedforms.controller.AbstractResourceListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for patient medication requests display.
 */
public class MainController extends AbstractResourceListView<Identifiable, Identifiable, DataSource> {

    private static final String QUERY = "subject={{patient}}";

    private final QueryContext queryContext = new QueryContextImpl();

    private DAOQueryService<ServiceOrder> queryServiceOrder;

    private DAOQueryService<MedicationOrder> queryMedicationOrder;

    @Override
    protected void setup() {
        queryServiceOrder = new DAOQueryService<>(getDataSource(), ServiceOrder.class, QUERY);
        queryMedicationOrder = new DAOQueryService<>(getDataSource(), MedicationOrder.class, QUERY);
        setup(Identifiable.class, "Orders", "Order Detail", "", 1, "Type", "Date", "Order", "Notes");
    }

    @Override
    protected void populate(
            Identifiable item,
            List<Object> columns) {
        if (item instanceof ServiceRequest) {
            render((ServiceOrder) item, columns);
        } else if (item instanceof MedicationRequest) {
            render((MedicationOrder) item, columns);
        }
    }

    private void render(
            ServiceOrder serviceOrder,
            List<Object> columns) {
        columns.add("Procedure");
        columns.add(serviceOrder.getRequestedOn());
        columns.add(serviceOrder.getOrderable().getCode());
        columns.add(serviceOrder.getNotes());
    }

    private void render(
            MedicationOrder medicationOrder,
            List<Object> columns) {
        columns.add(medicationOrder.getOrderable().getOrderable().getDisplayText());
        columns.add(medicationOrder.getRequestedOn());
        columns.add(medicationOrder.getStatus());
        String sig = null;

        if (medicationOrder instanceof SimpleMedicationOrder) {
            SimpleMedicationRequest request = ((SimpleMedicationOrder) medicationOrder).getOrderable();
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
        results.addAll(queryServiceOrder.fetch(queryContext).get());
        results.addAll(queryMedicationOrder.fetch(queryContext).get());
        return results;
    }

}
