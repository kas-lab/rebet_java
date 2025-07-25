
package org.ros2.rcljava.rebet_java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.client.Client;
import org.ros2.rcljava.concurrent.RCLFuture;
import org.ros2.rcljava.consumers.Consumer;
import org.ros2.rcljava.consumers.TriConsumer;
import org.ros2.rcljava.executors.Executor;
import org.ros2.rcljava.executors.MultiThreadedExecutor;
import org.ros2.rcljava.executors.SingleThreadedExecutor;
import org.ros2.rcljava.graph.EndpointInfo;
import org.ros2.rcljava.graph.NameAndTypes;
import org.ros2.rcljava.graph.NodeNameInfo;
import org.ros2.rcljava.node.Node;
import org.ros2.rcljava.publisher.Publisher;
import org.ros2.rcljava.qos.policies.Reliability;
import org.ros2.rcljava.qos.QoSProfile;
import org.ros2.rcljava.service.RMWRequestId;
import org.ros2.rcljava.service.Service;
import org.ros2.rcljava.subscription.Subscription;
import eu.coresense.resolution.resolutionModel.ResolutionModelPackage;
import ros.RosPackage;
import system.RossystemPackage;
import eu.coresense.adaptation.tactics.TacticsPackage;
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
import java.util.Map;
import de.fraunhofer.ipa.ros.RosStandaloneSetupGenerated;
import de.fraunhofer.ipa.ros2.Ros2StandaloneSetupGenerated;
import de.fraunhofer.ipa.rossystem.RosSystemStandaloneSetupGenerated;
import eu.coresense.resolution.ResolutionModelStandaloneSetupGenerated;
import eu.coresense.variability.FeatureModelStandaloneSetupGenerated;
import eu.coresense.adaptation.TacticsStandaloneSetupGenerated;
import eu.coresense.adaptation.tactics.TacticsModel;
import eu.coresense.adaptation.generator.TacticsGenerator;
import java.util.concurrent.Future;
import java.time.Duration;






public class GeneratedTQLTest {
  private Node node;
  private ResourceSet resSet = new XtextResourceSet();
  private TacticsModel tacticsModel;
  private Client<ros_typedb_msgs.srv.Query> typedb_client;



  private String speedTactic = """
name MaxSpeedInternal;
import "rebetmc.resolution";
import "dummy.var";
variability_model dummy
resolution_model rebetmc;
period[ms] 1;
rule MIN { if(nearest_object < "0.1" OR movement_power > "40.0") select_variant(LOW_SPEED)};
rule MAX { if(nearest_object > "0.15" AND movement_power < "39.0") select_variant(HI_SPEED)};
rule MED { if(nearest_object > "0.15") select_variant(MED_SPEED)};
""";
  
  private String commonMsgs = """
geometry_msgs:
  msgs:
    Accel
     message
       'geometry_msgs/msg/Vector3' linear 'geometry_msgs/msg/Vector3' angular
    AccelStamped
     message
       Header header 'geometry_msgs/msg/Accel' accel
    AccelWithCovariance
     message
       'geometry_msgs/msg/Accel' accel float64[] covariance
    AccelWithCovarianceStamped
     message
       Header header 'geometry_msgs/msg/AccelWithCovariance' accel
    Inertia
     message
       float64 m 'geometry_msgs/msg/Vector3' com float64 ixx float64 ixy float64 ixz float64 iyy float64 iyz float64 izz
    InertiaStamped
     message
       Header header 'geometry_msgs/msg/Inertia' inertia
    Point
     message
       float64 x float64 y float64 z
    Point32
     message
       float32 x float32 y float32 z
    PointStamped
     message
       Header header 'geometry_msgs/msg/Point' point
    Polygon
     message
       'geometry_msgs/msg/Point32'[] points
    PolygonStamped
     message
       Header header 'geometry_msgs/msg/Polygon' polygon
    Pose
     message
       'geometry_msgs/msg/Point' position 'geometry_msgs/msg/Quaternion' orientation
    Pose2D
     message
       float64 x float64 y float64 theta
    PoseArray
     message
       Header header 'geometry_msgs/msg/Pose'[] poses
    PoseStamped
     message
       Header header 'geometry_msgs/msg/Pose' pose
    PoseWithCovariance
     message
       'geometry_msgs/msg/Pose' pose float64[] covariance
    PoseWithCovarianceStamped
     message
       Header header 'geometry_msgs/msg/PoseWithCovariance' pose
    Quaternion
     message
       float64 x float64 y float64 z float64 w
    QuaternionStamped
     message
       Header header 'geometry_msgs/msg/Quaternion' quaternion
    Transform
     message
       'geometry_msgs/msg/Vector3' translation 'geometry_msgs/msg/Quaternion' rotation
    TransformStamped
     message
       Header header string child_frame_id 'geometry_msgs/msg/Transform' transform
    Twist
     message
       'geometry_msgs/msg/Vector3' linear 'geometry_msgs/msg/Vector3' angular
    TwistStamped
     message
       Header header 'geometry_msgs/msg/Twist' twist
    TwistWithCovariance
     message
       'geometry_msgs/msg/Twist' twist float64[] covariance
    TwistWithCovarianceStamped
     message
       Header header 'geometry_msgs/msg/TwistWithCovariance' twist
    Vector3
     message
       float64 x float64 y float64 z
    Vector3Stamped
     message
       Header header 'geometry_msgs/msg/Vector3' vector
    Wrench
     message
       'geometry_msgs/msg/Vector3' force 'geometry_msgs/msg/Vector3' torque
    WrenchStamped
     message
       Header header 'geometry_msgs/msg/Wrench' wrench
  """;

