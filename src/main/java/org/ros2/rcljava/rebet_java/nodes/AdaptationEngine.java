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

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.concurrent.Callback;
import org.ros2.rcljava.node.BetterComposableNode;
import org.ros2.rcljava.publisher.Publisher;
import org.ros2.rcljava.client.Client;
import org.ros2.rcljava.service.Service;
import org.ros2.rcljava.timer.WallTimer;
import org.ros2.rcljava.service.RMWRequestId;
import org.ros2.rcljava.parameters.*;

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

import eu.coresense.resolution.resolutionModel.ResolutionModelPackage;
import eu.coresense.resolution.resolutionModel.ResolutionModel;
import eu.coresense.resolution.resolutionModel.Resolution;
import eu.coresense.resolution.resolutionModel.Reconfiguration;
import eu.coresense.resolution.resolutionModel.SetParam;

import eu.coresense.resolution.ResolutionModelStandaloneSetupGenerated;

import system.RossystemPackage;
import system.Rossystem;
import system.System;
import de.fraunhofer.ipa.rossystem.RosSystemStandaloneSetupGenerated;
import de.fraunhofer.ipa.ros2.Ros2StandaloneSetupGenerated;
import de.fraunhofer.ipa.ros.RosStandaloneSetupGenerated;
import ros.Package;
import ros.RosPackage;

import org.ros2.rcljava.parameters.service.ParameterService;
import org.ros2.rcljava.parameters.service.ParameterServiceImpl;


import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.EList;

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

  private Client<aal_msgs.srv.AdaptArchitecture> aal_client;

  private Service<aal_msgs.srv.AdaptArchitectureTactical> adaptation_service;

  private WallTimer timer;

  private ResourceSet resSet = new XtextResourceSet();

 //A map from String to a list of aal_msgs.msg.Adaptation
 

  private TacticsModel tacticsModel;

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
	java.lang.System.out.println(getNode().getParameter("ros2_path").asString());
	loadModel(
        new RosStandaloneSetupGenerated(),
        "ros",
        getNode().getParameter("ros_path").asStringArray()
    );

	loadModel(
        new Ros2StandaloneSetupGenerated(),
        "ros2",
        getNode().getParameter("ros2_path").asString()
    );

	loadModel(
        new RosSystemStandaloneSetupGenerated(),
        "rossystem",
        getNode().getParameter("rossystem_path").asString()
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

	private void selectFeature(Resolution variant){
		try {
		java.lang.System.out.println("Select Feature: " + variant.getName());

		// Loop through variant.getReconfigurations()
		aal_msgs.srv.AdaptArchitecture_Request request = new aal_msgs.srv.AdaptArchitecture_Request();
		List<aal_msgs.msg.Adaptation> adaptations = new ArrayList<>();
		for(Reconfiguration reconfig : variant.getReconfigurations()){
			SetParam setparam = reconfig.getSet_param();

			rcl_interfaces.msg.Parameter param = new rcl_interfaces.msg.Parameter();
			param.setName(setparam.getParam().getName());

			aal_msgs.msg.Adaptation adaptation = new aal_msgs.msg.Adaptation();
			adaptation.setAdaptationTarget((byte)1);

			param.setValue(convertParameterValue(setparam.getValue()));
			adaptation.setParameterAdaptation(param);
			adaptation.setNodeName(setparam.getNode().getName());

			adaptations.add(adaptation);
		}

		request.setAdaptations(adaptations);


		if (this.aal_client.waitForService()) {
			java.lang.System.out.println("Service is available");
			Future<aal_msgs.srv.AdaptArchitecture_Response> future = this.aal_client.asyncSendRequest(request);
			java.lang.System.out.println("Result: " + future.get().getSuccess());
		} else {
			java.lang.System.out.println("Service is not available");
		}
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

  public AdaptationEngine(ArrayList<String> cli_args) throws Exception {
    super("adaptation_engine",cli_args);

	ParameterService parameterService = new ParameterServiceImpl(getNode());
    this.count = 0;
	getNode().declareParameter(new ParameterVariant("tactics_path", ""));
	getNode().declareParameter(new ParameterVariant("resolution_path", ""));
	getNode().declareParameter(new ParameterVariant("rossystem_path", ""));
	String[] paths = {"", ""};
	getNode().declareParameter(new ParameterVariant("ros_path", paths));
	getNode().declareParameter(new ParameterVariant("ros2_path", ""));

	java.lang.System.out.println("Got here!");
    
	this.context_client = node.<rebet_msgs.srv.GetContextVar>createClient(rebet_msgs.srv.GetContextVar.class, "/get_context_var");
	this.aal_client = node.<aal_msgs.srv.AdaptArchitecture>createClient(aal_msgs.srv.AdaptArchitecture.class, "/adapt_architecture");

	this.adaptation_service =  node.<aal_msgs.srv.AdaptArchitectureTactical>createService(
            aal_msgs.srv.AdaptArchitectureTactical.class, "/adapt_architecture_tactical",
            (RMWRequestId header, aal_msgs.srv.AdaptArchitectureTactical_Request request,
                aal_msgs.srv.AdaptArchitectureTactical_Response response)
                -> this.handleService(header, request, response));

	registerEPackages();

    java.lang.System.out.println("Got here!");
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
