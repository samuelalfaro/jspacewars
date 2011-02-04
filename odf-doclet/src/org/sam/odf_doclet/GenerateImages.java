package org.sam.odf_doclet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.batik.transcoder.TranscoderException;

public class GenerateImages {
	
	private static final Comparator<File> COMPARADOR_DE_FICHEROS = new Comparator<File>() {
		/** {@inheritDoc} */
		public int compare(File f1, File f2) {
			try {
				return f1.getCanonicalPath().compareTo(f2.getCanonicalPath());
			} catch (IOException e) {
				return 0;
			}
		}
	};
	
	private static final Comparator<Class<?>> COMPARADOR_DE_INTERFACES = new Comparator<Class<?>>() {
		/** {@inheritDoc} */
		public int compare(Class<?> e1, Class<?> e2) {
			return e1.getSimpleName().compareTo(e2.getSimpleName());
		}
	};
	
	private static final Comparator<Class<?>> COMPARADOR_DE_CLASES = new Comparator<Class<?>>() {
		/** {@inheritDoc} */
		public int compare(Class<?> e1, Class<?> e2) {
			return getHierarchicalName(e1).compareTo(getHierarchicalName(e2));
		}
	};
	
	private static Collection<Class<?>> packageClassesCollection;
	
	private static String getHierarchicalName(Class<?> clazz){
		String name = clazz.getSimpleName();
		Class<?> superClass = clazz.getSuperclass();
		while( superClass != null && !superClass.equals(Object.class) && 
				clazz.getPackage().equals(superClass.getPackage()) ){
				//packageClassesCollection.contains(superClass) ){			
			name = superClass.getSimpleName() + "\u25C1\u2500" + name;
			superClass = superClass.getSuperclass();
		}
		return name;
	}
	
	private static Collection<File> getPackages(File root){
		SortedSet<File> listadoPackages = new TreeSet<File>(COMPARADOR_DE_FICHEROS);
		Queue<File> directorios = new LinkedList<File>();
		directorios.add(root);
		listadoPackages.add(root);
		while(!directorios.isEmpty()){
			File directorioActual = directorios.poll();
			for(File archivo: directorioActual.listFiles())
				if( !archivo.isHidden() && archivo.isDirectory() ){
					directorios.add(archivo);
					listadoPackages.add(archivo);
				}
		}
		return listadoPackages;
	}
	
	private static String getPackageName(File pack, String rootPath) throws IOException{
		String absolutePath;
		absolutePath = pack.getCanonicalPath();
		if(absolutePath.length() == rootPath.length() )
			return "";
		return absolutePath.substring(rootPath.length()+1).replace('/', '.');
	}
	
	private static void getInterfacesAndClasses(ClassLoader loader, File pack, String packageName,
			Collection<Class<?>> interfacesSet, Collection<Class<?>> classesSet){
		interfacesSet.clear();
		classesSet.clear();
		
		for(File archivo: pack.listFiles()){
			if( !archivo.isHidden() && !archivo.isDirectory()){
				String fileName = archivo.getName();
				if(fileName.endsWith(".class") && fileName.indexOf('$') < 0 && !fileName.contains("package-info")){
					String className = String.format("%s%s",
							packageName.length() > 0 ? packageName + '.' : "",
							fileName.substring(0, fileName.length() - ".class".length())
					);
					try {
						Class<?> clazz = Class.forName( className, false, loader );
						if( clazz.isInterface() )
							interfacesSet.add(clazz);
						else
							classesSet.add(clazz);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * @param args ignorados
	 * @throws IOException 
	 * @throws TranscoderException 
	 */
	public static void main(String[] args) throws IOException, TranscoderException{
		
		File root = new File("/media/DATA/Samuel/Proyectos/jspacewars/bin");
		String rootPath = root.getCanonicalPath();

		ClassLoader classLoader = ClassLoaderTools.getLoader(
				"/media/DATA/Samuel/Proyectos/jspacewars/",
				"lib/ext/vecmath.jar:lib/gluegen-rt.jar:lib/jogl.jar:" +
				"lib/FengGUI.jar:lib/xstream-1.3.jar:lib/ibxm-alpha51.jar:" +
				"lib/jogg-0.0.7.jar:lib/jorbis-0.0.15.jar:bin"
		);
	
		SortedSet<Class<?>> packageInterfacesSet = new TreeSet<Class<?>>(COMPARADOR_DE_INTERFACES);
		packageClassesCollection = new LinkedList<Class<?>>();
		SortedSet<Class<?>> listadoDeClasesOrdenado = new TreeSet<Class<?>>(COMPARADOR_DE_CLASES);
		
		for(File pack: getPackages(root)){
			String packageName = getPackageName(pack, rootPath);
			getInterfacesAndClasses(classLoader, pack, packageName, packageInterfacesSet, packageClassesCollection);

			if( packageInterfacesSet.size() > 0 || packageClassesCollection.size() > 0 ){
				System.out.format("package: %s\n", packageName.length() > 0 ? packageName : "(default  package)" );
				for(Class<?> clazz:packageInterfacesSet){
					System.out.println("\t"+clazz.getCanonicalName());
					ClassToUML.toPNG( clazz, new FileOutputStream("output/"+clazz.getCanonicalName()+".png") );
					for(Class<?> subclazz : clazz.getDeclaredClasses())
						ClassToUML.toPNG( subclazz, new FileOutputStream("output/"+subclazz.getCanonicalName()+".png") );
				}if(packageClassesCollection.size() > 0){
					listadoDeClasesOrdenado.clear();
					for(Class<?> clazz:packageClassesCollection)
						listadoDeClasesOrdenado.add(clazz);
					for(Class<?> clazz:listadoDeClasesOrdenado){
						System.out.println("\t"+clazz.getCanonicalName());
						ClassToUML.toPNG( clazz, new FileOutputStream("output/"+clazz.getCanonicalName()+".png") );
						for(Class<?> subclazz : clazz.getDeclaredClasses())
							ClassToUML.toPNG( subclazz, new FileOutputStream("output/"+subclazz.getCanonicalName()+".png") );
					}
				}
			}
		}
	}
}
