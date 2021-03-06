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

import edu.utah.kmm.model.cool.clinical.finding.BloodPressure;
import edu.utah.kmm.model.cool.clinical.finding.ComposableObservation;
import edu.utah.kmm.model.cool.clinical.finding.ObservationalFinding;
import edu.utah.kmm.model.cool.clinical.finding.SimpleObservation;
import edu.utah.kmm.model.cool.mediator.datasource.DataSource;
import org.fujionclinical.sharedforms.controller.ResourceListView;

import java.util.List;

/**
 * Controller for patient observations display.
 */
public class MainController extends ResourceListView<ObservationalFinding, SimpleObservation<?>, DataSource> {

    @Override
    protected void setup() {
        setup(ObservationalFinding.class, "Observations", "Observation Detail", "subject={{patient}}", 1, "", "Date",
                "Status", "Result", "Ref Range");
    }

    @Override
    protected void populate(
            SimpleObservation<?> observation,
            List<Object> columns) {
        columns.add(observation.getCode());
        columns.add(observation.getEffective());
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
            if (observation instanceof ComposableObservation) {
                ComposableObservation<?> obs = (ComposableObservation<?>) observation;
                model.add(obs);
                addObservations(obs.getEntries());
            } else if (observation instanceof BloodPressure) {
                BloodPressure obs = (BloodPressure) observation;
                model.add(obs.getSystolic());
                model.add(obs.getDiastolic());
            } else if (observation instanceof SimpleObservation) {
                model.add((SimpleObservation<?>) observation);
            }
        }
    }

}
