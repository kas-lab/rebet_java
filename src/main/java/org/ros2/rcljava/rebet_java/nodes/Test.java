package org.ros2.rcljava.rebet_java.nodes;

import java.util.concurrent.TimeUnit;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Comparator;
import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.concurrent.Callback;
import org.ros2.rcljava.node.BaseComposableNode;
import org.ros2.rcljava.publisher.Publisher;
import org.ros2.rcljava.client.Client;
import org.ros2.rcljava.service.Service;
import org.ros2.rcljava.timer.WallTimer;

import eu.coresense.resolution.resolutionModel.ResolutionModelPackage;
import eu.coresense.resolution.resolutionModel.ResolutionModel;
import eu.coresense.resolution.ResolutionModelStandaloneSetupGenerated;

import system.RossystemPackage;
import system.Rossystem;
import system.System;
import de.fraunhofer.ipa.rossystem.RosSystemStandaloneSetupGenerated;
import de.fraunhofer.ipa.ros2.Ros2StandaloneSetupGenerated;
import de.fraunhofer.ipa.ros.RosStandaloneSetupGenerated;

import ros.Package;
import ros.RosPackage;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.EObject;
import java.util.Map;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.XtextResourceFactory;
import org.eclipse.xtext.resource.XtextResourceSet; 
import org.eclipse.emf.common.util.URI;
import java.io.File;
import com.google.inject.Injector;
import com.google.inject.Provider;
import java.util.concurrent.Future;
import org.ros2.rcljava.service.RMWRequestId;
import java.util.function.Function;
import rcl_interfaces.msg.ParameterValue;
import java.util.List;
import java.util.ArrayList;

import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.diagnostics.Diagnostic;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.validation.Issue;
import org.eclipse.xtext.ISetup;




public class Test extends BaseComposableNode {
  private int count;

  private Publisher<std_msgs.msg.String> publisher;

  private Client<rebet_msgs.srv.GetContextVar> context_client;

  private Client<aal_msgs.srv.AdaptArchitecture> aal_client;

  private Service<aal_msgs.srv.AdaptArchitectureTactical> adaptation_service;

  private WallTimer timer;

  private ResourceSet resSet = new XtextResourceSet();

  private Map<String, Double> temporaryContextModel;

 //A map from String to a list of aal_msgs.msg.Adaptation
 
  private Map<String, Function<Void, aal_msgs.msg.Adaptation>> temporaryResolutionModel;

