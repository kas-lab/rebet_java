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
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.concurrent.Callback;
import org.ros2.rcljava.node.BetterComposableNode;
import org.ros2.rcljava.publisher.Publisher;
import org.ros2.rcljava.client.Client;
import org.ros2.rcljava.service.Service;
import org.ros2.rcljava.timer.WallTimer;
import org.w3c.dom.Node;
import org.ros2.rcljava.service.RMWRequestId;
import org.ros2.rcljava.parameters.*;
import org.ros2.rcljava.graph.NodeNameInfo;

import rcl_interfaces.msg.ParameterValue;

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
import eu.coresense.adaptation.generator.TacticsGenerator;

import eu.coresense.resolution.resolutionModel.ResolutionModelPackage;
import eu.coresense.resolution.resolutionModel.ResolutionModel;
import eu.coresense.resolution.resolutionModel.Resolution;
import eu.coresense.resolution.resolutionModel.Reconfiguration;
import eu.coresense.resolution.resolutionModel.SetParam;
import eu.coresense.resolution.ResolutionModelStandaloneSetupGenerated;

import eu.coresense.variability.featureModel.FeatureModelPackage;
import eu.coresense.variability.featureModel.Model;
import eu.coresense.variability.FeatureModelStandaloneSetupGenerated;


import system.RossystemPackage;
import system.Rossystem;
import system.RossystemFactory;
import system.RosNode;
import system.System;
import de.fraunhofer.ipa.rossystem.RosSystemStandaloneSetupGenerated;
import de.fraunhofer.ipa.ros2.Ros2StandaloneSetupGenerated;
import de.fraunhofer.ipa.ros.RosStandaloneSetupGenerated;
import ros.Package;
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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AdaptationEngine extends BetterComposableNode {
  private int count;

  private Publisher<std_msgs.msg.String> publisher;

  private Client<rebet_msgs.srv.GetContextVar> context_client;

  private Client<ros_typedb_msgs.srv.Query> typedb_client;

  private Client<aal_msgs.srv.AdaptArchitecture> aal_client;

  private Service<aal_msgs.srv.AdaptArchitectureTactical> adaptation_service;

  private WallTimer tacticsTimer;

  private ResourceSet resSet = new XtextResourceSet();

 //A map from String to a list of aal_msgs.msg.Adaptation
 

  private TacticsModel tacticsModel;

  private TacticsGenerator generator;


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
            Resource resource = resSet.createResource(URI.createURI(filePath));
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

	loadModel(
        new Ros2StandaloneSetupGenerated(),
        "ros2",
        getNode().getParameter("ros2_path").asStringArray()
    );

	loadModel(
        new RosSystemStandaloneSetupGenerated(),
        "rossystem",
        getNode().getParameter("rossystem_path").asString()
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



	List<Resource> tacticsResources = loadModel(
		new TacticsStandaloneSetupGenerated(),
		"tactics",
		getNode().getParameter("tactics_path").asString()
	);

	Resource tacticsModelResource = tacticsResources.get(0);
	tacticsModel = (TacticsModel) tacticsModelResource.getContents().get(0);
 }

private rcl_interfaces.msg.ParameterValue requestContextVar(String variable_name){
	try {
	rebet_msgs.srv.GetContextVar_Request request = new rebet_msgs.srv.GetContextVar_Request();
	request.setVariableName(variable_name);

	if(this.context_client.waitForService()){
		java.lang.System.out.println("Service is available");
		Future<rebet_msgs.srv.GetContextVar_Response> future = this.context_client.asyncSendRequest(request);
		
		rcl_interfaces.msg.ParameterValue res = future.get().getVariableValue();
		java.lang.System.out.println("Result: " + res.getDoubleValue());

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


			if (this.aal_client.waitForService()) {
				java.lang.System.out.println("Service is available");
				Future<aal_msgs.srv.AdaptArchitecture_Response> future = this.aal_client.asyncSendRequest(request);
				java.lang.System.out.println("Result: " + future.get().getSuccess());
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
	

	private List<ros_typedb_msgs.msg.ResultTree> requestTypedbQuery(String query, String queryType) {
		try {
			ros_typedb_msgs.srv.Query_Request request = new ros_typedb_msgs.srv.Query_Request();
			request.setQuery(query);
			request.setQueryType(queryType);
			if (this.typedb_client.waitForService()) {
				java.lang.System.out.println("TYPEDB Service is available");
				Future<ros_typedb_msgs.srv.Query_Response> future = this.typedb_client.asyncSendRequest(request);
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

	private aal_msgs.msg.Adaptation processSetParameter(List<ros_typedb_msgs.msg.ResultTree> results) {
		java.lang.System.out.println("Processing SetParameter results \n");
		String node_name;
		List<ros_typedb_msgs.msg.IndexList> index_lists = null;
		int result_index = -1;

		for (int i = 0; i < results.size(); i++) {
			ros_typedb_msgs.msg.ResultTree result_tree = results.get(i);
			for (ros_typedb_msgs.msg.QueryResult q_result : result_tree.getResults()) {
				if (q_result.getType() == ros_typedb_msgs.msg.QueryResult.SUB_QUERY && q_result.getSubQueryName().equals(TacticsGenerator.SUBQUERY_NAME)) {
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

		parameterValues = RosTypeDBInterface.extractParameterValues(Double.class, index_lists, result_tree, TacticsGenerator.INDEX_VAR);
		node_name = RosTypeDBInterface.extractStringAttribute(result_tree,TacticsGenerator.NODE_VAR,"node_name");

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

	

  public void handleService(final RMWRequestId header,
      final aal_msgs.srv.AdaptArchitectureTactical_Request request,
      final aal_msgs.srv.AdaptArchitectureTactical_Response response) {
	loadAllModels();
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

  private void processTactics()
  {
	int period = tacticsModel.getPeriod();

 	tacticsTimer = getNode().createWallTimer(
        period, TimeUnit.MILLISECONDS,
        () -> {

			for(String measurement : generator.uniqueMeasurements)
			{
				rcl_interfaces.msg.ParameterValue context_value = requestContextVar(measurement);
				String insert_query = generator.generateInsertMeasurement(measurement, unpackParameterValue(context_value));

				requestTypedbQuery(insert_query, "insert");
			}

			List<ros_typedb_msgs.msg.ResultTree> results = requestTypedbQuery(generator.generateFetchQuery(tacticsModel), "fetch");
			if(results.isEmpty()) {
				java.lang.System.out.println("No Tactics valid");
				return;
			}

            aal_msgs.msg.Adaptation adap = processSetParameter(results);
            requestAdaptation(new ArrayList<>(Arrays.asList(adap)));
        }
    );

  }

  public AdaptationEngine(ArrayList<String> cli_args) throws Exception {
    super("adaptation_engine",cli_args);
	registerEPackages();

	parseComputationGraph();

	

	ParameterService parameterService = new ParameterServiceImpl(getNode());
    this.count = 0;
	getNode().declareParameter(new ParameterVariant("tactics_path", ""));
	getNode().declareParameter(new ParameterVariant("resolution_path", ""));
	getNode().declareParameter(new ParameterVariant("rossystem_path", ""));
	getNode().declareParameter(new ParameterVariant("variability_path", ""));
	String[] paths = {"", ""};
	getNode().declareParameter(new ParameterVariant("ros_path", paths));
	getNode().declareParameter(new ParameterVariant("ros2_path", ""));
	getNode().declareParameter(new ParameterVariant("loadmodelsonstart", true));

	java.lang.System.out.println("Got here!");
    
	this.context_client = node.<rebet_msgs.srv.GetContextVar>createClient(rebet_msgs.srv.GetContextVar.class, "/get_context_var");
	this.aal_client = node.<aal_msgs.srv.AdaptArchitecture>createClient(aal_msgs.srv.AdaptArchitecture.class, "/adapt_architecture");
	this.typedb_client = node.<ros_typedb_msgs.srv.Query>createClient(ros_typedb_msgs.srv.Query.class, "/ros_typedb/query");

	this.adaptation_service =  node.<aal_msgs.srv.AdaptArchitectureTactical>createService(
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
	generator = new TacticsGenerator();
	String x = generator.generateTQL(tacticsModel);

	// java.lang.System.out.println("Generated TQL: \n" + x);
	requestTypedbQuery(x, "insert");

	processTactics();

    java.lang.System.out.println("Got here!");

	var setup = new RosSystemStandaloneSetupGenerated();
	 Injector injector = setup.createInjectorAndDoEMFRegistration();
	printVisibleReferences(
    injector,
    resSet,
    RosPackage.Literals.ARTIFACT__NODE // or whatever reference you want to check
	);
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
