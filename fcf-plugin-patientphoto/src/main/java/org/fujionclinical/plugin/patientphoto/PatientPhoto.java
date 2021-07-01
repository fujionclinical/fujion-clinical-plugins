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
package org.fujionclinical.plugin.patientphoto;

import org.coolmodel.foundation.entity.Person;
import org.coolmodel.util.PersonUtils;
import org.fujion.annotation.WiredComponent;
import org.fujion.component.BaseComponent;
import org.fujion.component.Image;
import org.fujion.component.Label;
import org.fujion.component.Popup;
import org.fujionclinical.api.context.ISurveyResponse;
import org.fujionclinical.api.cool.patient.PatientContext;
import org.fujionclinical.personphoto.PersonPhoto;
import org.fujionclinical.ui.controller.FrameworkController;

/**
 * Controller for patient photo plugin.
 */
public class PatientPhoto extends FrameworkController implements PatientContext.IPatientContextSubscriber {

    @WiredComponent
    private PersonPhoto imgPhoto;

    @WiredComponent("popup.imgFullPhoto")
    private Image imgFullPhoto;

    @WiredComponent
    private Popup popup;

    @WiredComponent("popup.lblCaption")
    private Label lblCaption;

    @Override
    public void afterInitialized(BaseComponent comp) {
        super.afterInitialized(comp);
        committed();
    }

    @Override
    public void canceled() {
    }

    @Override
    public void committed() {
        Person patient = PatientContext.getActivePatient();
        imgPhoto.setPopup(null);
        imgPhoto.setPerson(patient);
        imgFullPhoto.setSrc(imgPhoto.getSrc());
        lblCaption.setLabel(PersonUtils.getFullName(patient));

        if (imgPhoto.hasPhoto()) {
            imgPhoto.setHint(null);
            imgPhoto.setPopup(popup);
        }
    }

    @Override
    public void pending(ISurveyResponse response) {
        response.accept();
    }

}