  private void LoadRos()
  {
    // Load the DSL model
    RosPackage.eINSTANCE.eClass();
    Injector injector = new RosStandaloneSetupGenerated().createInjectorAndDoEMFRegistration();
    Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
    Map<String, Object> m = reg.getExtensionToFactoryMap();
    m.put("ros", injector.getInstance(XtextResourceFactory.class));
    
    Resource resource = resSet.createResource(URI.createURI("/home/ega/lifecycle_msgs.ros"));
    Resource resource_common_msgs = resSet.createResource(URI.createURI("/home/ega/common_msgs.ros"));

    validate(resource);
    validate(resource_common_msgs);
    java.lang.System.out.println("A");
    try {
      resource.load(null);
      EObject root = resource.getContents().get(0);
      if (root instanceof Package) {
        Package model = (Package) root;
        java.lang.System.out.println("Loaded ros model with " + model.getName());


      }
      else {
        java.lang.System.out.println("Root is not an instance of Package");
        //Print what it is an instance of
        java.lang.System.out.println("Root is an instance of: " + root.getClass().getName());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
  private void LoadRos2()
  {
    // Load the DSL model
    RosPackage.eINSTANCE.eClass();
    Injector injector = new Ros2StandaloneSetupGenerated().createInjectorAndDoEMFRegistration();
    Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
    Map<String, Object> m = reg.getExtensionToFactoryMap();
    m.put("ros2", injector.getInstance(XtextResourceFactory.class));
    
    Resource resource = resSet.createResource(URI.createURI("/home/ega/velocity_smoother.ros2"));
    java.lang.System.out.println("A");
    try {
      resource.load(null);
      EObject root = resource.getContents().get(0);
      if (root instanceof Package) {
        Package model = (Package) root;
        java.lang.System.out.println("Loaded ros2 model with " + model.getName());

        validate(resource);
      }
      else {
        java.lang.System.out.println("Root is not an instance of Package");
        //Print what it is an instance of
        java.lang.System.out.println("Root is an instance of: " + root.getClass().getName());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private void LoadRossystem()
  {
    // Load the DSL model
    RossystemPackage.eINSTANCE.eClass();
    Injector injector = new RosSystemStandaloneSetupGenerated().createInjectorAndDoEMFRegistration();
    Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
    Map<String, Object> m = reg.getExtensionToFactoryMap();
    m.put("rossystem", injector.getInstance(XtextResourceFactory.class));
    
    Resource resource = resSet.createResource(URI.createURI("/home/ega/rebetmirte.rossystem"));
    java.lang.System.out.println("A");
    try {
      resource.load(null);
      EObject root = resource.getContents().get(0);
      if (root instanceof Rossystem) {
        Rossystem model = (Rossystem) root;
        java.lang.System.out.println("Loaded model with " + model.getName() + " and " + model.getComponents().size() + " components.");
      }
      if (root instanceof System) {
        System model = (System) root;
        java.lang.System.out.println("Loaded model with " + model.getName() + " and " + model.getComponents().size() + " components.");
      }
      else {
        java.lang.System.out.println("Root is not an instance of Rossystem");
        //Print what it is an instance of
        java.lang.System.out.println("Root is an instance of: " + root.getClass().getName());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
  private void LoadResolutionModel()
  {
    // Load the DSL model
    ResolutionModelPackage.eINSTANCE.eClass();
    Injector injector = new ResolutionModelStandaloneSetupGenerated().createInjectorAndDoEMFRegistration();
    Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
    Map<String, Object> m = reg.getExtensionToFactoryMap();
    m.put("resolution", injector.getInstance(XtextResourceFactory.class));
    Resource resource = resSet.createResource(URI.createURI("/home/ega/rebetmc.resolution"));
    try {
      resource.load(null);
      EObject root = resource.getContents().get(0);
      if (root instanceof ResolutionModel) {
        ResolutionModel model = (ResolutionModel) root;
        java.lang.System.out.println("Loaded model with " + model.getResolutions().size() + " resolutions.");
      }
      else {
        java.lang.System.out.println("Root is not an instance of Rossystem");
        //Print what it is an instance of
        java.lang.System.out.println("Root is an instance of: " + root.getClass().getName());
      }

      validate(resource);


    } catch (Exception e) {
      e.printStackTrace();
    }

  }

private void loadModel(ISetup setup, String fileExtension, String... filePaths) {
    try {
        // packageClass.getMethod("eClass").invoke(null); // Dynamically invoke eClass() on the package class
        Injector injector = setup.createInjectorAndDoEMFRegistration();
        Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
        Map<String, Object> m = reg.getExtensionToFactoryMap();
        m.put(fileExtension, injector.getInstance(XtextResourceFactory.class));

        for (String filePath : filePaths) {
            Resource resource = resSet.createResource(URI.createURI(filePath));
            resource.load(null);

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
  }

  public Test() throws Exception {
    super("test_node");
	java.lang.System.out.println("Gott here!");
  registerEPackages();
  // LoadRos();
      loadModel(
        new RosStandaloneSetupGenerated(),
        "ros",
        "/home/ega/lifecycle_msgs.ros",
        "/home/ega/common_msgs.ros"
    );
	java.lang.System.out.println("-----Loaded ROS---\n\n");

  // LoadRos2();

      loadModel(
        new Ros2StandaloneSetupGenerated(),
        "ros2",
        "/home/ega/velocity_smoother.ros2"
    );

    
	java.lang.System.out.println("-----Loaded ROS2---\n\n");

  // LoadRossystem();
      loadModel(
        new RosSystemStandaloneSetupGenerated(),
        "rossystem",
        "/home/ega/rebetmirte.rossystem"
    );
	java.lang.System.out.println("-----Loaded ROSSystem---\n\n");

  // LoadResolutionModel();

      loadModel(
        new ResolutionModelStandaloneSetupGenerated(),
        "resolution",
        "/home/ega/rebetmc.resolution"
    );

    // Load the DSL model
    // MyDslPackage.eINSTANCE.eClass();
    
	// Injector injector = new MyDslStandaloneSetupGenerated().createInjectorAndDoEMFRegistration();

	// Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
	// Map<String, Object> m = reg.getExtensionToFactoryMap();
	//   m.put("myDsl", injector.getInstance(XtextResourceFactory.class));

	// 	File file = new File("/home/ega/newtest.mydsl");

	// 	XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
	// 	Resource tacticsModelResource = resourceSet.getResource(URI.createFileURI(file.getAbsolutePath()), true);

	// 	Model myDslModel = (Model) tacticsModelResource.getContents().get(0);


    java.lang.System.out.println("Got here!");
    // java.lang.System.out.println("Number of reconfigs: " + myDslModel.getReconfigurations().size());

  }

  public static void main(String[] args) throws InterruptedException, Exception {
    // Initialize RCL
    RCLJava.rclJavaInit();

    RCLJava.spin(new Test());
  }
}
