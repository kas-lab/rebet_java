package org.ros2.rcljava.rebet_java;

import java.util.ArrayList;
import java.util.List;

public class RosTypeDBInterface {

	@SuppressWarnings("unchecked")
	public static <T> Object getParameterValue(Class<T> clazz, rcl_interfaces.msg.ParameterValue value) {
		switch (value.getType()) {
			case rcl_interfaces.msg.ParameterType.PARAMETER_BOOL:
				if (clazz == Boolean.class) return (T) Boolean.valueOf(value.getBoolValue());
				break;
			case rcl_interfaces.msg.ParameterType.PARAMETER_INTEGER:
				if (clazz == Long.class) return (T) Long.valueOf(value.getIntegerValue());
				break;
			case rcl_interfaces.msg.ParameterType.PARAMETER_DOUBLE:
				if (clazz == Double.class) return (T) Double.valueOf(value.getDoubleValue());
				break;
			case rcl_interfaces.msg.ParameterType.PARAMETER_STRING:
				if (clazz == String.class) return (T) String.valueOf(value.getStringValue());
				break;
			case rcl_interfaces.msg.ParameterType.PARAMETER_BYTE_ARRAY:
				if (clazz == List.class) return (List<T>) value.getByteArrayValue();
				break;
			case rcl_interfaces.msg.ParameterType.PARAMETER_BOOL_ARRAY:
				if (clazz == List.class) return (List<T>) value.getBoolArrayValue();
				break;
			case rcl_interfaces.msg.ParameterType.PARAMETER_INTEGER_ARRAY:
				if (clazz == List.class) return (List<T>) value.getIntegerArrayValue();
				break;
			case rcl_interfaces.msg.ParameterType.PARAMETER_DOUBLE_ARRAY:
				if (clazz == List.class) return (List<T>) value.getDoubleArrayValue();
				break;
			case rcl_interfaces.msg.ParameterType.PARAMETER_STRING_ARRAY:
				if (clazz == List.class) return (List<T>) value.getStringArrayValue();
				break;
			default:
				throw new IllegalArgumentException("Type from ParameterValue is not set or unknown");
		}
		throw new IllegalArgumentException("Type mismatch or unsupported type: " + clazz.getName());
	}

	public static String printParameterValue(rcl_interfaces.msg.ParameterValue value){
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
		sb.append("IntegerValue: ").append(value.getIntegerValue()).append(", ");
		return sb.toString();
	}

	public static <T> List<T> extractParameterValues(Class<T> elementType, List<ros_typedb_msgs.msg.IndexList> index_lists, ros_typedb_msgs.msg.ResultTree result_tree, String INDEX_VAR) {
		List<T> parameterValues = new ArrayList<>(index_lists.size());
		for (int i = 0; i < index_lists.size(); i++) {
        	parameterValues.add(null);
    	}

		for (ros_typedb_msgs.msg.IndexList index_list : index_lists) {
			long parameterIndex = -1;
			T parameterValue = null;
			for (int index : index_list.getIndex())
			{
				ros_typedb_msgs.msg.QueryResult q_r = result_tree.getResults().get(index);
				if(q_r.getType() != ros_typedb_msgs.msg.QueryResult.THING) continue;

				var thing = q_r.getThing();
				if (thing.getVariableName().equals(INDEX_VAR)) {
					for (ros_typedb_msgs.msg.Attribute attr : thing.getAttributes()) {
						parameterIndex = attr.getValue().getIntegerArrayValue().get(0);
					}
				}
				if (thing.getVariableName().equals("value")) {
					for (ros_typedb_msgs.msg.Attribute attr : thing.getAttributes()) {
						var valueList = (List<T>) RosTypeDBInterface.getParameterValue(List.class, attr.getValue());
						parameterValue = elementType.cast(valueList.get(0));
					}
				}	
				
				if(parameterIndex >= 0)
				{
					parameterValues.set((int)parameterIndex, parameterValue);
				}
			}
		}
		return parameterValues;
	}

	public static String extractStringAttribute(ros_typedb_msgs.msg.ResultTree result_tree, String thingVariableName, String attVariableName)
	{
		for (ros_typedb_msgs.msg.QueryResult q_result : result_tree.getResults()) {
			if (q_result.getType() != ros_typedb_msgs.msg.QueryResult.THING) continue;
			
			var thing = q_result.getThing();
			if (!thing.getVariableName().equals(thingVariableName)) continue;

			for (ros_typedb_msgs.msg.Attribute attr : thing.getAttributes()) {
				if (attr.getVariableName().equals(attVariableName)) {
					return attr.getValue().getStringArrayValue().get(0);
				}
			}
		}
		throw new IllegalStateException("Node name not found in result tree");
	}

	public static String queryResultTypeToString(int type) {
		switch (type) {
			case ros_typedb_msgs.msg.QueryResult.ATTRIBUTE:
				return "ATTRIBUTE";
			case ros_typedb_msgs.msg.QueryResult.THING:
				return "THING";
			case ros_typedb_msgs.msg.QueryResult.SUB_QUERY:
				return "SUB_QUERY";
			default:
				return "UNKNOWN";
		}
	}

	public static String thingTypeToString(int type) {
		switch (type) {
			case ros_typedb_msgs.msg.Thing.ENTITY:
				return "ENTITY";
			case ros_typedb_msgs.msg.Thing.RELATION:
				return "RELATION";
			default:
				return "UNKNOWN";
		}
	}

	public static String printAttribute(ros_typedb_msgs.msg.Attribute attribute) {
		StringBuilder sb = new StringBuilder();
		sb.append("Attribute: ");
		sb.append("\n\tVariableName: ").append(attribute.getVariableName()).append(", ");
		sb.append("\n\tLabel: ").append(attribute.getLabel()).append(", ");
		sb.append("\n\tValue: ").append(RosTypeDBInterface.printParameterValue(attribute.getValue()));
		return sb.toString();
	}

	public static String printThing(ros_typedb_msgs.msg.Thing thing) {
		StringBuilder sb = new StringBuilder();
		sb.append("Thing: ");
		sb.append("\n\tThingType: ").append(thingTypeToString(thing.getType())).append(", ");
		sb.append("\n\tVariableName: ").append(thing.getVariableName()).append(", ");
		sb.append("\n\tTypeName: ").append(thing.getTypeName()).append(", ");
		sb.append("\n\tAttributes: ");
		for (ros_typedb_msgs.msg.Attribute attr : thing.getAttributes()) {
			
			sb.append("\n\t").append(RosTypeDBInterface.printAttribute(attr));
		}
		return sb.toString();
	}

	public static String printQueryResult(ros_typedb_msgs.msg.QueryResult result) {
		StringBuilder sb = new StringBuilder();
		sb.append("QueryResult: ");
		sb.append("Type: ").append(RosTypeDBInterface.queryResultTypeToString(result.getType())).append(", ");
		if (result.getType() == ros_typedb_msgs.msg.QueryResult.ATTRIBUTE) {
			sb.append(RosTypeDBInterface.printAttribute((result.getAttribute())));
		} else if (result.getType() == ros_typedb_msgs.msg.QueryResult.THING) {
			sb.append(RosTypeDBInterface.printThing(result.getThing()));
		}
		else if (result.getType() == ros_typedb_msgs.msg.QueryResult.SUB_QUERY) {
			sb.append("SubQueryName: ").append(result.getSubQueryName()).append(", ");
			sb.append("ChildrenIndex: ").append(result.getChildrenIndex());
		}
		return sb.toString();
	}

}