  private String lifecycleMsgs = """
lifecycle_msgs:
  msgs:
    TransitionDescription
      message
        'lifecycle_msgs/msg/Transition'[] transition
        'lifecycle_msgs/msg/State'[] start_state
        'lifecycle_msgs/msg/State'[] goal_state
    Transition
      message
        uint8 id
        string label
    TransitionEvent
      message
        uint64 timestamp
        'lifecycle_msgs/msg/Transition'[] transition
        'lifecycle_msgs/msg/State'[] start_state
        'lifecycle_msgs/msg/State'[] goal_state
    State
      message
        uint8 id
        string label
    
  srvs:
    ChangeState
      request
        'lifecycle_msgs/msg/Transition'[] transition
      response
        bool success
    GetState
      request
      response
        'lifecycle_msgs/msg/State'[] current_state
    GetAvailableTransitions
      request
      response
        'lifecycle_msgs/msg/TransitionDescription'[] available_transitions
    GetAvailableStates
      request
      response
        'lifecycle_msgs/msg/State'[] available_states
""";

  private String velocitySmoother = """
nav2_velocity_smoother:
  artifacts:
    velocity_smoother:
      node: /velocity_smoother
      publishers:
        'cmd_vel':
          type: 'geometry_msgs/msg/Twist'
          qos:
          # profile:
          # history: UNKNOWN
          # depth:
            reliability: reliable
            durability: volatile
        'velocity_smoother/transition_event':
          type: 'lifecycle_msgs/msg/TransitionEvent'
          qos:
          # profile:
          # history: UNKNOWN
          # depth:
            reliability: reliable
            durability: volatile
      subscribers:
        'cmd_vel_nav':
          type: 'geometry_msgs/msg/Twist'
      serviceservers:
        'velocity_smoother/change_state':
          type: 'lifecycle_msgs/srv/ChangeState'
        'velocity_smoother/get_available_states':
          type: 'lifecycle_msgs/srv/GetAvailableStates'
        'velocity_smoother/get_available_transitions':
          type: 'lifecycle_msgs/srv/GetAvailableTransitions'
        'velocity_smoother/get_state':
          type: 'lifecycle_msgs/srv/GetState'
        'velocity_smoother/get_transition_graph':
          type: 'lifecycle_msgs/srv/GetAvailableTransitions'
      parameters:
        '/bond_disable_heartbeat_timeout':
          type: Boolean
          value: true
        'deadband_velocity':
          type: Array[Double]
          value: [0.0, 0.0, 0.0]
        'feedback':
          type: String
          value: "OPEN_LOOP"
        'max_accel':
          type: Array[Double]
          value: [2.5, 0.0, 3.2]
        'max_decel':
          type: Array[Double]
          value: [-2.5, 0.0, -3.2]
        'max_velocity':
          type: Array[Double]
          value: [0.26, 0.0, 1.0]
        'min_velocity':
          type: Array[Double]
          value: [-0.26, 0.0, -1.0]
""";

  private String rebetmirte = """
nav2: 
  nodes:
    velocity_smoother:
      from: "nav2_velocity_smoother./velocity_smoother" #From .ros2 file
      parameters:
        - max_velocity: "velocity_smoother::max_velocity"
          value: [0.26, 0.0, 1.0]
""";

