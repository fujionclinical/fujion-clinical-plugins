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
package org.fujionclinical.plugin.flowsheet;

import edu.utah.kmm.common.dates.DateUtils;
import org.coolmodel.clinical.finding.Condition;
import org.coolmodel.clinical.finding.SimpleObservation;
import org.coolmodel.foundation.core.Identifiable;
import org.coolmodel.mediator.datasource.DataSource;
import org.coolmodel.mediator.datasource.DataSources;
import org.coolmodel.mediator.expression.parser.Expression;
import org.coolmodel.mediator.expression.parser.ExpressionParser;
import org.coolmodel.mediator.query.QueryContext;
import org.coolmodel.mediator.query.QueryContextImpl;
import org.fujion.annotation.WiredComponent;
import org.fujion.component.*;
import org.fujionclinical.api.cool.patient.PatientContext;
import org.fujionclinical.shell.plugins.PluginController;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Controller for flowsheet display.
 */
@SuppressWarnings("rawtypes")
public class MainController extends PluginController {

    private static final String CONDITION_QUERY = "Condition?subject={{patient}}&_sort=date:asc";

    private static final Expression<Condition> conditionExpression = ExpressionParser.getInstance().parse(CONDITION_QUERY);

    private static final String OBSERVATION_QUERY = "Observation?subject={{patient}}&_sort=date:asc";

    private static final Expression<SimpleObservation> observationExpression = ExpressionParser.getInstance().parse(OBSERVATION_QUERY);

    private DataSource dataSource;

    @WiredComponent
    private Rows rows;

    @WiredComponent
    private Columns columns;

    public void setDataSource(String dataSourceId) {
        this.dataSource = DataSources.get(dataSourceId);
    }

    public void refresh() {
        QueryContext queryContext = new QueryContextImpl();
        queryContext.setParam("patient", PatientContext.getActivePatient());
        List<ResourceWrapper> conditions = fetch(Condition.class, conditionExpression, queryContext, ConditionWrapper::new);
        List<ResourceWrapper> observations = fetch(SimpleObservation.class, observationExpression, queryContext, ObservationWrapper::new);
        rows.destroyChildren();
        columns.destroyChildren();
        createFlowsheet(conditions, observations);
    }

    private <T extends Identifiable> List<ResourceWrapper> fetch(
            Class<T> type,
            Expression<T> expression,
            QueryContext queryContext,
            Function<T, ResourceWrapper<T>> factory) {
        return dataSource.getModelDAO(type)
                .search(expression, queryContext).getAsList().stream()
                .map(factory)
                .collect(Collectors.toList());
    }

    private void createFlowsheet(List<ResourceWrapper>... resourceLists) {
        createColumns(resourceLists);
        createRows(resourceLists);
    }

    private void createColumns(List<ResourceWrapper>... resourceLists) {
        columns.destroyChildren();
        Set<OffsetDateTime> dates = new TreeSet<>();

        for (List<ResourceWrapper> resources : resourceLists) {
            resources.forEach(resource -> dates.add(resource.getDateTime()));
        }

        columns.addChild(new Column());

        dates.forEach(date -> {
            Column column = new Column(DateUtils.formatDate(date));
            column.setData(date);
            columns.addChild(column);
        });
    }

    @SafeVarargs
    private final void createRows(List<ResourceWrapper>... resourceLists) {
        rows.destroyChildren();

        for (List<ResourceWrapper> resources : resourceLists) {
            for (ResourceWrapper resource : resources) {
                Row row = findRow(resource.getLabel());
                Column column = findColumn(resource.getDateTime());
                Rowcell cell = (Rowcell) row.getChildAt(column.getIndex());
                cell.addChild(new Cell(resource.getFormattedValue()));
            }
        }
    }

    private void addResource(ResourceWrapper resource) {
        Row row = new Row();
    }

    private Row findRow(String label) {
        return rows.getChildren().stream()
                .map(Row.class::cast)
                .filter(row -> label.equals(row.getFirstChild(Cell.class).getLabel()))
                .findAny()
                .orElse(newRow(label));
    }

    private Row newRow(String label) {
        Row row = new Row();

        for (int i = 0; i < columns.getChildCount(); i++) {
            row.addChild(new Rowcell(i == 0 ? label : null));
        }

        rows.addChild(row);
        return row;
    }

    private Column findColumn(OffsetDateTime date) {
        return columns.getChildren().stream()
                .map(Column.class::cast)
                .filter(column -> date.equals(column.getData()))
                .findAny()
                .orElse(null);
    }

}
