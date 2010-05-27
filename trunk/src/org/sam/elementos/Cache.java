package org.sam.elementos;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;


/**
 * @author Samuel
 *
 */
public class Cache<T extends PrototipoCacheable<T>>{

	@SuppressWarnings("serial")
	private static class PrototipoDesconocidoException extends RuntimeException {
		PrototipoDesconocidoException(String str){
			super(str);
		}
	}

	private class Nodo{
		PrototipoCacheable<T> prototipo;
		Queue<T> instancias;
		
		Nodo(PrototipoCacheable<T> prototipo){
			this.prototipo = prototipo;
			this.instancias = new LinkedList<T>();
		}
	}
	
	private int elementos;
	private int tam;
	private int tamMax;
	private Map <Integer,Nodo> prototipos;
	private int dCreados, dRecuperados;
	
	private static Comparator<Integer> COMPARADOR = new Comparator<Integer>(){
		public int compare(Integer o1, Integer o2) {
			return o1.hashCode() - o2.hashCode();
		}
	};
	
	public Cache(int tam){
		this.prototipos = new TreeMap<Integer,Nodo>(COMPARADOR);
		this.elementos = 0;
		this.tam = tam;
		this.tamMax = (tam*125)/100;
		this.dCreados = 0;
		this.dRecuperados = 0;
	}
	
	public void addPrototipo(PrototipoCacheable<T> prototipo){
		prototipos.put(prototipo.hashCode(),new Nodo(prototipo));
	}
	
	public T newObject(int tipo) throws PrototipoDesconocidoException{
		Nodo nodo = prototipos.get(tipo);
		try{
			T newObject;
			if (nodo.instancias.size()>0){
				elementos--;
				dRecuperados++;
				newObject = nodo.instancias.poll();
			}else{
				dCreados++;
				newObject = nodo.prototipo.clone();
			}
			newObject.reset();
			return newObject;
		}catch(NullPointerException e){
			throw new PrototipoDesconocidoException("\nNo existe ningún prototipo con el identificador: "+tipo);
		}
	}
	
	public void cached(T elemento) throws PrototipoDesconocidoException{
		try{
			prototipos.get(elemento.hashCode()).instancias.offer(elemento);
			elementos++;
			if (elementos > tamMax)
				while( elementos > tam ){
					for(Nodo nodo: prototipos.values()){
						if(nodo.instancias.size()>0){
							nodo.instancias.poll();
							elementos --;
						}
					}
				}
		}catch(NullPointerException e){
			throw new PrototipoDesconocidoException("Prototipo "+elemento.hashCode()+" desconcido");
		}
	}
	
	public String toString(){
		String me = "Caché con capacidad para "+tam+" elementos:\n"+
		"\tElementos en cache:\t"+elementos+"\n"+
		"\tElementos creados: \t"+dCreados+"\n"+
		"\tElementos recuperados: \t"+dRecuperados+"\n";
		return me;
	}
}
