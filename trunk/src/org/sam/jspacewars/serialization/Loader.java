package org.sam.jspacewars.serialization;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.sam.elementos.Cache;
import org.sam.jspacewars.elementos.Canion;
import org.sam.jspacewars.elementos.Elemento;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class Loader {
	
	private Loader(){
	}
	
	private static void loadToCache( ObjectInputStream in, Cache<Elemento> cache )
			throws IOException {
		try {
			while (true) {
				Object o = in.readObject();
				if (o != null)
					cache.addPrototipo((Elemento) o);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (EOFException eof) {
			in.close();
		}
	}

	static public void loadData(Cache<Elemento> cache) throws FileNotFoundException, IOException {

		Canion.setCache(cache);
		XStream xStream = new XStream(new DomDriver());

		ElementosConverters.register(xStream);
		loadToCache(
				xStream.createObjectInputStream(new FileReader("resources/elementos-instancias3D-stream-sh.xml")),
				cache
		);
	}
}
