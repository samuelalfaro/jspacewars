/* 
 * Loader.java
 * 
 * Copyright (c) 2008-2010
 * Samuel Alfaro Jiménez <samuelalfaro at gmail.com>.
 * All rights reserved.
 * 
 * This file is part of jSpaceWars.
 * 
 * jSpaceWars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jSpaceWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jSpaceWars. If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.jspacewars.serialization;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.sam.elementos.Cache;
import org.sam.jspacewars.servidor.elementos.Canion;
import org.sam.jspacewars.servidor.elementos.Elemento;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class Loader {
	
	private Loader(){
	}
	
	private static void loadToCache( ObjectInputStream in, Cache<Elemento> cache ) throws IOException {
		try {
			while (true) {
				Object o = in.readObject();
				if (o != null){
					Elemento e = (Elemento)o;
//					System.out.println(e+" -->"+e.getLimites());
					cache.addPrototipo(e);
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (EOFException eof) {
			in.close();
		}
	}

	static public void loadData( String path, Cache<Elemento> cache ) throws FileNotFoundException, IOException{

		Canion.setCache( cache );
		XStream xStream = new XStream( new DomDriver() );

		ElementosConverters.register( xStream );
		loadToCache( xStream.createObjectInputStream( new FileReader( path ) ), cache );
	}
}
