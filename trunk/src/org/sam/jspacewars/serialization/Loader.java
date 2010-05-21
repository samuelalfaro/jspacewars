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
	
	static public void loadData(Cache<Elemento> cache) throws FileNotFoundException, IOException {

		Canion.setCache(cache);
		XStream xStream = new XStream(new DomDriver());

		/*
		 * XStream xStream = new XStream(new DomDriver()) {
		 * 
		 * @Override protected MapperWrapper wrapMapper(MapperWrapper next) {
		 * MapperWrapper myMapper = new MapperWrapper(next) {
		 * 
		 * @SuppressWarnings("unchecked")
		 * 
		 * @Override public boolean shouldSerializeMember(Class definedIn,
		 * String fieldName) { try { realClass(realMember(definedIn,
		 * fieldName)); } catch
		 * (com.thoughtworks.xstream.mapper.CannotResolveClassException ex) {
		 * return false; } return super.shouldSerializeMember(definedIn,
		 * fieldName); } }; return myMapper; } };
		 */

		ElementosConverters.register(xStream);
		loadToCache(cache, xStream.createObjectInputStream(new FileReader("resources/elementos-instancias3D-stream-sh.xml")));
		// loadToCache(cache, xStream.createObjectInputStream(new
		// FileReader("disparos.xml")));
	}

	private static void loadToCache(Cache<Elemento> cache, ObjectInputStream in)
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
}
