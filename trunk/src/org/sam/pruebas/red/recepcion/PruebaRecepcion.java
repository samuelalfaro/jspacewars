package org.sam.pruebas.red.recepcion;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.TreeSet;

import org.sam.util.Lista;

/**
 * @author Samuel
 *
 */
interface Producto{
	public void tratar();
	public boolean mayorQue(Producto otro);
	public boolean menorQue(Producto otro);
	public boolean igualA(Producto otro);
}

interface Productor<E>{
	public int size();
	public void inciarProduccion();
	public E producir();
}

class ProductoConcreto implements Producto{
	int valor;

	public ProductoConcreto(int valor){
		this.valor = valor;
	}
	
	public void tratar() {
	}

	public boolean mayorQue(Producto otro) {
		return valor >((ProductoConcreto)otro).valor;
	}

	public boolean menorQue(Producto otro) {
		return valor <((ProductoConcreto)otro).valor;
	}

	public boolean igualA(Producto otro) {
		return valor ==((ProductoConcreto)otro).valor;
	}
}

public class PruebaRecepcion {
	
	@SuppressWarnings("unused")
	private static final InterruptedException terminado = new InterruptedException();
	
	private class Comparador implements Comparator<Producto>{
		public int compare(Producto o1, Producto o2) {
			return ((ProductoConcreto)o1).valor - ((ProductoConcreto)o2).valor;
		}
	}
	
	private class ProductorConcreto implements Productor<Producto>{
		private Collection<Producto> contenido;
		private Iterator<Producto> iterator;

		ProductorConcreto(Collection<Producto> contenido){
			this.contenido = contenido;
		}
		
		public int size() {
			return contenido.size();
		}

		public void inciarProduccion(){
			iterator = contenido.iterator();
		}
		
		public Producto producir() {
			return iterator.next();
		}
	}

	@SuppressWarnings("unused")
	private static PrintStream log1, log2, logOp, logRe;
	
	private final Productor<Producto> productor;
	private final List<Producto> consumidor;

	public PruebaRecepcion() {
		productor = new ProductorConcreto(new TreeSet<Producto>(new Comparador()));
		consumidor = new Lista<Producto>();
	}

	public static void recibir(Productor<Producto> unProductor, List<Producto> unConsumidor){
		unProductor.inciarProduccion();
		int nElementos = unProductor.size();
		int i = 0;
		ListIterator <Producto>iConsumidor = unConsumidor.listIterator();

		if(iConsumidor.hasNext() && i < nElementos){
			i++;
			Producto producto = unProductor.producir();
			Producto almacenado = iConsumidor.next();
			do{
				if(almacenado.menorQue(producto)){
					//logOp.print("     1.1 Eliminando "+((ProductoConcreto)almacenado).valor+"\n");
					iConsumidor.remove();
					if(!iConsumidor.hasNext()){
						unConsumidor.add(producto);
						break;
					}
					almacenado = iConsumidor.next();
				}else if(almacenado.mayorQue(producto)){
					//logOp.print("     1.2 Añadiendo "+((ProductoConcreto)producto).valor+"\n");
					iConsumidor.add(producto);
					if(i == nElementos){
						iConsumidor.remove();
						break;
					}
					i++;
					producto = unProductor.producir();
				}else{
					//logOp.print("     1.3 Tratando "+((ProductoConcreto)almacenado).valor+"\n");
					almacenado.tratar();
					if(i == nElementos || !iConsumidor.hasNext()) break;
					i++;
					producto = unProductor.producir();
					almacenado = iConsumidor.next();
				}
			}while(true);
		}
		if(i<nElementos)
			// Si se reciben mas elementos de los q hay, se crean.
			do{
				i++;
				//logOp.print("2 Añadiendo nuevos ( "+i+" / "+nElementos+" )\n");
				Producto nuevo = unProductor.producir();
				nuevo.tratar();
				unConsumidor.add(nuevo);
			}while(i < nElementos);
		else 
			while(iConsumidor.hasNext()){
				//logOp.print("3 Eliminando viejos\n");
				iConsumidor.next();
				iConsumidor.remove();
			}
	}

