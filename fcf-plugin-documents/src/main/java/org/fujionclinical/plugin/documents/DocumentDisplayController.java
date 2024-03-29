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
package org.fujionclinical.plugin.documents;

import org.coolmodel.clinical.finding.Document;
import org.coolmodel.mediator.query.QueryContext;
import org.coolmodel.mediator.query.filter.DateQueryFilter;
import org.coolmodel.mediator.query.service.InMemoryQueryService;
import org.fujion.annotation.EventHandler;
import org.fujion.annotation.WiredComponent;
import org.fujion.component.Combobox;
import org.fujion.component.Label;
import org.fujion.component.Row;
import org.fujion.event.EventUtil;
import org.fujion.model.IListModel;
import org.fujionclinical.sharedforms.controller.AbstractGridController;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Controller for displaying the contents of selected documents.
 */
public class DocumentDisplayController extends AbstractGridController<Document, Document> {

    private final DocumentDisplayComboRenderer comboRenderer = new DocumentDisplayComboRenderer();

    private List<Document> documents;

    @WiredComponent
    private Label lblInfo;

    @WiredComponent
    private Combobox cboHeader;

    @SuppressWarnings("unchecked rawtypes")
    public DocumentDisplayController() {
        super(new InMemoryQueryService<>(), "fcfdocuments", "DOCUMENT", "documentsPrint.css", "patient");
        ((InMemoryQueryService) getService()).setQueryResult(() -> documents);
    }

    @Override
    protected void initializeController() {
        super.initializeController();
        cboHeader.setRenderer(comboRenderer);
        grid.getRows().setRenderer(new DocumentDisplayRenderer());
    }

    @Override
    protected void prepareQueryContext(QueryContext context) {
        context.setParam("documents", documents);
    }

    /**
     * This view should be closed when the patient context changes.
     */
    @Override
    protected void onParameterChanged(SupplementalQueryParam<?> param) {
        closeView();
    }

    /**
     * Suppress data fetch if there are no documents in the view.
     */
    @Override
    protected void fetchData() {
        if (documents != null) {
            super.fetchData();
        }
    }

    @Override
    protected List<Document> toModel(List<Document> results) {
        return results;
    }

    /**
     * Scroll to document with same header.
     */
    @EventHandler(value = "change", target = "@cboHeader")
    private void onChange$cboHeader() {
        Document doc = (Document) cboHeader.getSelectedItem().getData();

        for (Row row : grid.getRows().getChildren(Row.class)) {
            Document doc2 = (Document) row.getData();

            if (doc == doc2) {
                row.scrollIntoView();
                break;
            }
        }
    }

    /**
     * Clears any displayed documents and reverts to document selection mode.
     */
    @EventHandler(value = "click", target = "btnReturn")
    private void onClick$btnReturn() {
        closeView();
    }

    /**
     * Clears any displayed documents and reverts to document selection mode.
     */
    protected void closeView() {
        documents = null;
        setModel(null);
        EventUtil.post("viewOpen", root, false);
    }

    /**
     * Sets the documents to be displayed and updates the displayed count.
     *
     * @param documents The documents to be displayed.
     */
    protected void setDocuments(List<Document> documents) {
        this.documents = documents;
        lblInfo.setLabel((documents == null ? 0 : documents.size()) + " document(s)");
        refresh();
    }

    /**
     * Updates the header selector when the model changes.
     */
    @Override
    protected void setModel(IListModel<Document> model) {
        super.setModel(model);
        cboHeader.setModel(model);
        cboHeader.setValue(null);
    }

    @Override
    public OffsetDateTime getDateByType(
            Document result,
            DateQueryFilter.DateType dateType) {
        return null;
    }

}
