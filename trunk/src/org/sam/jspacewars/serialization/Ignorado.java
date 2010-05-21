package org.sam.jspacewars.serialization;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class Ignorado implements Converter {
	
//	private static String tabs ="";
	
	@SuppressWarnings("unchecked")
	public boolean canConvert(Class clazz) {
		return Ignorado.class == clazz;
	}

	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
//		System.out.println(tabs+'<'+reader.getNodeName()+'>');
		while(reader.hasMoreChildren()){
//			tabs = tabs.concat("   ");
			reader.moveDown();
			unmarshal(reader, context);
			reader.moveUp();
//			tabs = tabs.substring(0, tabs.length()-3);
		}
//		System.out.println(tabs+"</"+reader.getNodeName()+'>');
		return null;
	}
}
