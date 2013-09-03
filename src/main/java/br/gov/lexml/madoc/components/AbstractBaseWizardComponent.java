package br.gov.lexml.madoc.components;

import br.gov.lexml.madoc.execution.hosteditor.HostEditorReplacer;
import br.gov.lexml.madoc.schema.entity.BaseWizardType;

public abstract class AbstractBaseWizardComponent<B extends BaseWizardType, C> 
	implements BaseWizardComponent<B, C> {

	protected final B wizardElement;
	protected final HostEditorReplacer hostEditorReplacer;
	protected final ComponentController componentController;

	private C component;

	protected AbstractBaseWizardComponent(B wizardElement, ComponentController componentController){
		this.wizardElement = wizardElement;
		this.componentController = componentController;
		this.hostEditorReplacer = componentController.getHostEditorReplacer();
	}
	
	private void initialize() {
		component = createComponent();
		
		//register this object in ComponentController
		if (wizardElement!= null){
			componentController.register(this);
		}
	}
	
	protected abstract C createComponent();

//  Removido por questão de performance. Necessidade do método sob avaliação.
//	
//	@Override
//	public boolean equals(Object obj) {
//		if(obj == null) {
//			return false;
//		}
//		
//		if (super.equals(obj)){
//			return true;
//		}
//		
//		if (obj instanceof BaseWizardComponent<?,?>){
//			BaseWizardComponent<?,?> bwc = ((BaseWizardComponent<?,?>)obj);
//			if (bwc.getId()!= null && !bwc.getId().equals("") && getId()!=null && !getId().equals("")){
//				return ((BaseWizardComponent<?,?>)obj).getId().equals(getId());
//			}
//		}
//		
//		return false;
//	}
	
	@Override
	public String toString() {
		return "WizardElement id="+getId()+" ("+getClass().getName()+")";
	}
	
	@Override
	public String getId() {
		return wizardElement.getId();
	}
	
	@Override
	public B getWizardElement() {
		return wizardElement;
	}
	
	@Override
	public C getComponent() {
		if(component == null) {
			initialize();
		}
		return component;
	}
	
	@Override
	public ComponentController getComponentController() {
		return componentController;
	}

}
