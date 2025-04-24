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
    options.setAllowUndeclaredParameters(true);
    options.setCliArgs(new ArrayList<String>(Arrays.asList("--ros-args", "-p", "test_param:=foo")));
    Node node = RCLJava.createNode("test_node", "", RCLJava.getDefaultContext(), options);
    // node.declareParameter(new ParameterVariant("test_param", ""));
    System.out.println("Node created: " + node.getName());
    System.out.println("Node namespace should be /foo " + node.getNamespace());
    System.out.println("Node parameter should be foo " + node.getParameter("test_param").asString());


    // Publishers are type safe, make sure to pass the message type
    Publisher<std_msgs.msg.String> publisher =
        node.<std_msgs.msg.String>createPublisher(std_msgs.msg.String.class, "topic");

    std_msgs.msg.String message = new std_msgs.msg.String();

    int publishCount = 0;

    while (RCLJava.ok()) {
      message.setData("Hello, world! " + publishCount);
      publishCount++;
      System.out.println("Publishing: [" + message.getData() + "]");
      publisher.publish(message);
      RCLJava.spinSome(node);
      Thread.sleep(500);
    }
  }
}