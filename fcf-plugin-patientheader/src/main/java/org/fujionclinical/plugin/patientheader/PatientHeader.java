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
package org.fujionclinical.plugin.patientheader;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.coolmodel.core.datatype.Identifier;
import org.coolmodel.core.terminology.ConceptSet;
import org.coolmodel.foundation.datatype.Address;
import org.coolmodel.foundation.datatype.ContactPoint;
import org.coolmodel.foundation.datatype.PersonName;
import org.coolmodel.foundation.entity.Person;
import org.coolmodel.mediator.common.Formatters;
import org.coolmodel.util.PersonUtils;
import org.fujion.annotation.EventHandler;
import org.fujion.annotation.WiredComponent;
import org.fujion.common.DateUtil;
import org.fujion.common.MiscUtil;
import org.fujion.common.StrUtil;
import org.fujion.component.*;
import org.fujionclinical.api.cool.patient.PatientContext;
import org.fujionclinical.api.event.IEventSubscriber;
import org.fujionclinical.api.security.SecurityUtil;
import org.fujionclinical.api.user.User;
import org.fujionclinical.patientselection.common.PatientSelection;
import org.fujionclinical.shell.elements.ElementPlugin;
import org.fujionclinical.shell.plugins.PluginController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Controller for patient header plugin.
 */
public class PatientHeader extends PluginController {

    private static final Log log = LogFactory.getLog(PatientHeader.class);

    @WiredComponent
    private Toolbar tbPatient;

    @WiredComponent
    private Button btnDetail;

    @WiredComponent
    private Popup popDetail;

    @WiredComponent(value = "popDetail.pnlDetail")
    private Paneview pnlDetail;

    @WiredComponent
    private Label lblName;

    @WiredComponent
    private Label lblGender;

    @WiredComponent
    private Label lblDOBLabel;

    @WiredComponent
    private Label lblDOB;

    @WiredComponent
    private Label lblDODLabel;

    @WiredComponent
    private Label lblDOD;

    @WiredComponent
    private Label lblUser;

    private String noSelection;

    private Person patient;

    private String patientName;

    private boolean needsDetail = true;

    private final IEventSubscriber<Person> patientChangeListener = (event, patient) -> setPatient(patient);

    private boolean showUser = true;

    @Override
    public void afterInitialized(BaseComponent comp) {
        super.afterInitialized(comp);
        noSelection = lblName.getLabel();
        User user = SecurityUtil.getAuthenticatedUser();
        lblUser.setVisible(showUser);

        if (user == null) {
            setLabel(lblUser, "Unknown User", null);
        } else {
            setLabel(lblUser, user.getFullName() + " @ " + user.getSecurityDomain().getName(), null);
        }

        setPatient(PatientContext.getActivePatient());
        PatientContext.getPatientContext().addListener(patientChangeListener);
    }

    @Override
    public void onLoad(ElementPlugin plugin) {
        super.onLoad(plugin);
        plugin.registerProperties(this, "showUser");
    }

    @Override
    public void onUnload() {
        PatientContext.getPatientContext().removeListener(patientChangeListener);
    }

    @EventHandler(value = "click", target = "lnkSelect")
    private void onClick$lnkSelect() {
        PatientSelection.show(true);
    }

    @EventHandler(value = "click", target = "@btnDetail")
    private void onClick$btnDetail() {
        buildDetail();
        popDetail.open(btnDetail, "left top", "right bottom");
    }

    private void setPatient(Person patient) {
        this.patient = patient;
        hideLabels();
        needsDetail = true;
        pnlDetail.destroyChildren();

        if (log.isDebugEnabled()) {
            log.debug("patient: " + patient);
        }

        if (patient == null) {
            lblName.setLabel(noSelection);
            btnDetail.setDisabled(true);
            return;
        }

        btnDetail.setDisabled(false);
        patientName = PersonUtils.getFullName(patient);
        Identifier mrn = PersonUtils.getMRN(patient);
        lblName.setLabel(patientName + (mrn == null ? "" : "  (" + mrn.getId() + ")"));
        setLabel(lblDOB, formatDateAndAge(patient.getBirthDate()), lblDOBLabel);
        setLabel(lblDOD, patient.getDeathDate(), lblDODLabel);
        setLabel(lblGender, patient.getGender(), null);
    }

