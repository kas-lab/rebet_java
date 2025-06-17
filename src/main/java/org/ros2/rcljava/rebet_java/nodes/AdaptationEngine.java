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





class AtomicRuleWithPriorityComparator implements Comparator<AtomicRuleWithPriority> {

	
	
	/*
	 * This is designed in such a way that a rule with priority 0 is stored
	 * after a rule with priority 1.
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(AtomicRuleWithPriority arg0, AtomicRuleWithPriority arg1) {

		if(arg0.getPriorityValue() < arg1.getPriorityValue()){
			return 1;
		}else if(arg0.getPriorityValue() == arg1.getPriorityValue()){
			return 0;
		}else{ // (arg0.getPriorityValue() > arg1.getPriorityValue()){
			return -1;
		}

	}



}

public class AdaptationEngine extends BetterComposableNode {
  private int count;

  private Publisher<std_msgs.msg.String> publisher;

  private Client<rebet_msgs.srv.GetContextVar> context_client;

  private Client<ros_typedb_msgs.srv.Query> typedb_client;

  private Client<aal_msgs.srv.AdaptArchitecture> aal_client;

  private Service<aal_msgs.srv.AdaptArchitectureTactical> adaptation_service;

  private WallTimer timer;

  private ResourceSet resSet = new XtextResourceSet();

 //A map from String to a list of aal_msgs.msg.Adaptation
 

  private TacticsModel tacticsModel;


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

  private void evaluateAndExecuteAdaptationRule(AdaptationRule rule){

		if(rule instanceof AtomicRule){
			evaluateAndExecuteAtomicRule( ((AtomicRule) rule).getRuleBody());
		}else if(rule instanceof RuleSet){
			evaluateAndExecuteRuleSet( (RuleSet) rule);
		}

	}

	private void evaluateAndExecuteAtomicRule(RuleBody ruleBody){

		if(ruleBody instanceof PureAction){

			executeAtomicAction(ruleBody.getAtomicAction());

		}else if(ruleBody instanceof ConditionAction){
			evaluateAndExecuteConditionAction((ConditionAction)ruleBody);
		}

	}

	private void evaluateAndExecuteConditionAction(ConditionAction conditionAction){

		if(evaluateConditionChain(conditionAction.getCondition()) == false){

			if(conditionAction.getElse() != null){
				evaluateAndExecuteAtomicRule(conditionAction.getElse());
			}else{
				return;
			}

		}else{
			executeAtomicAction(conditionAction.getAtomicAction());
		}
	}

	private boolean evaluateConditionChain(Condition condition){

		if(condition.getSecondTerm() == null){
			return evaluateCondition(condition);
		}

		if(condition.getLogicalOp() == LogicalOperator.AND){
			return evaluateCondition(condition) && evaluateConditionChain(condition.getSecondTerm());
		}else if (condition.getLogicalOp() == LogicalOperator.OR){
			return evaluateCondition(condition) || evaluateConditionChain(condition.getSecondTerm());
		}

		return false;


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

	private String printParameterValue(rcl_interfaces.msg.ParameterValue value){
		StringBuilder sb = new StringBuilder();
		sb.append("ParameterValue: ");
		sb.append("Type: ").append(value.getType()).append(", ");
		sb.append("DoubleArray: ").append(value.getDoubleArrayValue()).append(", ");
		sb.append("StringArray: ").append(value.getStringArrayValue()).append(", ");
		sb.append("BoolArray: ").append(value.getBoolArrayValue()).append(", ");
		sb.append("ByteArray: ").append(value.getByteArrayValue()).append(", ");
		sb.append("IntArray: ").append(value.getIntegerArrayValue()).append(", ");
		sb.append("DoubleValue: ").append(value.getDoubleValue()).append(", ");
		sb.append("StringValue: ").append(value.getStringValue()).append(", ");
		sb.append("BoolValue: ").append(value.getBoolValue()).append(", ");
		return sb.toString();
	}

	private void requestTypedbQuery(String query, String queryType) {
		try {
			ros_typedb_msgs.srv.Query_Request request = new ros_typedb_msgs.srv.Query_Request();
			request.setQuery(query);
			request.setQueryType(queryType);
			if (this.typedb_client.waitForService()) {
				java.lang.System.out.println("Service is available");
				Future<ros_typedb_msgs.srv.Query_Response> future = this.typedb_client.asyncSendRequest(request);
				ros_typedb_msgs.srv.Query_Response response = future.get();
				java.lang.System.out.println("Result: " + response.getSuccess());
				java.lang.System.out.println("Num results: " + response.getResults().size());
				response.getResults().forEach(result -> {
					java.lang.System.out.println("Result: " + result.getVariableName() + " " + result.getVariableType());
					result.getAttributes().forEach(att -> {
					java.lang.System.out.println("Result: " + att.getName() + " " + att.getLabel() + " " + printParameterValue(att.getValue()));
					});
				});
			} else {
				java.lang.System.out.println("Service is not available");
			}
		} catch (Exception e) {
			java.lang.System.out.println("Error in requestTypedbQuery");
			e.printStackTrace();
		}
	}

	private boolean evaluateCondition(Condition condition){
	java.lang.System.out.println("Evaluating condition");
    // Object value = lastReceivedMessages.get(cdm);
    String measurement = condition.getMeasurement();
	//demo requestContextVar
	rcl_interfaces.msg.ParameterValue value = null;
	try {
		value = requestContextVar(measurement);
	}
	catch (Exception e) {
		java.lang.System.out.println("Error in requestContextVar");
		e.printStackTrace();
	}

		if(value == null){
			return false;
		}

		//TODO: logic to handle ParameterValues.
		String val = "";
		if(false){
			// val = (String)value;
			java.lang.System.out.println("Wut");
		}else{
			java.lang.System.out.println("Parse ParameterValue");
			val = String.valueOf(value.getDoubleValue());
			// val = String.valueOf(value);
		}

		val.compareTo(condition.getValue());

		if(condition.getOperator() == MathOperator.LESS){
			return val.compareTo(condition.getValue()) == -1;
		}else if(condition.getOperator() == MathOperator.GREATER){
			return val.compareTo(condition.getValue()) == 1;
		}else if(condition.getOperator() == MathOperator.EQUAL){
			return val.compareTo(condition.getValue()) == 0;
		}else if(condition.getOperator() == MathOperator.DIFFERENT){
			return val.compareTo(condition.getValue()) != 0;
		}

		return true;
	}

	private void evaluateAndExecuteRuleSet(RuleSet ruleSet){


		// 1) Create a rule set with rules ordered based on priority
		// As soon as the DLS grammar will be stable the comparator should be moved
		// in the class AtomicRuleWithPriority
		TreeSet<AtomicRuleWithPriority> rules = new TreeSet<AtomicRuleWithPriority>(new AtomicRuleWithPriorityComparator());

		rules.addAll(ruleSet.getAtomicRules());

		// 2) Execute the actions starting form max priority to min priority 

		for(AtomicRuleWithPriority action : rules){

			evaluateAndExecuteAtomicRule(action.getRuleBody());

		}

	}

	private void executeAtomicAction(AtomicAction atomicAction){

		if(atomicAction instanceof AtomicActionSelectFeature){

			AtomicActionSelectFeature currentAction = (AtomicActionSelectFeature)atomicAction;

			selectFeature(currentAction.getVariant());
			//java.lang.System.out.println("Select Feature: " + currentAction.getFeature().getName());

		}else if(atomicAction instanceof AtomicActionDeselectFeature){

			AtomicActionDeselectFeature currentAction = (AtomicActionDeselectFeature)atomicAction;

			// to be improved, see select feature
			// featureModel.removeSubFeatureFromInstance(currentFeatureModelInstance, currentAction.getVariant());
			//currentFeatureModelInstance.getSelectedFeatures().remove(currentAction.getFeature());
			//java.lang.System.out.println("Deselect Feature: " + currentAction.getFeature().getName());

		}
		// else if(atomicAction instanceof AtomicActionModifyAttribute){

		// 	AtomicActionModifyAttribute currentAction = (AtomicActionModifyAttribute)atomicAction;



		// }
		// else if(atomicAction instanceof AtomicActionQuery){

		// 	AtomicActionQuery currentAction = (AtomicActionQuery)atomicAction;



		// }

		if(atomicAction.getSecondAction() != null){
			executeAtomicAction(atomicAction.getSecondAction());
		}

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

	private void selectFeature(Feature variant){
		try {
		// java.lang.System.out.println("Select Feature: " + variant.getName());

		// // Loop through variant.getReconfigurations()
		// aal_msgs.srv.AdaptArchitecture_Request request = new aal_msgs.srv.AdaptArchitecture_Request();
		// List<aal_msgs.msg.Adaptation> adaptations = new ArrayList<>();
		// for(Reconfiguration reconfig : variant.getReconfigurations()){
		// 	SetParam setparam = reconfig.getSet_param();

		// 	rcl_interfaces.msg.Parameter param = new rcl_interfaces.msg.Parameter();
		// 	param.setName(setparam.getParam().getName());

		// 	aal_msgs.msg.Adaptation adaptation = new aal_msgs.msg.Adaptation();
		// 	adaptation.setAdaptationTarget((byte)1);

		// 	param.setValue(convertParameterValue(setparam.getValue()));
		// 	adaptation.setParameterAdaptation(param);
		// 	adaptation.setNodeName(setparam.getNode().getName());

		// 	adaptations.add(adaptation);
		// }

		// request.setAdaptations(adaptations);


		// if (this.aal_client.waitForService()) {
		// 	java.lang.System.out.println("Service is available");
		// 	Future<aal_msgs.srv.AdaptArchitecture_Response> future = this.aal_client.asyncSendRequest(request);
		// 	java.lang.System.out.println("Result: " + future.get().getSuccess());
		// } else {
		// 	java.lang.System.out.println("Service is not available");
		// }
		// 	} else {
		// 		java.lang.System.out.println("Adaptation is null");
		// 	}
		// }
		}
		catch (Exception e) {
			java.lang.System.out.println("Error in selectFeature");
			e.printStackTrace();
		}
	}

  public void handleService(final RMWRequestId header,
      final aal_msgs.srv.AdaptArchitectureTactical_Request request,
      final aal_msgs.srv.AdaptArchitectureTactical_Response response) {
	loadAllModels();
    
	for( AdaptationRule rule : tacticsModel.getAdaptationRules() ){
		evaluateAndExecuteAdaptationRule(rule);
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
	TacticsGenerator generator = new TacticsGenerator();
	String x = generator.generateTQL(tacticsModel);

	// java.lang.System.out.println("Generated TQL: \n" + x);


	requestTypedbQuery(x, "insert");

	requestTypedbQuery(
	"""
	match
    $t_measured_nearest_object isa Term, has term_name "t_measured_nearest_object";

insert
    $t_measured_nearest_object has term_value 0.09;
    """
	, "insert");


	String valid_rule = """
	match
    $r (consequent: $c) isa tactic_rule, has rule_valid true;
fetch
    $r: rule_name;
    $c: attribute;
    """;

	String fetch_action = """
match
    $r (tactic:$t, consequent:$c) isa tactic_rule, has rule_valid true;
    $c has term_name $consequent_name;
    (tactic:$t, variant_resolution:$vr) isa resolution_model;
    $vr (resolution_action:$res_action) isa variant_resolution, has variant_name $variant_name;
    $variant_name == $consequent_name;
fetch
    set_parameter_action:{
        match
            $res_action isa! SetParameter;
        fetch
            $res_action: node_name, parameter_name, parameter-type, parameter-value;
    };
""";

	requestTypedbQuery(fetch_action, "fetch");



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
