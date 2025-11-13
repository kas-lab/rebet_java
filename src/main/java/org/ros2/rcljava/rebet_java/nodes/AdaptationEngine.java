package org.ros2.rcljava.rebet_java.nodes;

import java.util.concurrent.TimeUnit;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.Map;
import java.util.function.Function;
import java.io.File;
import java.time.Duration;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.concurrent.Callback;
import org.ros2.rcljava.node.BetterComposableNode;
import org.ros2.rcljava.subscription.Subscription;
import org.ros2.rcljava.client.Client;
import org.ros2.rcljava.service.Service;
import org.ros2.rcljava.timer.WallTimer;
import org.w3c.dom.Node;
import org.ros2.rcljava.service.RMWRequestId;
import org.ros2.rcljava.parameters.*;
import org.ros2.rcljava.graph.NodeNameInfo;

import rcl_interfaces.msg.ParameterValue;
import rcl_interfaces.msg.ParameterEvent;

import eu.coresense.adaptation.tactics.AdaptationRule;
import eu.coresense.adaptation.tactics.TacticsPackage;
import eu.coresense.adaptation.tactics.TacticsModel;
import eu.coresense.adaptation.TacticsStandaloneSetupGenerated;
import eu.coresense.adaptation.tactics.RuleBody;
import eu.coresense.adaptation.tactics.AtomicRule;
import eu.coresense.adaptation.tactics.RuleSet;
import eu.coresense.adaptation.tactics.AtomicRuleWithPriority;
import eu.coresense.adaptation.tactics.LogicalOperator;
import eu.coresense.adaptation.tactics.MathOperator;
import eu.coresense.adaptation.tactics.PureAction;
import eu.coresense.adaptation.tactics.ConditionAction;
import eu.coresense.adaptation.tactics.Condition;
import eu.coresense.adaptation.tactics.AtomicAction;
import eu.coresense.adaptation.tactics.AtomicActionSelectFeature;
import eu.coresense.adaptation.tactics.AtomicActionDeselectFeature;
import eu.coresense.adaptation.generator.TQLGenerator;
import eu.coresense.adaptation.generator.Namer;

import eu.coresense.resolution.resolutionModel.ResolutionModelPackage;
import eu.coresense.resolution.resolutionModel.ResolutionModel;
import eu.coresense.resolution.resolutionModel.Resolution;
import eu.coresense.resolution.resolutionModel.Reconfiguration;
import eu.coresense.resolution.resolutionModel.SetParam;
import eu.coresense.resolution.ResolutionModelStandaloneSetupGenerated;

import eu.coresense.variability.featureModel.FeatureModelPackage;
import eu.coresense.variability.featureModel.Model;
import eu.coresense.variability.FeatureModelStandaloneSetupGenerated;

import eu.coresense.context.ContextModelStandaloneSetupGenerated;	
import eu.coresense.context.contextModel.ContextModelPackage;	

import eu.coresense.requirements.RequirementsModelStandaloneSetupGenerated;	
import eu.coresense.requirements.requirementsModel.RequirementsModelPackage;	
import eu.coresense.requirements.requirementsModel.RequirementsModel;

import system.RossystemPackage;
import system.Rossystem;
import system.RossystemFactory;
import system.RosNode;
import system.System;
import de.fraunhofer.ipa.rossystem.RosSystemStandaloneSetupGenerated;
import de.fraunhofer.ipa.ros2.Ros2StandaloneSetupGenerated;
import de.fraunhofer.ipa.ros.RosStandaloneSetupGenerated;
import ros.Package;
import ros.AmentPackage;
import ros.RosPackage;
import ros.RosFactory;
import uma.caosd.rhea.BasicFMmetamodel.Feature;

import org.ros2.rcljava.parameters.service.ParameterService;
import org.ros2.rcljava.parameters.service.ParameterServiceImpl;


import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import com.google.inject.Injector;
import com.google.inject.Provider;

import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.diagnostics.Diagnostic;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceFactory;
import org.eclipse.xtext.resource.XtextResourceSet; 
import org.eclipse.xtext.validation.Issue;
import org.eclipse.xtext.ISetup;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.IGlobalScopeProvider;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
//import IEObjectDescription
import org.eclipse.xtext.resource.IEObjectDescription;
import com.google.common.base.Predicate;