    private String formatDateAndAge(LocalDate date) {
        String formatted = Formatters.format(date, null);
        return formatted == null ? null : formatted + " (" + DateUtil.formatAge(date) + ")";
    }

    private void setLabel(
            Label label,
            Object value,
            BaseUIComponent associatedComponent) {
        String formatted = Formatters.format(value, null);
        label.setLabel(formatted);
        label.setVisible(formatted != null);

        if (associatedComponent != null) {
            associatedComponent.setVisible(label.isVisible());
        }
    }

    private void hideLabels() {
        for (Label child : tbPatient.getChildren(Label.class)) {
            if (child != lblName) {
                child.setVisible(false);
            }
        }
    }

    private void buildDetail() {
        if (!needsDetail) {
            return;
        }

        needsDetail = false;

        Pane header = null;

        // Names

        for (PersonName name : patient.getName()) {

            String nm = name.toString();

            if (patientName.equals(nm)) {
                continue;
            }

            if (header == null) {
                header = addHeader("Other Names");
            }

            addDetail(header, nm, null);
        }

        // Identifiers

        header = null;

        if (patient.getIdentifiers() != null) {
            for (Identifier id : patient.getIdentifiers()) {
                if (header == null) {
                    header = addHeader("Identifiers");
                }

                String category = MiscUtil.asNull(() -> id.getUse().getPreferredName().toLowerCase());
                String system = StringUtils.defaultString(id.getSystem().toString());
                String value = StringUtils.defaultString(id.getId());

                if (!StringUtils.isEmpty(system)) {
                    value += " (" + system + ")";
                }

                addDetail(header, value, category);
            }
        }

        // Communication

        header = null;

        for (ConceptSet language : patient.getLanguage()) {
            String languageStr = Formatters.formatConceptSet(language);

            if (header == null) {
                header = addHeader("Communication");
                languageStr += " (preferred)";
            }

            addDetail(header, languageStr, null);
        }

        // Telecom info

        header = null;

        List<ContactPoint> contactPoints = new ArrayList<>(patient.getContact());
        contactPoints.sort(Comparator.comparingInt(ContactPoint::getRank));

        for (ContactPoint contactPoint : contactPoints) {
            if (header == null) {
                header = addHeader("Contact Details");
            }

            String type = MiscUtil.withDefault("", () -> contactPoint.getSystem().getPreferredName());
            String use = MiscUtil.asNull(() -> contactPoint.getUse().getPreferredName());

            if (!StringUtils.isEmpty(use)) {
                type += " (" + use + ")";
            }

            addDetail(header, contactPoint.getValue(), type);
        }

        // Address(es)

        header = null;

        for (Address address : patient.getAddress()) {
            if (header == null) {
                header = addHeader("Addresses");
            }

            String type = MiscUtil.withDefault("", () -> address.getType().getPreferredName());
            String use = MiscUtil.asNull(() -> address.getUse().getPreferredName());

            if (!StringUtils.isEmpty(type)) {
                use += " (" + type + ")";
            }

            addDetail(header, " ", use);

            for (String line : address.getLine()) {
                addDetail(header, line, null);
            }

            StringBuilder line = new StringBuilder();
            line.append(address.getCity()).append(", ");
            line.append(address.getState()).append("  ");
            line.append(address.getPostalCode());
            addDetail(header, line.toString(), null);
        }

        if (pnlDetail.getFirstChild() == null) {
            addHeader(StrUtil.getLabel("fcfpatientheader.nodetail.label"));
        }
    }

    private Pane addHeader(String text) {
        Pane header = new Pane();
        header.setTitle(text);
        pnlDetail.addChild(header);
        return header;
    }

    private void addDetail(
            Pane header,
            String text,
            String label) {
        if (StringUtils.isEmpty(text)) {
            return;
        }

        if (label != null) {
            Label lbl = new Label(label);
            lbl.addClass("fcf-patientheader-label");
            header.addChild(lbl);
        }

        header.addChild(new Label(text));
    }

    public boolean getShowUser() {
        return showUser;
    }

    public void setShowUser(boolean showUser) {
        this.showUser = showUser;

        if (lblUser != null) {
            lblUser.setVisible(showUser);
        }
    }

}
