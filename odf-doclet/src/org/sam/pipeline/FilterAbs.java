/* 
 * FilterAbs.java
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
package org.sam.pipeline;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public abstract class FilterAbs implements Filter{
	
	private static class RunnableSource implements Runnable{
		
		private final Filterable source;
		private OutputStream out;
		
		RunnableSource(Filterable source){
			this.source = source;
		}
		
		public void run() {
			try {
				source.process(out);
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void setOutput(OutputStream out){
			this.out = out;
		}
	}
	
	private RunnableSource source;
	
	public final void setSource(Filterable source) throws IOException {
		this.source = new RunnableSource(source);
	}
		
	public final void process(OutputStream out) throws IOException{
		if(source == null)
			throw new RuntimeException("Source is null");
		
		PipedOutputStream pipeOut = new PipedOutputStream();
		source.setOutput(pipeOut);
		Thread sourceThread = new Thread(source);
		
		PipedInputStream pipeIn;
		pipeIn = new PipedInputStream();
		pipeIn.connect(pipeOut);
		
		sourceThread.start();
		process(pipeIn, out);
		try {
			sourceThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}
}