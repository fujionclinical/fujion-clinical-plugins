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
package org.fujionclinical.plugin.diagnosticreports;

import org.coolmodel.clinical.finding.DiagnosticReport;
import org.coolmodel.mediator.datasource.DataSource;
import org.fujionclinical.sharedforms.controller.ResourceListView;

import java.util.List;

/**
 * Controller for patient diagnostic reports display.
 */
public class MainController extends ResourceListView<DiagnosticReport, DiagnosticReport, DataSource> {

    @Override
    protected void setup() {
        setup(DiagnosticReport.class, "Diagnostic Reports", "Report Detail", "subject={{patient}}", 1, "Date", "Type",
                "Conclusion", "Notes");
    }

    @Override
    protected void populate(
            DiagnosticReport report,
            List<Object> columns) {
        columns.add(report.getEffective());
        columns.add(report.getCode());
        columns.add(report.getConclusion());
        columns.add(report.getNotes());
    }

    @Override
    protected void initModel(List<DiagnosticReport> entries) {
        model.addAll(entries);
    }

}
