package org.bimserver.runmodelcompare;

import java.util.List;

import org.bimserver.LocalDevSetup;
import org.bimserver.interfaces.objects.SCompareType;
import org.bimserver.interfaces.objects.SObjectType;
import org.bimserver.interfaces.objects.SProject;
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
