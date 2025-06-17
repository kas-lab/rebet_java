package org.ros2.rcljava.rebet_java.nodes;

import java.util.concurrent.TimeUnit;

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.concurrent.Callback;
import org.ros2.rcljava.node.Node;
import org.ros2.rcljava.publisher.Publisher;
import org.ros2.rcljava.timer.WallTimer;
import org.ros2.rcljava.node.NodeOptions;
import org.ros2.rcljava.parameters.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Test {
  public static void main(String[] args) throws InterruptedException {
    // Initialize RCL
    RCLJava.rclJavaInit();

    // Let's create a Node
    NodeOptions options = new NodeOptions();
    options.setCliArgs(new ArrayList<String>(Arrays.asList("--ros-args", "-p", "int_param:=42", "-p", "test_param:=false")));
    Node node = RCLJava.createNode("test_node", "", RCLJava.getDefaultContext(), options);

    
    //print node.parameterOverrides which is of type HashMap<String, ParameterVariant>
    node.getParameterOverrides().forEach((key, value) -> {
    switch (value.getType()) {
      case ParameterType.PARAMETER_BOOL:
        System.out.println("Parameter Name: " + key + ", Value: " + value.asBool());
        break;
      case ParameterType.PARAMETER_INTEGER:
        System.out.println("Parameter Name: " + key + ", Value: " + value.asInt());
        break;
      case ParameterType.PARAMETER_DOUBLE:
        System.out.println("double");
        break;
      case ParameterType.PARAMETER_STRING:
        System.out.println("string");
        break;
      case ParameterType.PARAMETER_BYTE_ARRAY:
        System.out.println("byte_array");
        break;
      case ParameterType.PARAMETER_BOOL_ARRAY:
        System.out.println("bool_array");
        break;
      case ParameterType.PARAMETER_INTEGER_ARRAY:
        System.out.println("integer_array");
        break;
      case ParameterType.PARAMETER_DOUBLE_ARRAY:
        System.out.println("double_array");
        break;
      case ParameterType.PARAMETER_STRING_ARRAY:
        System.out.println("string_array");
        break;
      case ParameterType.PARAMETER_NOT_SET:
        System.out.println("not set");
        break;
      default:
        throw new IllegalArgumentException(
            "Unexpected type from ParameterVariant: " + value.getType());
    }
    
    });

    node.declareParameter(new ParameterVariant("test_param", true));
    node.declareParameter(new ParameterVariant("int_param", 1337));
    node.declareParameter(new ParameterVariant("twint_param", 1337));



    System.out.println("Node created: " + node.getName());
    System.out.println("Node namespace should be /foo " + node.getNamespace());
    System.out.println("Node parameter should be false " + node.getParameter("test_param").asBool());
    System.out.println("Node parameter should be 42 " + node.getParameter("int_param").asInt());

    // Publishers are type safe, make sure to pass the message type
    Publisher<std_msgs.msg.String> publisher =
        node.<std_msgs.msg.String>createPublisher(std_msgs.msg.String.class, "topic");

    std_msgs.msg.String message = new std_msgs.msg.String();

    int publishCount = 0;

    while (RCLJava.ok()) {
      message.setData("Bello, world! " + publishCount);
      publishCount++;
      System.out.println("Publishing: [" + message.getData() + "]");
      publisher.publish(message);
      RCLJava.spinSome(node);
      Thread.sleep(500);
    }
  }
}
