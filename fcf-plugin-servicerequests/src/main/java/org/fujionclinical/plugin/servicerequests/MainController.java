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
package org.fujionclinical.plugin.servicerequests;

import org.coolmodel.clinical.action.ServiceOrder;
import org.coolmodel.mediator.datasource.DataSource;
import org.fujionclinical.sharedforms.controller.ResourceListView;

import java.util.List;

/**
 * Controller for patient family history display.
 */
public class MainController extends ResourceListView<ServiceOrder, ServiceOrder, DataSource> {

    @Override
    protected void setup() {
        setup(ServiceOrder.class, "Procedures", "Procedure Detail", "subject={{patient}}", 1, "Procedure", "Date", "Status",
                "Notes");
    }

    @Override
    protected void populate(
            ServiceOrder serviceOrder,
            List<Object> columns) {
        columns.add(serviceOrder.getOrderable().getOrderable());
        columns.add(serviceOrder.getRequestedOn());
        columns.add(serviceOrder.getStatus());
        columns.add(serviceOrder.getNotes());
    }

    @Override
    protected void initModel(List<ServiceOrder> entries) {
        model.addAll(entries);
    }

}
