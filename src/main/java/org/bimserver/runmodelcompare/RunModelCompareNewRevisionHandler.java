package org.bimserver.runmodelcompare;

import java.util.List;

import org.bimserver.LocalDevSetup;
import org.bimserver.interfaces.objects.SCompareType;
import org.bimserver.interfaces.objects.SModelComparePluginConfiguration;
import org.bimserver.interfaces.objects.SObjectType;
import org.bimserver.interfaces.objects.SProject;
import org.bimserver.interfaces.objects.SRevision;
import org.bimserver.plugins.services.BimServerClientInterface;
import org.bimserver.plugins.services.NewRevisionHandler;
import org.bimserver.shared.exceptions.PublicInterfaceNotFoundException;
import org.bimserver.shared.exceptions.ServerException;
import org.bimserver.shared.exceptions.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunModelCompareNewRevisionHandler  implements NewRevisionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(RunModelCompareNewRevisionHandler.class);
	private static final String NAMESPACE = "http://bimserver.org/runModelComparePlugin";

	public void newRevision(BimServerClientInterface bimServerClientInterface, long poid, long roid, String userToken, long soid, SObjectType settings) 
			    throws ServerException, UserException 
	{
		SProject project;
		try {
			project = bimServerClientInterface.getBimsie1ServiceInterface().getProjectByPoid(poid);
			LOGGER.info("Run Model Compare service plugin is called");
			Long sId = bimServerClientInterface.getBimsie1ServiceInterface().getSerializerByName("Ifc2x3tc1").getOid();
		    List<Long> list = project.getRevisions();
		    SRevision revision1 = bimServerClientInterface.getBimsie1ServiceInterface().getRevision(roid);
		    SRevision revision2 = null;
		    for (Long roid2 : list)
		    {
		        if (roid != roid2)
		    	{
		        	SRevision revision3 = bimServerClientInterface.getBimsie1ServiceInterface().getRevision(roid2);
			    	if (revision2 != null)
			    	{
			    		if (revision3.getDate().getTime() > revision2.getDate().getTime())
			    		    revision2 = revision3;
			    	}
			    	else 
					{
		    		    revision2 = revision3;					
			    	}
		    	}
		    }
	    	if (revision2.getOid() != roid)
	    	{
	    	  
		      LOGGER.info("Comparing version: " + revision1.getComment() + "(" + revision1.getDate() + ") with: " + revision2.getComment() + "(" + revision2.getDate() + ") using serializer: " + "Ifc2x3tc1");
 			  final BimServerClientInterface client = LocalDevSetup.setupJson("http://localhost:8080");
 			  SModelComparePluginConfiguration modelCompareConfig = client.getPluginInterface().getModelCompareByName("org.bimserver.changecompare.ConstructionChangeBasedModelComparePlugin");
 			  long configId = modelCompareConfig.getOid();
 			  Long compareId = client.getServiceInterface().downloadCompareResults(sId, new Long(roid), new Long(revision2.getOid()), new Long(configId), SCompareType.values()[0], true);
			  LOGGER.info("Compare id: " + compareId);
	    	}
		} catch (PublicInterfaceNotFoundException e) {
			e.printStackTrace();
		}
	}
}