	public static boolean iguales(Collection<Producto> c1, Collection<Producto> c2){
		if(c1.size()!= c2.size())
			return false;
		Iterator<Producto> it1 = c1.iterator();
		Iterator<Producto> it2 = c2.iterator();
		for(int i=0, tam = c1.size(); i<tam; i++){
			if(!it1.next().igualA(it2.next()))
				return false;
		}
		return true;
	}
	
	public static void listar(PrintStream out, Collection<Producto> c){
		out.println("Contenido de la lista:");
		Iterator<Producto> it = c.iterator();
		for(int i=0, tam = c.size(); i<tam; i++){
			out.println("    "+((ProductoConcreto)it.next()).valor);
		}
		out.println();
	}
	
	public static void comprobar(Productor<Producto> productor, List<Producto> consumidor){
		logRe.println("Comprobando resultados\n");
		listar(log1, ((ProductorConcreto)productor).contenido);
		listar(log2, consumidor);
		recibir(productor,consumidor);
		listar(logRe, consumidor);
		logRe.println("Elementos del productor:  "+((ProductorConcreto)productor).contenido.size());
		logRe.println("Elementos del consumidor: "+consumidor.size());
		boolean iguales = iguales(((ProductorConcreto)productor).contenido,consumidor);
		logRe.println("Iguales: "+iguales +"\n");
	}
	
	public static void main(String arg[]){
//		JFrame frame = new JFrame("Prueba Recepcion");
//
//		frame.setSize(400,400);
//		frame.setDefaultCloseOperation(3);
//		
//		JTextArea tList1 = new JTextArea();
//		tList1.setEditable(false);
//		tList1.setForeground(Color.BLUE.darker());
//		tList1.setRows(40);
//		tList1.setColumns(5);
//		log1 = Consola.getLog(tList1);
//		
//		JTextArea tList2 = new JTextArea();
//		tList2.setEditable(false);
//		tList2.setForeground(Color.GREEN.darker());
//		tList2.setRows(40);
//		tList2.setColumns(5);
//		log2 = Consola.getLog(tList2);
//		
//		JTextArea tOperaciones = new JTextArea();
//		tOperaciones.setEditable(false);
//		tOperaciones.setForeground(Color.BLUE);
//		tOperaciones.setRows(40);
//		tOperaciones.setColumns(5);
//		logOp = Consola.getLog(tOperaciones);
//		
//		JTextArea tResultados = new JTextArea();
//		tResultados.setEditable(false);
//		tResultados.setForeground(Color.MAGENTA.darker().darker());
//		tResultados.setRows(40);
//		tResultados.setColumns(5);
//		logRe = Consola.getLog(tResultados);
//
//		// Area donde se mostrará System.err
//		JTextArea tErr = new JTextArea();
//		tErr.setEditable(false);
//		tErr.setForeground(Color.RED);
//		tErr.setRows(10);
//		tErr.setColumns(40);
//		
//		frame.getContentPane().setLayout(new BorderLayout());
//		JPanel titulos = new JPanel(new GridLayout(1,4));
//		titulos.add(new JLabel("Productor"));
//		titulos.add(new JLabel("Consumidor"));
//		titulos.add(new JLabel("Operaciones"));
//		titulos.add(new JLabel("Resultados"));
//		frame.getContentPane().add(titulos,BorderLayout.NORTH);
//		JPanel logs = new JPanel(new GridLayout(1,4));
//		logs.add(new JScrollPane(tList1));
//		logs.add(new JScrollPane(tList2));
//		logs.add(new JScrollPane(tOperaciones));
//		logs.add(new JScrollPane(tResultados));
//		frame.getContentPane().add(logs,BorderLayout.CENTER);
//		frame.pack();
//		frame.setVisible(true);
		
		PruebaRecepcion me = new PruebaRecepcion();
		Random aleatorio = new Random();
		
		for(int i = 0; i <10000; i++){
			((ProductorConcreto)me.productor).contenido.clear();
			for(int j = 0; j < aleatorio.nextInt(1000); j++)
				((ProductorConcreto)me.productor).contenido.add(new ProductoConcreto(aleatorio.nextInt(100)));
			//comprobar(me.productor,me.consumidor);
			recibir(me.productor,me.consumidor);
			if(!iguales(((ProductorConcreto)me.productor).contenido,me.consumidor)){
				System.out.println(i);
				break;
			}
			System.out.println("\t"+i+" ----> Ok");
		}
		
	}
}