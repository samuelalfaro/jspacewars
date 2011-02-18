/* 
 * XMLPrinter.java
 * 
 * Copyright (c) 2011 Samuel Alfaro Jiménez <samuelalfaro at gmail dot com>.
 * All rights reserved.
 * 
 * This file is part of odf-doclet.
 * 
 * odf-doclet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * odf-doclet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with odf-doclet.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.xml;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

/**
 * 
 */
public final class XMLPrinter{
	
	/*
	private static class StringPrintStream extends PrintStream{
		StringPrintStream(){
			super( new java.io.ByteArrayOutputStream() );
		}
		
		public String toString(String enconding) throws java.io.IOException{
			out.flush();
			return ((java.io.ByteArrayOutputStream)out).toString(enconding);
		}
		
		public String toString(){
			try {
				return toString("UTF8");
			} catch (java.io.IOException e) {
				return ((java.io.ByteArrayOutputStream)out).toString();
			}
		}
	}//*/
	
	private interface TabsManager{
		
		static final class Default implements TabsManager{
			final public void add(){}
			final public void remove(){}
			final public String get(){
				return "";
			}
		}
		
		static final class Tabulate implements TabsManager{
			
			private String tabs = "";
			
			final public void add(){
				tabs = tabs.concat("\t");
			}
			
			final public void remove(){
				tabs = tabs.substring(1);
			}
			
			final public String get(){
				return tabs;
			}
		}
		
		void add();
		void remove();
		String get();
	}
	
	
	private static final PrintStream toPrintStream(OutputStream out){
		if (out instanceof PrintStream)
			return (PrintStream)out;
		try {
			return new PrintStream(out, false, "UTF-8");
		} catch (UnsupportedEncodingException ignorada) {
			assert(false): ignorada.toString();
			return null;
		}
	}
	
	private int previousNodeDepth;
	private final TabsManager tabs;
	private final String eol;
	private final Deque<String> nodeStack;
	private final StringBuffer attributes;
	private final PrintStream out;
	
	public XMLPrinter(	OutputStream out, boolean tabulate ) {
		this.previousNodeDepth = 0;
		if(tabulate){
			this.tabs = new TabsManager.Tabulate();
			this.eol = System.getProperty("line.separator");
		}else{
			this.tabs = new TabsManager.Default();
			this.eol = "";
		}
		this.nodeStack = new ArrayDeque<String>();
		this.attributes = new StringBuffer(512);
		this.attributes.setLength(0);
		this.out = toPrintStream(out);
	}
	
	public XMLPrinter(	OutputStream out ) {
		this(out, false);
	}
	
	final private boolean flushPreviousNode(){
		if( nodeStack.size() == previousNodeDepth )
			return false;
		if( attributes.length() > 0 ){
			out.append(attributes.toString());
			attributes.setLength(0);
		}
		return true;
	}
	
	final public void openNode(String nodeName) {
		if(flushPreviousNode())
			out.append(">").append(eol);
		out.append(tabs.get()).append('<').append(nodeName);
		nodeStack.push(nodeName);
		tabs.add();
	}
	
	final public void addAttribute(String name, String value) {
		attributes.append(' ').append(name).append("=\"").append(value).append('\"');
	}
	
	final public void addAttribute(String name, boolean value) {
		attributes.append(' ').append(name).append("=\"").append(value).append('\"');
	}
	
	final public void addAttribute(String name, char value) {
		attributes.append(' ').append(name).append("=\"").append(value).append('\"');
	}
	
	final public void addAttribute(String name, int value) {
		attributes.append(' ').append(name).append("=\"").append(value).append('\"');
	}
	
	final public void addAttribute(String name, long value) {
		attributes.append(' ').append(name).append("=\"").append(value).append('\"');
	}
	
	final public void addAttribute(String name, float value) {
		attributes.append(' ').append(name).append("=\"").append(value).append('\"');
	}
	
	final public void addAttribute(String name, double value) {
		attributes.append(' ').append(name).append("=\"").append(value).append('\"');
	}
	
	final public void addAttribute(String name, Object value) {
		attributes.append(' ').append(name).append("=\"").append(value).append('\"');
	}
	
	private transient boolean hasContent = false;
	
	final public void print(String content) {
		hasContent = content != null && content.length() > 0;
		if( hasContent ){
			flushPreviousNode();
			out.append('>').append("<![CDATA[").append(content).append("]]>");
		}
	}
	
	final public void print(String nodeName, String content) {
		if( content != null && content.length() > 0 ){
			openNode(nodeName);
				print(content);
			closeNode();
		}
	}
	
	final public <T extends XMLSerializable> void print(String nodeName, Collection<T> collection){
		if( collection != null && collection.size() > 0 ){
			openNode( nodeName );
			for(T element: collection)
				element.toXML(this);
			closeNode();
		}
	}
	
	final public void closeNode() {
		tabs.remove();
		if( nodeStack.size() != previousNodeDepth ){
			if(hasContent){
				out.append("</").append(nodeStack.pop()).append(">").append(eol);
				hasContent = false;
			}else{
				flushPreviousNode();
				out.append("/>").append(eol);
				nodeStack.pop();
			}
		}else
			out.append(tabs.get()).append("</").append(nodeStack.pop()).append(">").append(eol);
		previousNodeDepth = nodeStack.size();
	}
}