  private String rebetmc = """
name rebetmc;
import "rebetmirte.rossystem";
import "dummy.var";
variability_model dummy;
ros_system nav2;
resolutions 
LOW_SPEED : select: SetParameter(velocity_smoother,max_velocity,[0.10,0.0,1.0]) deselect:
MED_SPEED : select: SetParameter(velocity_smoother,max_velocity,[0.18,0.0,1.0]) deselect:
HI_SPEED : select: SetParameter(velocity_smoother,max_velocity,[0.26,0.0,1.0]) deselect:
""";

private String feature_model = """
feature_model {
name dummy
root 
  mandatory selected Alternative MAX_SPEED { id MAX_SPEED children 
    { mandatory selected Feature LOW_SPEED { id LOW_SPEED }, 
      mandatory selected Feature MED_SPEED { id MED_SPEED },
      mandatory selected Feature HI_SPEED { id HI_SPEED }
    }
  }
crossTreeConstraints}
""";

private String exampleInsert = """
insert 

# define Feature Model
$f_max_speed isa Feature, has feature_name "MAX_SPEED";
$f_low_speed isa Feature, has feature_name "LOW_SPEED";
$f_hi_speed isa Feature, has feature_name "HIGH_SPEED";
$f_med_speed isa Feature, has feature_name "MED_SPEED";
$alternative (parent: $f_max_speed, child: $f_low_speed, child: $f_med_speed, child: $f_hi_speed) isa alternative;

# define MaxSpeedInternal Tactic    
$ta_max_speed isa Tactic, has tactic_name "MaxSpeedInternal", has tactic_period 1.0;

$t_measured_nearest_object isa Term, has term_name "t_measured_nearest_object";
$t_measured_movement_power isa Term, has term_name "t_measured_movement_power";


# define ROSNodes
$velocity_smoother_node isa ROSNode, has node_name "velocity_smoother";

$t_nearest_object_close isa Term, has term_name "nearest_object_close", has term_value 0.1;


$ineq_nearest_lower_than_10 (lhs_term:$t_measured_nearest_object, rhs_term: $t_nearest_object_close) isa inequality_expression, has operator "<";
$t_movement_power_high isa Term, has term_name "t_movement_power_high", has term_value 40.0;
$ineq_mv_pwer_greater_than_40  (lhs_term:$t_measured_movement_power, rhs_term: $t_movement_power_high) isa inequality_expression, has operator ">";
$or_expression (expression:$ineq_nearest_lower_than_10, expression:$ineq_mv_pwer_greater_than_40) isa or_expression;
$tactic_rule_min (tactic:$ta_max_speed, antecedent:$or_expression, consequent:$f_low_speed ) isa tactic_rule, has rule_name "MIN";


$t_nearest_object_far isa Term, has term_name "nearest_object_far", has term_value 0.15;
$ineq_nearest_greater_than_15 (lhs_term:$t_measured_nearest_object, rhs_term: $t_nearest_object_far) isa inequality_expression, has operator ">";
$t_movement_power_low isa Term, has term_name "t_movement_power_low", has term_value 39.0;
$ineq_mv_pwer_lower_than_39  (lhs_term:$t_measured_movement_power, rhs_term: $t_movement_power_low) isa inequality_expression, has operator "<";
$and_expression (expression:$ineq_nearest_greater_than_15, expression:$ineq_mv_pwer_lower_than_39) isa and_expression;
$tactic_rule_max (tactic:$ta_max_speed, antecedent:$and_expression, consequent:$f_hi_speed ) isa tactic_rule, has rule_name "MAX";


$tactic_rule_med (tactic:$ta_max_speed, antecedent:$ineq_nearest_greater_than_15, consequent:$f_med_speed ) isa tactic_rule, has rule_name "MED";

# define parameter values low
$p_max_velocity_x_low isa ParameterDouble, has double_value 0.10;
$p_max_velocity_y_low isa ParameterDouble, has double_value 0.0;
$p_max_velocity_z_low isa ParameterDouble, has double_value 1.0;
# define parameter array low
$p_max_speed_low_array isa ParameterArray;
$p_max_speed_low_array_0 (parameter_array: $p_max_speed_low_array, parameter_value: $p_max_velocity_x_low) isa at_index, has parameter_value_index 0;
$p_max_speed_low_array_1 (parameter_array: $p_max_speed_low_array, parameter_value: $p_max_velocity_y_low) isa at_index, has parameter_value_index 1;
$p_max_speed_low_array_2 (parameter_array: $p_max_speed_low_array, parameter_value: $p_max_velocity_z_low) isa at_index, has parameter_value_index 2;
# define parameter max_speed_low
$p_max_speed_low (parameter_value: $p_max_speed_low_array) isa parameter, has parameter_name "max_velocity";

# define parameter values med
$p_max_velocity_x_med isa ParameterDouble, has double_value 0.18;
$p_max_velocity_y_med isa ParameterDouble, has double_value 0.0;
$p_max_velocity_z_med isa ParameterDouble, has double_value 1.0;
# define parameter array med
$p_max_speed_med_array isa ParameterArray;
$p_max_speed_med_array_0 (parameter_array: $p_max_speed_med_array, parameter_value: $p_max_velocity_x_med) isa at_index, has parameter_value_index 0;
$p_max_speed_med_array_1 (parameter_array: $p_max_speed_med_array, parameter_value: $p_max_velocity_y_med) isa at_index, has parameter_value_index 1;
$p_max_speed_med_array_2 (parameter_array: $p_max_speed_med_array, parameter_value: $p_max_velocity_z_med) isa at_index, has parameter_value_index 2;
# define parameter max_speed_med
$p_max_speed_med (parameter_value: $p_max_speed_med_array) isa parameter, has parameter_name "max_velocity";
# define variant resolutions


# define parameter values high
$p_max_velocity_x_high isa ParameterDouble, has double_value 0.26;
$p_max_velocity_y_high isa ParameterDouble, has double_value 0.0;
$p_max_velocity_z_high isa ParameterDouble, has double_value 1.0;
# define parameter array high
$p_max_speed_high_array isa ParameterArray;
$p_max_speed_high_array_0 (parameter_array: $p_max_speed_high_array, parameter_value: $p_max_velocity_x_high) isa at_index, has parameter_value_index 0;
$p_max_speed_high_array_1 (parameter_array: $p_max_speed_high_array, parameter_value: $p_max_velocity_y_high) isa at_index, has parameter_value_index 1;
$p_max_speed_high_array_2 (parameter_array: $p_max_speed_high_array, parameter_value: $p_max_velocity_z_high) isa at_index, has parameter_value_index 2;

# define parameter max_speed_high
$p_max_speed_high (parameter_value: $p_max_speed_high_array) isa parameter, has parameter_name "max_velocity";

# define set nodes parameter actions
$sp_max_speed_low (node: $velocity_smoother_node, parameter: $p_max_speed_low) isa set_nodes_parameters;
$sp_max_speed_med (node: $velocity_smoother_node, parameter: $p_max_speed_med) isa set_nodes_parameters;
$sp_max_speed_high (node: $velocity_smoother_node, parameter: $p_max_speed_high) isa set_nodes_parameters;

# define variant resolutions
$low_speed_resolution (resolution_action: $sp_max_speed_low, variant: $f_low_speed) isa variant_resolution;
$med_speed_resolution (resolution_action: $sp_max_speed_med, variant: $f_med_speed) isa variant_resolution;
$high_speed_resolution (resolution_action: $sp_max_speed_high, variant: $f_hi_speed) isa variant_resolution;

$res_rebetmc (tactic: $ta_max_speed, variant_resolution:$low_speed_resolution,  variant_resolution:$med_speed_resolution,  variant_resolution:$high_speed_resolution) isa resolution_model, 
    has resolution_model_name "rebetmc", has ros_system "nav2";
""";



