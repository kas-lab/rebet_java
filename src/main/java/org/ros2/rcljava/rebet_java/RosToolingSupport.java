package org.ros2.rcljava.rebet_java;

import ros.RosFactory;
import system.RossystemFactory;
import org.eclipse.emf.ecore.resource.Resource;
import system.RosNode;
import java.util.List;
import system.System;

public class RosToolingSupport {

	

	public static ros.Parameter findParameter(ros.Node node, String parameterName) {
  		return node.getParameter()
		.stream()
		.filter(parameter -> parameter.getName().equals(parameterName)).
		findFirst().
		orElse(null);
	}

	public static system.RosParameter findRosParameter(RosNode rosNode, String parameterName) {
		return rosNode.getRosparameters()
		.stream()
		.filter(parameter -> parameter.getFrom().getName().equals(parameterName))
		.findFirst()
		.orElse(null);
	}

	public static system.RosParameter findRosParameter(ros.Parameter parameter, RosNode rosNode) {
		return rosNode.getRosparameters()
			.stream()
			.filter(rosParam -> rosParam.getFrom().equals(parameter))
			.findFirst()
			.orElse(null);
	}


	public static void updateRosParameter(system.RosParameter rosParam, rcl_interfaces.msg.Parameter parameter) {
		ros.ParameterValue p_val = RosToolingSupport.convertParameterValue(parameter.getValue());
		boolean valueSame = rosParam.getValue().equals(p_val);

		if(!valueSame)
		{
			java.lang.System.out.println("Parameter value changed for " + parameter.getName() + ", updating rossystem.");
			rosParam.setValue(p_val);
		}
	}

	public static void newRosParameter(RosNode rosNode, ros.Parameter originalParameter, rcl_interfaces.msg.Parameter parameter) {
		ros.ParameterValue p_val = RosToolingSupport.convertParameterValue(parameter.getValue());
		boolean valueSame = originalParameter.getValue().equals(p_val);

		if(!valueSame)
		{
			java.lang.System.out.println("Parameter value changed for " + parameter.getName() + ", updating rossystem.");
			system.RosParameter new_parameter = RossystemFactory.eINSTANCE.createRosParameter();
			new_parameter.setName(originalParameter.getName());
			new_parameter.setValue(p_val);
			new_parameter.setFrom(originalParameter);

			rosNode.getRosparameters().add(new_parameter);
		}
	}

	public static void newParameter(ros.Node node, rcl_interfaces.msg.Parameter parameter) {
		ros.Parameter new_parameter = RosFactory.eINSTANCE.createParameter();

		new_parameter.setName(parameter.getName());
		new_parameter.setValue(RosToolingSupport.convertParameterValue(parameter.getValue()));
		new_parameter.setType(RosToolingSupport.getParameterType(parameter.getValue().getType()));

		node.getParameter().add(new_parameter);
	}

	public static ros.ParameterType getParameterType(byte type) {
		switch (type) {
			case rcl_interfaces.msg.ParameterType.PARAMETER_BOOL:
				return RosFactory.eINSTANCE.createParameterBooleanType();
			case rcl_interfaces.msg.ParameterType.PARAMETER_INTEGER:
				return RosFactory.eINSTANCE.createParameterIntegerType();
			case rcl_interfaces.msg.ParameterType.PARAMETER_DOUBLE:
				return RosFactory.eINSTANCE.createParameterDoubleType();
			case rcl_interfaces.msg.ParameterType.PARAMETER_STRING:
				return RosFactory.eINSTANCE.createParameterStringType();
			case rcl_interfaces.msg.ParameterType.PARAMETER_BYTE_ARRAY:
				return RosFactory.eINSTANCE.createParameterBase64Type();
			case rcl_interfaces.msg.ParameterType.PARAMETER_INTEGER_ARRAY:
				ros.ParameterArrayType int_array = RosFactory.eINSTANCE.createParameterArrayType();
				int_array.setType(RosFactory.eINSTANCE.createParameterIntegerType());
				return int_array;
			case rcl_interfaces.msg.ParameterType.PARAMETER_DOUBLE_ARRAY:
				ros.ParameterArrayType double_array = RosFactory.eINSTANCE.createParameterArrayType();
				double_array.setType(RosFactory.eINSTANCE.createParameterDoubleType());
				return double_array;
			case rcl_interfaces.msg.ParameterType.PARAMETER_BOOL_ARRAY:
				ros.ParameterArrayType bool_array = RosFactory.eINSTANCE.createParameterArrayType();
				bool_array.setType(RosFactory.eINSTANCE.createParameterBooleanType());
				return bool_array;
			case rcl_interfaces.msg.ParameterType.PARAMETER_STRING_ARRAY:
				ros.ParameterArrayType string_array = RosFactory.eINSTANCE.createParameterArrayType();
				string_array.setType(RosFactory.eINSTANCE.createParameterStringType());
				return string_array;
			default:
				throw new IllegalArgumentException("Unsupported parameter type: " + type);
		}
  	}

