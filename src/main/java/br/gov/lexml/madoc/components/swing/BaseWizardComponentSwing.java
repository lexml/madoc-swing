package br.gov.lexml.madoc.components.swing;

import javax.swing.JComponent;

import br.gov.lexml.madoc.components.BaseWizardComponent;
import br.gov.lexml.madoc.schema.entity.BaseWizardType;

interface BaseWizardComponentSwing<B extends BaseWizardType, C extends JComponent> extends BaseWizardComponent<B, C> {

}
