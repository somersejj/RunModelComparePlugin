package org.bimserver.runmodelcompare;

import java.util.List;

import org.bimserver.LocalDevSetup;
import org.bimserver.interfaces.objects.SInternalServicePluginConfiguration;
import org.bimserver.interfaces.objects.SObjectType;
import org.bimserver.interfaces.objects.SProject;
import org.bimserver.interfaces.objects.SCompareType;
import org.bimserver.models.log.AccessMethod;
import org.bimserver.models.store.ObjectDefinition;
import org.bimserver.models.store.ServiceDescriptor;
import org.bimserver.models.store.StoreFactory;
import org.bimserver.models.store.Trigger;
import org.bimserver.plugins.PluginConfiguration;
import org.bimserver.plugins.PluginManagerInterface;
import org.bimserver.plugins.services.BimServerClientInterface;
import org.bimserver.plugins.services.NewRevisionHandler;
import org.bimserver.plugins.services.ServicePlugin;
import org.bimserver.shared.exceptions.PluginException;
import org.bimserver.shared.exceptions.PublicInterfaceNotFoundException;
import org.bimserver.shared.exceptions.ServerException;
import org.bimserver.shared.exceptions.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunModelComparePlugin extends ServicePlugin  {

	private static final Logger LOGGER = LoggerFactory.getLogger(RunModelComparePlugin.class);
	private boolean initialized;
	private static final String NAMESPACE = "http://bimserver.org/runModelComparePlugin";
	
	@Override
    public void init(PluginManagerInterface pluginManager) throws PluginException {
        super.init(pluginManager);
        initialized = true;
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
								   new NewRevisionHandler() 
		{
			public void newRevision(BimServerClientInterface bimServerClientInterface, long poid, long roid, String userToken, long soid, SObjectType settings) 
					    throws ServerException, UserException 
			{
				SProject project;
				try {
					project = bimServerClientInterface.getBimsie1ServiceInterface().getProjectByPoid(poid);
					LOGGER.info("Run Model Compare service plugin is called");
	 				Long sId = bimServerClientInterface.getBimsie1ServiceInterface().getSerializerByName("Ifc2x3").getOid();
	 			    Long roid1 = new Long(roid);
	 			    List<Long> list = project.getRevisions();
	 			    for (Long roid2 : list)
	 			    {
	 			    	if (roid2.longValue() != roid1.longValue())
	 			    	{
		 			      LOGGER.info("Comparing version: " + roid1 + "with: " + roid2 + " using serializer: " + sId);
			 			  final BimServerClientInterface client = LocalDevSetup.setupJson("http://localhost:8080");
	 				      Long compareId = client.getServiceInterface().downloadCompareResults(sId , roid1, roid2, new Long(393298), SCompareType.values()[0], true);
		 				  LOGGER.info("Compare id: " + compareId);
	 			    	}
	 			    }
				} catch (PublicInterfaceNotFoundException e) {
					e.printStackTrace();
				}
 			}
		}
		);
		LOGGER.info("Run Model Compare service plugin loaded");
	}

	@Override
	public void unregister(SInternalServicePluginConfiguration internalService) {
		 	
	}
	@Override
	public boolean isInitialized() {
		return initialized;
	}
	
	public String getDescription() {
		return "Simplified Model service plugin" ;
	}

	public String getDefaultName() {
		return  "Simplified Model service plugin" ;
	}

	public String getVersion() {
		return "0.1" ;
	}

	public ObjectDefinition getSettingsDefinition() {
		return null;
	}

	@Override
	public String getTitle() {
		return "Simplified Model service plugin" ;
	}
}