  @BeforeClass
  public static void setupOnce() throws Exception {
    // Just to quiet down warnings
    try
    {
      // Configure log4j. Doing this dynamically so that Android does not complain about missing
      // the log4j JARs, SLF4J uses Android's native logging mechanism instead.
      Class c = Class.forName("org.apache.log4j.BasicConfigurator");
      Method m = c.getDeclaredMethod("configure", (Class<?>[]) null);
      Object o = m.invoke(null, (Object[]) null);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    RCLJava.rclJavaInit();
  }


  @Before
  public void setUp() {
    node = RCLJava.createNode("test_node");
;
  }

  @After
  public void tearDown() {
    node.dispose();
  }

  @AfterClass
  public static void tearDownOnce() {
    RCLJava.shutdown();
  }

  @Test
  public final void testCreate() {
    assertNotEquals(0, node.getHandle());
  }

  @Test
  public final void testRegister() throws Exception {
    assertNotNull(ResolutionModelPackage.eINSTANCE.eClass());
    assertNotNull(RossystemPackage.eINSTANCE.eClass());
    assertNotNull(RosPackage.eINSTANCE.eClass());
    assertNotNull(TacticsPackage.eINSTANCE.eClass());
  }

  private void validate(Resource resource)
  {
      IResourceValidator validator = ((XtextResource) resource).getResourceServiceProvider().get(IResourceValidator.class);

      List<Issue> issues = validator.validate(resource, CheckMode.ALL, CancelIndicator.NullImpl);

      assertTrue(issues.size() == 0);
      for (Issue issue : issues) {
          java.lang.System.out.println(issue.getSeverity() + ": " + issue.getMessage());
      }

  }

  private Resource loadModelFromString(ISetup setup, String fileExtension, String modelContent, String name) {
    java.lang.System.out.println("Loading Model " + name);
    
    try {
        Injector injector = setup.createInjectorAndDoEMFRegistration();
        Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
        Map<String, Object> m = reg.getExtensionToFactoryMap();
        m.put(fileExtension, injector.getInstance(XtextResourceFactory.class));

        Resource resource = resSet.createResource(URI.createURI(name + "." + fileExtension));
        resource.load(new java.io.ByteArrayInputStream(modelContent.getBytes()), null);
        validate(resource);

        EObject root = resource.getContents().isEmpty() ? null : resource.getContents().get(0);
        if (root != null) {
            validate(resource);
        } else {
            System.out.println("Root is null for string model");
        }
        return resource;
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
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
                  validate(resource);
              } else {
                  java.lang.System.out.println("Root is null for file: " + filePath);
              }
          }
      } catch (Exception e) {
          e.printStackTrace();
      }
	return resources;
 }

  @Test
  public final void testLoadModels() throws Exception {
    testRegister();
    java.lang.System.out.println("Prints work here??");
    java.lang.System.out.println("Current working directory: " + java.lang.System.getProperty("user.dir"));
    assertNotNull(loadModelFromString(
      new RosStandaloneSetupGenerated(),
      "ros",
      commonMsgs,
      "common_msgs"
    ));
    assertNotNull(loadModelFromString(
      new RosStandaloneSetupGenerated(),
      "ros",
      lifecycleMsgs,
      "lifecycle_msgs"
    ));

    assertNotNull(loadModelFromString(
      new Ros2StandaloneSetupGenerated(),
      "ros2",
      velocitySmoother,
      "velocity_smoother"
    ));

	  assertNotNull(loadModelFromString(
        new RosSystemStandaloneSetupGenerated(),
        "rossystem",
        rebetmirte,
        "rebetmirte"
    ));

    assertNotNull(loadModelFromString(
        new FeatureModelStandaloneSetupGenerated(),
        "var",
        feature_model,
        "dummy"
    ));

    assertNotNull(loadModelFromString(
      new ResolutionModelStandaloneSetupGenerated(),
      "resolution",
      rebetmc,
      "rebetmc"
    ));

  
	Resource tacticsModelResource = loadModelFromString(
		new TacticsStandaloneSetupGenerated(),
		"tactics",
		speedTactic,
    "maxvelocity"
	);

  assertNotNull(tacticsModelResource);
	tacticsModel = (TacticsModel) tacticsModelResource.getContents().get(0);
  assertNotNull(tacticsModel);
  }

  private Boolean requestTypedbQuery(String query, String queryType) {
		try {
			ros_typedb_msgs.srv.Query_Request request = new ros_typedb_msgs.srv.Query_Request();
			request.setQuery(query);
			request.setQueryType(queryType);
			if (this.typedb_client.waitForService(Duration.ofSeconds(10))) {
				java.lang.System.out.println("TYPEDB Service is available");
				Future<ros_typedb_msgs.srv.Query_Response> future = this.typedb_client.asyncSendRequest(request);
				ros_typedb_msgs.srv.Query_Response response = future.get();
        return response.getSuccess();
			} else {
				java.lang.System.out.println("Service is not available");
        return false;
			}
		} catch (Exception e) {
			java.lang.System.out.println("Error in requestTypedbQuery");
			e.printStackTrace();
      return false;
		}
	}

  // @Test
  // public final void testExampleInsert() throws Exception {

  // 	typedb_client = node.<ros_typedb_msgs.srv.Query>createClient(ros_typedb_msgs.srv.Query.class, "/ros_typedb/query");
  //   assertNotNull(typedb_client);

  // 	assertTrue(requestTypedbQuery(exampleInsert, "insert"));
  // }

  @Test
  public final void testGenerate() throws Exception {
    testLoadModels();

  	typedb_client = node.<ros_typedb_msgs.srv.Query>createClient(ros_typedb_msgs.srv.Query.class, "/ros_typedb/query");
    assertNotNull(typedb_client);

    TacticsGenerator generator = new TacticsGenerator();
    assertNotNull(generator);
	  String insert_query = generator.generateTQL(tacticsModel);
  	assertTrue(requestTypedbQuery(insert_query, "insert"));


	  java.lang.System.out.println("Generated TQL: \n" + insert_query);
  }
}