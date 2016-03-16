package org.bimserver.runmodelcompare;

import org.bimserver.interfaces.objects.SInternalServicePluginConfiguration;
import org.bimserver.models.log.AccessMethod;
import org.bimserver.models.store.ObjectDefinition;
import org.bimserver.models.store.ServiceDescriptor;
import org.bimserver.models.store.StoreFactory;
import org.bimserver.models.store.Trigger;
import org.bimserver.plugins.PluginConfiguration;
import org.bimserver.plugins.PluginContext;
import org.bimserver.plugins.services.ServicePlugin;
import org.bimserver.shared.exceptions.PluginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunModelComparePlugin extends ServicePlugin  {

	private static final Logger LOGGER = LoggerFactory.getLogger(RunModelComparePlugin.class);
	private boolean initialized;
	private static final String NAMESPACE = "http://bimserver.org/runModelComparePlugin";
	
	@Override
	public void init(PluginContext pluginContext) throws PluginException {
		super.init(pluginContext);
	}

	@Override
	public void register(long uoid,
			SInternalServicePluginConfiguration internalService,
			PluginConfiguration pluginConfiguration) 
	{
		ServiceDescriptor simplifiedModel= StoreFactory.eINSTANCE.createServiceDescriptor();
		simplifiedModel.setProviderName("BIMserver");
		simplifiedModel.setIdentifier("" + internalService.getOid());
		simplifiedModel.setName("Run Model Compare service plugin");
		simplifiedModel.setDescription("Run a model compare plugin on BimServer");
		simplifiedModel.setNotificationProtocol(AccessMethod.INTERNAL);
		simplifiedModel.setReadRevision(true);
		simplifiedModel.setWriteExtendedData(NAMESPACE);
		simplifiedModel.setTrigger(Trigger.NEW_REVISION);
		registerNewRevisionHandler(uoid, 
								   simplifiedModel, 
								   new RunModelCompareNewRevisionHandler() );
		LOGGER.info("Run Model Compare service plugin loaded");
	}

	@Override
	public String getTitle() {
		return "RunModelComparePlugin";
	}

	@Override
	public ObjectDefinition getSettingsDefinition() {
		ObjectDefinition objectDefinition = StoreFactory.eINSTANCE.createObjectDefinition();
		return objectDefinition;
	}

	@Override
	public void unregister(SInternalServicePluginConfiguration internalService) {
	}
	
}
