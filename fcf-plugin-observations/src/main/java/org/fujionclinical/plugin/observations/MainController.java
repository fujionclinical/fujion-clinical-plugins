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
package org.fujionclinical.plugin.observations;

import org.coolmodel.clinical.finding.BloodPressure;
import org.coolmodel.clinical.finding.ObservationalFinding;
import org.coolmodel.clinical.finding.Panel;
import org.coolmodel.clinical.finding.QuantitativeObservation;
import org.coolmodel.mediator.datasource.DataSource;
import org.fujionclinical.sharedforms.controller.ResourceListView;

import java.util.List;

/**
 * Controller for patient observations display.
 */
public class MainController extends ResourceListView<ObservationalFinding, QuantitativeObservation, DataSource> {

    @Override
    protected void setup() {
        setup(ObservationalFinding.class, "Observations", "Observation Detail", "subject={{patient}}", 1, "", "Date",
                "Status", "Result", "Ref Range");
    }

    @Override
    protected void populate(
            QuantitativeObservation observation,
            List<Object> columns) {
        columns.add(observation.getCode());
        columns.add(observation.getEffectiveDateTime());
        columns.add(observation.getStatus());
        columns.add(observation.getValue());
        columns.add(observation.getReferenceRange());
    }

    @Override
    protected void initModel(List<ObservationalFinding> entries) {
        addObservations(entries);
    }

    private void addObservations(List<? extends ObservationalFinding> src) {
        for (ObservationalFinding observation : src) {
            if (observation instanceof Panel) {
                Panel<?> obs = (Panel<?>) observation;
                addObservations(obs.getItems());
            } else if (observation instanceof BloodPressure) {
                BloodPressure obs = (BloodPressure) observation;
                model.add(obs.getSystolic());
                model.add(obs.getDiastolic());
            } else if (observation instanceof QuantitativeObservation) {
                model.add((QuantitativeObservation) observation);
            }
        }
    }

}