	public static RosNode nodeInRosSystems(String nodeName, List<Resource> rosSystemResources) {
		for (Resource resource : rosSystemResources) {
			System sys = (System) resource.getContents().get(0);
			for (system.Component component : sys.getComponents()) {
				if (!(component instanceof system.RosNode)) {
					continue;
				}
				system.RosNode ros_node = (system.RosNode) component;

				if (nodeName.equals(ros_node.getName()))
				{
					return ros_node;
				}
			}
		}
		return null;
	}

	public static ros.ParameterValue convertParameterValue(rcl_interfaces.msg.ParameterValue paramValue) {
		if (paramValue.getType() == rcl_interfaces.msg.ParameterType.PARAMETER_DOUBLE_ARRAY) {
			ros.ParameterSequence value = RosFactory.eINSTANCE.createParameterSequence();
			for (Double d : paramValue.getDoubleArrayValue()) {
				ros.ParameterDouble param = RosFactory.eINSTANCE.createParameterDouble();
				param.setValue(d);
				value.getValue().add(param);
			}
			return value;
		} else if (paramValue.getType() == rcl_interfaces.msg.ParameterType.PARAMETER_STRING_ARRAY) {
			ros.ParameterSequence value = RosFactory.eINSTANCE.createParameterSequence();
			for (String s : paramValue.getStringArrayValue()) {
				ros.ParameterString param = RosFactory.eINSTANCE.createParameterString();
				param.setValue(s);
				value.getValue().add(param);
			}
			return value;
		} else if (paramValue.getType() == rcl_interfaces.msg.ParameterType.PARAMETER_BOOL_ARRAY) {
			ros.ParameterSequence value = RosFactory.eINSTANCE.createParameterSequence();
			for (Boolean b : paramValue.getBoolArrayValue()) {
				ros.ParameterBoolean param = RosFactory.eINSTANCE.createParameterBoolean();
				param.setValue(b);
				value.getValue().add(param);
			}
			return value;
		} else if (paramValue.getType() == rcl_interfaces.msg.ParameterType.PARAMETER_BYTE_ARRAY) {
			    ros.ParameterBase64 value = RosFactory.eINSTANCE.createParameterBase64();
				// Convert List<Byte> to byte[]
				List<Byte> byteList = paramValue.getByteArrayValue();
				byte[] byteArray = new byte[byteList.size()];
				for (int i = 0; i < byteList.size(); i++) {
					byteArray[i] = byteList.get(i);
				}
				value.setValue(byteArray);
				return value;
		} else if (paramValue.getType() == rcl_interfaces.msg.ParameterType.PARAMETER_INTEGER_ARRAY) {
			ros.ParameterSequence value = RosFactory.eINSTANCE.createParameterSequence();
			for (Long l : paramValue.getIntegerArrayValue()) {
				ros.ParameterInteger param = RosFactory.eINSTANCE.createParameterInteger();
				int valueInt = l.intValue();
				param.setValue(valueInt);
				value.getValue().add(param);
			}
			return value;
		} else if (paramValue.getType() == rcl_interfaces.msg.ParameterType.PARAMETER_DOUBLE) {
			ros.ParameterDouble param = RosFactory.eINSTANCE.createParameterDouble();
			param.setValue(paramValue.getDoubleValue());
			return param;
		} else if (paramValue.getType() == rcl_interfaces.msg.ParameterType.PARAMETER_STRING) {
			ros.ParameterString param = RosFactory.eINSTANCE.createParameterString();
			param.setValue(paramValue.getStringValue());
			return param;
		} else if (paramValue.getType() == rcl_interfaces.msg.ParameterType.PARAMETER_BOOL) {
			ros.ParameterBoolean param = RosFactory.eINSTANCE.createParameterBoolean();
			param.setValue(paramValue.getBoolValue());
			return param;
		} else if (paramValue.getType() == rcl_interfaces.msg.ParameterType.PARAMETER_INTEGER) {
			ros.ParameterInteger param = RosFactory.eINSTANCE.createParameterInteger();
			int valueInt = (int) paramValue.getIntegerValue(); // Cast long to int

			param.setValue(valueInt);
			return param;
		} else {
			throw new IllegalArgumentException("Unsupported parameter type: " + paramValue.getType());
		}
	}

	public static Object valueOf(ros.ParameterValue param) {
		if (param instanceof ros.ParameterBoolean) {
			return ((ros.ParameterBoolean) param).isValue();
		} else if (param instanceof ros.ParameterInteger) {
			return ((ros.ParameterInteger) param).getValue();
		} else if (param instanceof ros.ParameterDouble) {
			return ((ros.ParameterDouble) param).getValue();
		} else if (param instanceof ros.ParameterString) {
			return ((ros.ParameterString) param).getValue();
		} else if (param instanceof ros.ParameterBase64) {
			return ((ros.ParameterBase64) param).getValue();
		} else if (param instanceof ros.ParameterSequence) {
			//Return list of valueOf of inside elements
			return ((ros.ParameterSequence) param).getValue().stream()
				.map(RosToolingSupport::valueOf)
				.toList();
		}
		else if (param == null) {
			return null;
		}
		throw new IllegalArgumentException("Unsupported parameter type: " + param.getClass().getName());
	}



}