import org.ros2.rcljava.rebet_java.RosTypeDBInterface;
import org.ros2.rcljava.rebet_java.RosToolingSupport;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AdaptationEngine extends BetterComposableNode {
  private int count;

  private Subscription<ParameterEvent> parameterEventSubscriber;

  private Client<rebet_msgs.srv.GetContextVar> contextClient;

  private Client<ros_typedb_msgs.srv.Query> typeDBClient;

  private Client<std_srvs.srv.Trigger> createMeasuresClient;

  private Client<aal_msgs.srv.AdaptArchitecture> aalClient;

  private Service<aal_msgs.srv.AdaptArchitectureTactical> adaptationService;

  private WallTimer tacticsTimer;

  private ResourceSet resSet = new XtextResourceSet();

 //A map from String to a list of aal_msgs.msg.Adaptation
 

  private TacticsModel tacticsModel;
  private RequirementsModel requirementsModel;

  private boolean flag = true;

  private boolean modelsLoaded = false;

  private TQLGenerator generator;
  Resource tacticsModelResource;
  Resource requirementsModelResource;
  List<Resource> ros2Resources;
  List<Resource> rosSystemResources;
  


public void printVisibleReferences(
    Injector injector,
    ResourceSet resSet,
    EReference referenceToResolve
) {
	java.lang.System.out.println("Printing visible references for: " + referenceToResolve.getName());
    IGlobalScopeProvider scopeProvider = injector.getInstance(IGlobalScopeProvider.class);

    Resource contextRes = resSet.getResources().get(0);

    // You can pass Predicate.ALWAYS_TRUE if you want everything
    Predicate<IEObjectDescription> filter = com.google.common.base.Predicates.alwaysTrue();


    IScope scope = scopeProvider.getScope(contextRes, referenceToResolve, filter);

    for (IEObjectDescription desc : scope.getAllElements()) {
        java.lang.System.out.println("Available: " + desc.getQualifiedName() + " -> " + desc.getEObjectOrProxy().eClass().getName());
    }
}

public static void printEObject(EObject obj) {
    printEObject(obj, 0, new HashSet<>());
}

private static void printEObject(EObject obj, int indent, Set<EObject> visited) {
    if (obj == null || visited.contains(obj)) {
        printIndent(indent);
        java.lang.System.out.println("[Already visited or null]");
        return;
    }
    visited.add(obj);
    printIndent(indent);
    java.lang.System.out.println(obj.eClass().getName());

    for (EStructuralFeature feature : obj.eClass().getEAllStructuralFeatures()) {
        Object value = obj.eGet(feature);
        printIndent(indent + 1);
        java.lang.System.out.print(feature.getName() + ": ");

        if (value == null) {
            java.lang.System.out.println("null");
        } else if (feature.isMany() && value instanceof List<?>) {
            java.lang.System.out.println("[");
            for (Object item : (List<?>) value) {
                if (item instanceof EObject) {
                    printEObject((EObject) item, indent + 2, visited);
                } else {
                    printIndent(indent + 2);
                    java.lang.System.out.println(item);
                }
            }
            printIndent(indent + 1);
            java.lang.System.out.println("]");
        } else if (value instanceof EObject) {
            EObject child = (EObject) value;
            if (child.eIsProxy()) {
                java.lang.System.out.println("[Unresolved proxy: " + ((InternalEObject) child).eProxyURI() + "]");
            } else {
                java.lang.System.out.println();
                printEObject(child, indent + 2, visited);
            }
        } else {
            java.lang.System.out.println(value);
        }
    }
}

private static void printIndent(int indent) {
    for (int i = 0; i < indent; i++) {
        java.lang.System.out.print("  ");
    }
}

  private void validate(Resource resource)
  {
      IResourceValidator validator = ((XtextResource) resource).getResourceServiceProvider().get(IResourceValidator.class);

      List<Issue> issues = validator.validate(resource, CheckMode.ALL, CancelIndicator.NullImpl);

      for (Issue issue : issues) {
          java.lang.System.out.println(issue.getSeverity() + ": " + issue.getMessage());
      }

  }

  private void registerEPackages() {
    // Register the EPackages for the models you want to load
    ResolutionModelPackage.eINSTANCE.eClass();
    RossystemPackage.eINSTANCE.eClass();
    RosPackage.eINSTANCE.eClass();
	TacticsPackage.eINSTANCE.eClass();
	RequirementsModelPackage.eINSTANCE.eClass();
	// Add any other EPackages you need to register
  }

  private List<Resource> loadModel(ISetup setup, String fileExtension, String... filePaths) {
    List<Resource> resources = new ArrayList<>();
	try {
        Injector injector = setup.createInjectorAndDoEMFRegistration();
        Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
        Map<String, Object> m = reg.getExtensionToFactoryMap();
        m.put(fileExtension, injector.getInstance(XtextResourceFactory.class));

        for (String filePath : filePaths) {
            Resource resource = resSet.createResource(URI.createFileURI(filePath));
			validate(resource);
            resource.load(null);
			resources.add(resource);

            EObject root = resource.getContents().get(0);
            if (root != null) {
                java.lang.System.out.println("Loaded model with root of type: " + root.getClass().getName() + " from file: " + filePath);
                validate(resource);
            } else {
                java.lang.System.out.println("Root is null for file: " + filePath);
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
	java.lang.System.out.println("-----Loaded " + filePaths.length + " ." + fileExtension + " files-----\n\n");
	return resources;
 }

 private void loadAllModels()
 {
	java.lang.System.out.println("Loading models...");
	java.lang.System.out.println(getNode().getParameter("ros2_path").asStringArray());
	loadModel(
        new RosStandaloneSetupGenerated(),
        "ros",
        getNode().getParameter("ros_path").asStringArray()
    );

	ros2Resources = loadModel(
        new Ros2StandaloneSetupGenerated(),
        "ros2",
        getNode().getParameter("ros2_path").asStringArray()
    );

	rosSystemResources = loadModel(
        new RosSystemStandaloneSetupGenerated(),
        "rossystem",
        getNode().getParameter("rossystem_path").asStringArray()
    );

		loadModel(
        new FeatureModelStandaloneSetupGenerated(),
        "var",
        getNode().getParameter("variability_path").asString()
    );

	loadModel(
        new ResolutionModelStandaloneSetupGenerated(),
        "resolution",
        getNode().getParameter("resolution_path").asString()
    );

	loadModel(
        new ContextModelStandaloneSetupGenerated(),
        "context",
        getNode().getParameter("context_path").asString()
    );

	List<Resource> requirementsResources = loadModel(
        new RequirementsModelStandaloneSetupGenerated(),
        "reqs",
        getNode().getParameter("requirements_path").asString()
    );


	List<Resource> tacticsResources = loadModel(
		new TacticsStandaloneSetupGenerated(),
		"tactics",
		getNode().getParameter("tactics_path").asString()
	);

	tacticsModelResource = tacticsResources.get(0);
	tacticsModel = (TacticsModel) tacticsModelResource.getContents().get(0);

	requirementsModelResource = requirementsResources.get(0);
	requirementsModel = (RequirementsModel) requirementsModelResource.getContents().get(0);

	modelsLoaded = true;
 }

private rcl_interfaces.msg.ParameterValue requestContextVar(String variable_name){
	try {
	rebet_msgs.srv.GetContextVar_Request request = new rebet_msgs.srv.GetContextVar_Request();
	request.setVariableName(variable_name);
	java.lang.System.out.println("Getting context var " + variable_name);



	if(this.contextClient.waitForService()){
		java.lang.System.out.println("Service is available");
		Future<rebet_msgs.srv.GetContextVar_Response> future = this.contextClient.asyncSendRequest(request);
		
		rcl_interfaces.msg.ParameterValue res = future.get().getVariableValue();
		java.lang.System.out.println("Context Var Result: " + res.getDoubleValue());

		return res;
	}
	else{
		java.lang.System.out.println("Service is not available");
		return null;
	}
	}
	catch (Exception e) {
		java.lang.System.out.println("Error in requestContextVar");
		e.printStackTrace();
		return null;
	}
}

	private boolean requestAdaptation(List<aal_msgs.msg.Adaptation> adaptations) {
		try {
			aal_msgs.srv.AdaptArchitecture_Request request = new aal_msgs.srv.AdaptArchitecture_Request();
			request.setAdaptations(adaptations);


			if (this.aalClient.waitForService()) {
				java.lang.System.out.println("Service is available");
				Future<aal_msgs.srv.AdaptArchitecture_Response> future = this.aalClient.asyncSendRequest(request);
				java.lang.System.out.println("Adaptation Result: " + future.get().getSuccess());
				return future.get().getSuccess();
			} else {
				java.lang.System.out.println("Service is not available");
			}	
		}
		catch (Exception e) {
			java.lang.System.out.println("Error in selectFeature");
			e.printStackTrace();
		}
		return false;
	}
	

	private List<ros_typedb_msgs.msg.ResultTree> requestTypedbQuery(String query, byte queryType) {
		try {
			ros_typedb_msgs.srv.Query_Request request = new ros_typedb_msgs.srv.Query_Request();
			request.setQuery(query);
			request.setQueryType(queryType);
			if (this.typeDBClient.waitForService()) {
				java.lang.System.out.println("TYPEDB Service is available");
				Future<ros_typedb_msgs.srv.Query_Response> future = this.typeDBClient.asyncSendRequest(request);
				ros_typedb_msgs.srv.Query_Response response = future.get();

				if( response.getSuccess() == false) {
					throw new IllegalStateException("Query to TypeDB Failed");
				}
				
				return response.getResults();
					
						
				
			} 
			else {
				java.lang.System.out.println("Service is not available");
			}
		} catch (Exception e) {
			java.lang.System.out.println("Error in requestTypedbQuery");
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	private void requestMeasures() {
		try {
			std_srvs.srv.Trigger_Request request = new std_srvs.srv.Trigger_Request();
			if (this.createMeasuresClient.waitForService()) {
				java.lang.System.out.println("createMeasures Service is available");
				Future<std_srvs.srv.Trigger_Response> future = this.createMeasuresClient.asyncSendRequest(request);
				std_srvs.srv.Trigger_Response response = future.get();

				if( response.getSuccess() == false) {
					throw new IllegalStateException("Create Measures Failed");
				}				
			} 
			else {
				java.lang.System.out.println("Service is not available");
			}
		} catch (Exception e) {
			java.lang.System.out.println("Error in requestMeasures");
			e.printStackTrace();
		}
	}

	private aal_msgs.msg.Adaptation processSetParameter(List<ros_typedb_msgs.msg.ResultTree> results) {
		java.lang.System.out.println("Processing SetParameter results \n");
		String node_name;
		List<ros_typedb_msgs.msg.IndexList> index_lists = null;
		int result_index = -1;

		for (int i = 0; i < results.size(); i++) {
			ros_typedb_msgs.msg.ResultTree result_tree = results.get(i);
			for (ros_typedb_msgs.msg.QueryResult q_result : result_tree.getResults()) {
				if (q_result.getType() == ros_typedb_msgs.msg.QueryResult.SUB_QUERY && q_result.getSubQueryName().equals(TQLGenerator.PARAM_VALUES_SUBQUERY_NAME)) {
					index_lists = q_result.getChildrenIndex();
					result_index = i;
				}
			}
		}

		if (index_lists == null || result_index < 0) {
			java.lang.System.out.println("No index list found in results");
			throw new IllegalStateException("No index list found in results");
		}

		ros_typedb_msgs.msg.ResultTree result_tree = results.get(result_index);

		List<Double> parameterValues = new ArrayList<Double>(index_lists.size());
		double b = 0.0;

		parameterValues = RosTypeDBInterface.extractParameterValues(Double.class, index_lists, result_tree, TQLGenerator.INDEX_VAR);
		node_name = RosTypeDBInterface.extractStringAttribute(result_tree,TQLGenerator.NODE_VAR,"node_name");

		java.lang.System.out.println("Node name: " + node_name);

		String param_name = RosTypeDBInterface.extractStringAttribute(result_tree,"parameter","parameter_name");
		java.lang.System.out.println("Parameter name: " + param_name);
		aal_msgs.msg.Adaptation adaptation = new aal_msgs.msg.Adaptation();
		adaptation.setAdaptationTarget((byte)1);

		rcl_interfaces.msg.ParameterValue value = new rcl_interfaces.msg.ParameterValue();

		value.setDoubleArrayValue(parameterValues);
		value.setType(rcl_interfaces.msg.ParameterType.PARAMETER_DOUBLE_ARRAY);

		rcl_interfaces.msg.Parameter param = new rcl_interfaces.msg.Parameter();

		param.setValue(value);
	
		adaptation.setNodeName(node_name);

		param.setName(param_name);

		adaptation.setParameterAdaptation(param);

		return adaptation;
			
	}

	private rcl_interfaces.msg.ParameterValue convertParameterValue(ros.ParameterValue paramValue){
		rcl_interfaces.msg.ParameterValue value = new rcl_interfaces.msg.ParameterValue();

		if(paramValue instanceof ros.ParameterSequence){
				ros.ParameterSequence paramSeq = (ros.ParameterSequence)paramValue;

				EList<?> list = (EList<?>) paramSeq.getValue();
				if (!list.isEmpty()) {
					Object firstElement = list.get(0);
					if(firstElement instanceof ros.ParameterDouble){
						value.setType(rcl_interfaces.msg.ParameterType.PARAMETER_DOUBLE_ARRAY);

						ArrayList<Double> doubleArray = new ArrayList<>();
						for(Object obj : list){
							ros.ParameterDouble param = (ros.ParameterDouble)obj;
							doubleArray.add(param.getValue());
						}
						value.setDoubleArrayValue(doubleArray);

						return value;
					}
					java.lang.System.out.println("The type of elements in the EList is: " + firstElement.getClass().getName());
				} else {
					java.lang.System.out.println("The EList is empty, so the type cannot be determined.");
				}
				// if(paramSeq.getValue() instanceof ros.)
				// java.lang.System.out.println("Nitt is an instance of: " + paramSeq.getValue().get(0).getClass().getName());
			}
		return value;
		// value.setType(paramValue.getType());
		// value.setDoubleArrayValue(paramValue.getDoubleArrayValue());
		// value.setStringArrayValue(paramValue.getStringArrayValue());
		// value.setBoolArrayValue(paramValue.getBoolArrayValue());
		// value.setByteArrayValue(paramValue.getByteArrayValue());
		// value.setIntArrayValue(paramValue.getIntArrayValue());
		// value.setLongArrayValue(paramValue.getLongArrayValue());
		// return value;
	}

	private <T> List<ros.Node> nodesWith(Function<ros.Node, List<T>> getList, Function<T, String> getName, String targetName) {
		List<ros.Node> nodes = new ArrayList<>();
		for (system.RosNode ros_node : allRossystemNodes()) {
			ros.Node node = ros_node.getFrom();
			for (T item : getList.apply(node)) {
				if (getName.apply(item).equals(targetName)) {
					nodes.add(node);
				}
			}		
		}
		return nodes;
	}

	private List<system.RosNode> allRossystemNodes()
	{
		List<system.RosNode> rosNodesInRossystems = new ArrayList<>();

		for (Resource resource : rosSystemResources) {
			System sys = (System) resource.getContents().get(0);
			for (system.Component component : sys.getComponents()) {
				if (!(component instanceof system.RosNode)) {
					continue;
				}
				system.RosNode ros_node = (system.RosNode) component;
				rosNodesInRossystems.add(ros_node);
			}
		}

		return rosNodesInRossystems;
	}

	public List<ros.Node> nodesWithActionClient(String actionName) {
		return nodesWith(ros.Node::getActionclient, ros.ActionClient::getName, actionName);
	}

	public List<ros.Node> nodesWithActionServer(String actionName) {
		return nodesWith(ros.Node::getActionserver, ros.ActionServer::getName, actionName);
	}

	public List<ros.Node> nodesWithServiceClient(String serviceName) {
		return nodesWith(ros.Node::getServiceclient, ros.ServiceClient::getName, serviceName);
	}

	public List<ros.Node> nodesWithServiceServer(String serviceName) {
		return nodesWith(ros.Node::getServiceserver, ros.ServiceServer::getName, serviceName);
	}

	public List<ros.Node> nodesWithPublisher(String topicName) {
		return nodesWith(ros.Node::getPublisher, ros.Publisher::getName, topicName);
	}

	public List<ros.Node> nodesWithSubscriber(String topicName) {
		return nodesWith(ros.Node::getSubscriber, ros.Subscriber::getName, topicName);
	}

	public Set<ros.Node> allConnectedNodes(ros.Node node) {
		Set<ros.Node> connectedNodes = new HashSet<>();

		connectedNodes.addAll(processExpansion(node));

		for (ros.ActionServer actionServer : node.getActionserver()) {
			connectedNodes.addAll(nodesWithActionClient(actionServer.getName()));	  
		}

		for (ros.ActionClient actionClient : node.getActionclient()) {
			connectedNodes.addAll(nodesWithActionServer(actionClient.getName()));
		}

		// Collect all nodes from subscribers
		for (ros.Subscriber subscriber : node.getSubscriber()) {
			connectedNodes.addAll(nodesWithPublisher(subscriber.getName()));
		}

		// Collect all nodes from publishers
		for (ros.Publisher publisher : node.getPublisher()) {
			connectedNodes.addAll(nodesWithSubscriber(publisher.getName()));
		}

		for (ros.ServiceServer serviceServer : node.getServiceserver()) {
			connectedNodes.addAll(nodesWithServiceClient(serviceServer.getName()));	  
		}

		for (ros.ServiceClient serviceClient : node.getServiceclient()) {
			connectedNodes.addAll(nodesWithServiceServer(serviceClient.getName()));	  
		}

		return connectedNodes;
  	}
  public void handleService(final RMWRequestId header,
      final aal_msgs.srv.AdaptArchitectureTactical_Request request,
      final aal_msgs.srv.AdaptArchitectureTactical_Response response) {
	// loadAllModels();
		aal_msgs.msg.ActionNodeDescription action_des = request.getChildDescription();

		String key = action_des.getActionName();
		
		boolean manualImplementation = false;

		if(manualImplementation) {
			ros.Node dat_node = null;
			for (Resource resource : rosSystemResources) {
				System sys = (System) resource.getContents().get(0);
				java.lang.System.out.println("Name of the system: " + sys.getName());
				for (system.Component component : sys.getComponents()) {
					if (!(component instanceof system.RosNode)) {
						continue;
					}
					system.RosNode ros_node = (system.RosNode) component;
					ros.Node node = ros_node.getFrom();
					for (ros.ActionServer action_server : node.getActionserver())
					{
						// ros.ActionSpec a_spec = action_server.getAction();
						String looking_for = action_server.getName();
						// java.lang.System.out.println("Looking for action server: " + looking_for);

						if(looking_for.equals(key))
						{
							java.lang.System.out.println("!! Found action server: " + looking_for);

							dat_node = node;

						}
					}
				}
			// java.lang.System.out.println(pkg.getArtifact());
			}
			if (dat_node == null) {
				java.lang.System.out.println("No node found for action server: " + key);
				response.setSuccess(false);
				return;
			}

			java.lang.System.out.println("------");

			// Search for all connected nodes
			HashSet<ros.Node> node_set = new HashSet<>();
			node_set.add(dat_node);
			search(dat_node, node_set);
			java.lang.System.out.println("Total connected nodes: " + node_set.size());
			for(ros.Node connected_node : node_set) {
				java.lang.System.out.println("Connected node: " + connected_node.getName());
			}
		}
		else
		{
			
			String fetch_q = generator.generateFetchApplicableTactics(generator.getResolutionModel(tacticsModel,tacticsModelResource).get(0), key);
			List<ros_typedb_msgs.msg.ResultTree> results = requestTypedbQuery(fetch_q, ros_typedb_msgs.srv.Query_Request.FETCH);
			if(results.isEmpty()) {
				java.lang.System.out.println("No Applicable Tactics");
				return;
			}
			// Print the length
			java.lang.System.out.println("Number of results: " + results.size());
			for (ros_typedb_msgs.msg.ResultTree result_tree : results) {
				for (ros_typedb_msgs.msg.QueryResult q_result : result_tree.getResults()) {
					java.lang.System.out.println(RosTypeDBInterface.printQueryResult(q_result));
					
				}
				java.lang.System.out.println("something unique");
			}
		}




  }

  public List<ros.Node> processExpansion(ros.Node node) {
	List<ros.Node> rosNodes = new ArrayList<>();
	for (Resource resource : rosSystemResources) {
		System sys = (System) resource.getContents().get(0);
		for(system.Process process : sys.getProcesses()) {
			for(system.Component component : process.getComponents()) {
				if (!(component instanceof system.RosNode)) {
					continue;
				}
				system.RosNode ros_node = (system.RosNode) component;
				rosNodes.add(ros_node.getFrom());
			}		
		}
	}
	return rosNodes;
  }

  public void search(ros.Node node, HashSet<ros.Node> node_set) {
	for(ros.Node connected_node : allConnectedNodes(node)) {
		if(node_set.add(connected_node))
		{
			search(connected_node, node_set);
		}
	}
  }

  public void parseComputationGraph()
  {
	var setup = new Ros2StandaloneSetupGenerated();
	String fileExtension = "ros2"; // or "xmi" or any other extension you want to use
	Injector injector = setup.createInjectorAndDoEMFRegistration();
	Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
	Map<String, Object> m = reg.getExtensionToFactoryMap();
	m.put(fileExtension, injector.getInstance(XtextResourceFactory.class));
	try{
	java.lang.System.out.println("Parsing computation graph...");
	Collection<NodeNameInfo> node_names = getNode().getNodeNames();

	//loop thhrough the node names and print them
	for (NodeNameInfo node_name : node_names) {
		java.lang.System.out.println("Node name: " + node_name.name);
		java.lang.System.out.println("Node namespace: " + node_name.namespace);
		var pkg = RosFactory.eINSTANCE.createAmentPackage();
		pkg.setName("bin");
		var artifact = RosFactory.eINSTANCE.createArtifact();
		artifact.setName(node_name.name);
		var r = RosFactory.eINSTANCE.createNode();
		r.setName(node_name.name);

		var param = RosFactory.eINSTANCE.createParameter();
		param.setName("use_sim_time");
		param.setType(RosFactory.eINSTANCE.createParameterBooleanType());

		var bool_val = RosFactory.eINSTANCE.createParameterBoolean();
		bool_val.setValue(true);
		param.setValue(bool_val);

		r.getParameter().add(param);
		artifact.setNode(r);
		pkg.getArtifact().add(artifact);

		

		URI uri = URI.createURI(node_name.name + ".ros2"); // could also be .xmi or custom
		Resource resource = resSet.createResource(uri);
		validate(resource);
		// resource.load(null);

		// 2. Add your root object (e.g., Package) to the resource
		resource.getContents().add(pkg);

		java.lang.System.out.println("Created resource with: " + pkg.getClass().getName());

		printEObject(pkg);



		// resource.save(java.lang.System.out, null);

		// 3. Optionally save or validate
		try {
			java.lang.System.out.println("Created resource with: " + pkg.getName());
			// If needed:
			// resource.save(System.out, null); // for debugging
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	}
	catch (Exception e) {
			e.printStackTrace();
		}

  }

  
  private Object unpackParameterValue(rcl_interfaces.msg.ParameterValue paramValue) {
		if (paramValue.getType() == rcl_interfaces.msg.ParameterType.PARAMETER_DOUBLE_ARRAY) {
			return paramValue.getDoubleArrayValue();
		} else if (paramValue.getType() == rcl_interfaces.msg.ParameterType.PARAMETER_STRING_ARRAY) {
			return paramValue.getStringArrayValue();
		} else if (paramValue.getType() == rcl_interfaces.msg.ParameterType.PARAMETER_BOOL_ARRAY) {
			return paramValue.getBoolArrayValue();
		} else if (paramValue.getType() == rcl_interfaces.msg.ParameterType.PARAMETER_BYTE_ARRAY) {
			return paramValue.getByteArrayValue();
		} else if (paramValue.getType() == rcl_interfaces.msg.ParameterType.PARAMETER_INTEGER_ARRAY) {
			return paramValue.getIntegerArrayValue();
		} else if (paramValue.getType() == rcl_interfaces.msg.ParameterType.PARAMETER_DOUBLE) {
			return paramValue.getDoubleValue();
		} else if (paramValue.getType() == rcl_interfaces.msg.ParameterType.PARAMETER_STRING) {
			return paramValue.getStringValue();
		} else if (paramValue.getType() == rcl_interfaces.msg.ParameterType.PARAMETER_BOOL) {
			return paramValue.getBoolValue();
		} else if (paramValue.getType() == rcl_interfaces.msg.ParameterType.PARAMETER_INTEGER) {
			return paramValue.getIntegerValue();
		} else {
			throw new IllegalArgumentException("Unsupported parameter type: " + paramValue.getType());
		}
	}
	
//   private void processTactics()
//   {
// 	int period = 0;

//  	tacticsTimer = getNode().createWallTimer(
//         period, TimeUnit.MILLISECONDS,
//         () -> {
			
// 			if(flag)
// 			{
// 				for(eu.coresense.context.contextModel.ContextVar measurement : generator.uniqueMeasurements)
// 				{
// 					rcl_interfaces.msg.ParameterValue context_value = requestContextVar(measurement.getName());
// 					String insert_query = generator.generateInsertMeasurement(measurement.getName(), unpackParameterValue(context_value));

// 					requestTypedbQuery(insert_query, ros_typedb_msgs.srv.Query_Request.INSERT);
// 				}
// 			flag = false;
// 			}
// 			String fetch_q = generator.generateFetchQuery(tacticsModel);
// 			List<ros_typedb_msgs.msg.ResultTree> results = requestTypedbQuery(fetch_q, ros_typedb_msgs.srv.Query_Request.FETCH);
// 			if(results.isEmpty()) {
// 				java.lang.System.out.println("No Tactics valid");
// 				return;
// 			}
// 			// Print the length
// 			java.lang.System.out.println("Number of results: " + results.size());
// 			for (ros_typedb_msgs.msg.ResultTree result_tree : results) {
// 				for (ros_typedb_msgs.msg.QueryResult q_result : result_tree.getResults()) {
// 					java.lang.System.out.println(RosTypeDBInterface.printQueryResult(q_result));
					
// 				}
// 				java.lang.System.out.println("something unique");
// 			}

//             aal_msgs.msg.Adaptation adap = processSetParameter(results);
//             requestAdaptation(new ArrayList<>(Arrays.asList(adap)));
//         }
//     );

//   }

  private void parameterEventCallback(final rcl_interfaces.msg.ParameterEvent eventMsg) {
	String nodeAffected = eventMsg.getNode();
	java.lang.System.out.println("Parameter event received for node: " + nodeAffected);

	if(!modelsLoaded) { return; }

	RosNode rosNode = RosToolingSupport.nodeInRosSystems(nodeAffected, rosSystemResources);
	if(rosNode == null) {
		java.lang.System.out.println("ParamEvent affected node " + nodeAffected + " not found in ROS2 model.");
		return;
	}
	ros.Node fromNode = rosNode.getFrom();

	for (rcl_interfaces.msg.Parameter parameter : eventMsg.getNewParameters())
	{
		ros.Parameter matchRosParam = RosToolingSupport.findParameter(fromNode, parameter.getName());
		system.RosParameter matchRossystemParam = RosToolingSupport.findRosParameter(rosNode, parameter.getName());

		boolean existsInRos = matchRosParam != null;
		boolean existsInRossystem = matchRossystemParam != null;

		if(!existsInRos && existsInRossystem)
		{
			throw new IllegalStateException("Very strange, a parameter was found in the rossystem but not in the ros files which should not be possible");
		}


		if(existsInRos && existsInRossystem)
		{
			//Check if the value is same as what is in .rossystem, if not, update rossystem. This should reuse code from getChangedParameters below
			RosToolingSupport.updateRosParameter(matchRossystemParam, parameter);
		}
		else if(existsInRos && !existsInRossystem)
		{
			// Is the value different than what is in .ros2 files? If so add to the rossystem.
			RosToolingSupport.newRosParameter(rosNode, matchRosParam, parameter);
		}
		else if(!existsInRos && !existsInRossystem)
		{
			// It should be added to the .ros
			RosToolingSupport.newParameter(fromNode, parameter);
			//TODO: Handle the fact that this should update the original ros2 files, either with an overwrite or a new file.
		}
	}

	for (rcl_interfaces.msg.Parameter parameter : eventMsg.getChangedParameters())
	{
		ros.Parameter matchRosParam = RosToolingSupport.findParameter(fromNode, parameter.getName());
		system.RosParameter matchRossystemParam = RosToolingSupport.findRosParameter(rosNode, parameter.getName());

		boolean existsInRos = matchRosParam != null;
		boolean existsInRossystem = matchRossystemParam != null;

		if(!existsInRos && existsInRossystem)
		{
			throw new IllegalStateException("Very strange, a parameter was found in the rossystem but not in the ros files which should not be possible");
		}

		if(existsInRossystem)
		{
			// It already has an override, so just update that.
			RosToolingSupport.updateRosParameter(matchRossystemParam, parameter);
		}
		else if(existsInRos)
		{
			//Not in the rossystem, so we add it there.
			RosToolingSupport.newRosParameter(rosNode, matchRosParam, parameter);
		}
		else
		{
			RosToolingSupport.newParameter(fromNode, parameter);
		}
	}

	for (rcl_interfaces.msg.Parameter parameter : eventMsg.getDeletedParameters())
	{
		//TODO: Implementing this properly requires having a notion of an 'active' set of parameters. This is not currently implemented.
		// It will be done on start through a manual query to each node in the rosSystem I suppose.
	}
  }

  public AdaptationEngine(ArrayList<String> cli_args) throws Exception {
    super("adaptation_engine",cli_args);
	registerEPackages();

	// parseComputationGraph();

	

	ParameterService parameterService = new ParameterServiceImpl(getNode());
    this.count = 0;
	getNode().declareParameter(new ParameterVariant("tactics_path", ""));
	getNode().declareParameter(new ParameterVariant("resolution_path", ""));
	getNode().declareParameter(new ParameterVariant("variability_path", ""));
	getNode().declareParameter(new ParameterVariant("context_path", ""));
	getNode().declareParameter(new ParameterVariant("requirements_path", ""));
	String[] paths = {"", ""};
	getNode().declareParameter(new ParameterVariant("ros_path", paths));
	getNode().declareParameter(new ParameterVariant("ros2_path", paths));
	getNode().declareParameter(new ParameterVariant("rossystem_path", paths));

	getNode().declareParameter(new ParameterVariant("loadmodelsonstart", true));
	getNode().declareParameter(new ParameterVariant("checkinitialparameters", true));


	java.lang.System.out.println("Got here!");
    
	this.contextClient = node.<rebet_msgs.srv.GetContextVar>createClient(rebet_msgs.srv.GetContextVar.class, "/get_context_var");
	this.aalClient = node.<aal_msgs.srv.AdaptArchitecture>createClient(aal_msgs.srv.AdaptArchitecture.class, "/adapt_architecture");
	this.typeDBClient = node.<ros_typedb_msgs.srv.Query>createClient(ros_typedb_msgs.srv.Query.class, "/tactical_retreat_kb_node/query");
	this.createMeasuresClient = node.<std_srvs.srv.Trigger>createClient(std_srvs.srv.Trigger.class, "/tactical_retreat_kb_node/create_measures");
	this.parameterEventSubscriber = node.<ParameterEvent>createSubscription(ParameterEvent.class, "/parameter_events", (ParameterEvent event) -> this.parameterEventCallback(event));

	this.adaptationService =  node.<aal_msgs.srv.AdaptArchitectureTactical>createService(
            aal_msgs.srv.AdaptArchitectureTactical.class, "/adapt_architecture_tactical",
            (RMWRequestId header, aal_msgs.srv.AdaptArchitectureTactical_Request request,
                aal_msgs.srv.AdaptArchitectureTactical_Response response)
                -> this.handleService(header, request, response));


	if(getNode().getParameter("loadmodelsonstart").asBool() == true){
		java.lang.System.out.println("Loading models on start");
		loadAllModels();

	}else{
		java.lang.System.out.println("Not loading models on start");
	}

	boolean skipping = false;
	boolean checkGraph = false;

	


	if (modelsLoaded && checkGraph)
	{
		List<system.RosNode> rosNodesInRossystems = allRossystemNodes();
		Set<String> knownNodeNames = new HashSet<>();
		for (system.RosNode rosNode : rosNodesInRossystems) {
			knownNodeNames.add(rosNode.getName());
		}
		
		
		
		Set<String> graphNodeNames = new HashSet<>();
		Collection<NodeNameInfo> node_names = getNode().getNodeNames();

		for (NodeNameInfo node_name_info : node_names) {
			graphNodeNames.add(node_name_info.name);
		}

		java.lang.System.out.println("--------------------");
		java.lang.System.out.println("--------------------");

		// Check if all rossystem nodes are present in the computation graph
		boolean allKnown = graphNodeNames.containsAll(knownNodeNames);

		java.lang.System.out.println("Are all rossystem nodes are present? " + allKnown);

		for (String nodeName : graphNodeNames) {
			java.lang.System.out.println("Node in computation graph: " + nodeName);
		}

		if (!allKnown) {
			java.lang.System.out.println("Some nodes in the computation graph are not known in the ROS2 model.");
			for (String nodeName : knownNodeNames) {
				if (!graphNodeNames.contains(nodeName)) {
					java.lang.System.out.println("Node which isn't running: " + nodeName);
				}
			}
		} else {
			java.lang.System.out.println("All nodes in the rossystem files are in the computation graph.");

			for (system.RosNode rosNode : rosNodesInRossystems) {
				java.lang.System.out.println("\n---------------------\n");
				List<String> parameterNames = rosNode.getFrom().getParameter().stream()
					.map(ros.Parameter::getName)
					.collect(java.util.stream.Collectors.toList());


				String nodeName = rosNode.getName();

				Client<rcl_interfaces.srv.GetParameters> client =
					getNode().<rcl_interfaces.srv.GetParameters>createClient(
						rcl_interfaces.srv.GetParameters.class, "/" + nodeName + "/get_parameters");

				if(parameterNames.isEmpty()) {
					continue;
				}
				rcl_interfaces.srv.GetParameters_Request request = new rcl_interfaces.srv.GetParameters_Request();
				request.setNames(parameterNames);
				java.lang.System.out.println("Requesting parameters for node: " + nodeName);

				if (client.waitForService(Duration.ofSeconds(5))) {
					Future<rcl_interfaces.srv.GetParameters_Response> future = client.asyncSendRequest(request);
					try {
						rcl_interfaces.srv.GetParameters_Response response = future.get();
						if (response.getValues().isEmpty()) {
							java.lang.System.out.println("NO parameters found for node " + nodeName + " one of the parameters requested might not exist");
							continue;
						}
						for (int i = 0; i < response.getValues().size(); i++) {
							rcl_interfaces.msg.ParameterValue value = response.getValues().get(i);
							String param_name = parameterNames.get(i);
							ros.ParameterValue current_value = RosToolingSupport.convertParameterValue(value);

							for(ros.Parameter param : rosNode.getFrom().getParameter()) {
								Object running_value = RosToolingSupport.valueOf(current_value);
								Object model_value = RosToolingSupport.valueOf(param.getValue());
								boolean equal = false;
								if(param.getName().equals(param_name)) {
									if(!running_value.equals(model_value))
									{
										if (param.getType() instanceof ros.ParameterArrayType && 
											running_value instanceof List<?> &&
											((List<?>) running_value).isEmpty() &&
											(model_value == null || (model_value instanceof List<?> && ((List<?>) model_value).isEmpty()))) {
											continue;
										}
										// Check if the parameter exists within rosNode.getParameters()
										system.RosParameter rosParam = RosToolingSupport.findRosParameter(rosNode, param_name);
										if(rosParam != null) {
											if(RosToolingSupport.valueOf(current_value).equals(RosToolingSupport.valueOf(rosParam.getValue())))
											{
												continue;
											}
										}
										throw new IllegalStateException("Parameter value for " + param.getName() + " in node " + nodeName + " is different than in the .ros2 file. This should not happen, please check your models.");
									}
								}
							}
						}
					}
					catch (Exception e) {
						java.lang.System.out.println("Error while getting parameters for node " + nodeName);
						e.printStackTrace();
					}

				}
				else {
					java.lang.System.out.println("Client for node " + nodeName + " is not available.");
				}
			}
			java.lang.System.out.println("Done checking all the nodes in the rossystem files.");
		}


	}

	

	
	if(!skipping) {
	Namer namer = new Namer();
	generator = new TQLGenerator(namer);
	String y = "";
	for (ResolutionModel resModel : generator.getResolutionModel(tacticsModel,tacticsModelResource)) {
		y = y + generator.generateArchitectureInsertQuery(resModel);
	}
	
	// for (System rossys : generator.collectAllSystems(tacticsModel.getResolutionModel())) {
	// 	for (RosNode comp : generator.rosNodesFromSystem(rossys)) {
	// 		y = y + generator.generateInsertQuery(comp);
			
	// 	}
	// }
	// requestTypedbQuery(y, ros_typedb_msgs.srv.Query_Request.INSERT);

	String x = generator.generateTQL(tacticsModel, tacticsModelResource); // one specific instance of a .tactics file
	
	String temp = generator.generateInsertQuery(requirementsModel,"rebetmc");
	java.lang.System.out.println(y+x);
	java.lang.System.out.println(temp);
	requestTypedbQuery(y + x, ros_typedb_msgs.srv.Query_Request.INSERT);
	// processTactics();
	java.lang.System.out.println("Doing the measures!");
	requestMeasures();
    java.lang.System.out.println("Got here!");

	var setup = new RosSystemStandaloneSetupGenerated();
	 Injector injector = setup.createInjectorAndDoEMFRegistration();
	printVisibleReferences(
    injector,
    resSet,
    RosPackage.Literals.ARTIFACT__NODE // or whatever reference you want to check
	);
  	}
    // java.lang.System.out.println("Number of tactics: " + tacticsModel.getAdaptationRules().size());
  }

  public static void main(String[] args) throws InterruptedException, Exception {
    // Initialize RCL
	//print all the args
	java.lang.System.out.println("Args: ");
	for (String arg : args) {
		java.lang.System.out.println(arg);
	}
	
    RCLJava.rclJavaInit();

    RCLJava.spin(new AdaptationEngine(new ArrayList<>(Arrays.asList(args))));
  }
}
