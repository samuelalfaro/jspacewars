package tips;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Clase que muestra todas las clases accesibles de un paquete.<br/>
 */
public class ListClassFromAPackage {
	
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
	
	private static String getHierarchicalComposeName(Class<?> clazz){
		String name = getComposeName(clazz);
		Class<?> superClass = clazz.getSuperclass();
		while( superClass != null && !superClass.equals(Object.class) && 
				clazz.getPackage().equals(superClass.getPackage()) ){
			name = getComposeName(superClass) + "\u25C1\u2500" + name;
			superClass = superClass.getSuperclass();
		}
		return name;
	}

	private static String getComposeName(Class<?> clazz){
//		String name = clazz.getSimpleName();
//		Class<?> enclosingCass = clazz.getEnclosingClass();
//		while(enclosingCass != null){
//			name = enclosingCass.getSimpleName() + "." + name;
//			enclosingCass = enclosingCass.getEnclosingClass();
//		}
		Package pack = clazz.getPackage();
		if(pack == null)
			return clazz.getCanonicalName();
		return  clazz.getCanonicalName().substring(pack.getName().length() + 1);
	}
	
	private static String getTabs(Class<?> clazz){
		Class<?> enclosingClass = clazz.getEnclosingClass();
		int level = 0;
		while(enclosingClass != null){
			enclosingClass = enclosingClass.getEnclosingClass();
			level ++;
		}
		if(level == 0)
			return "    ";
		String tabs = "    ";
		while(level > 1){
			tabs += "    ";
			level --;
		}
		return tabs + "  \u2295\u2500";
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
	
	private static void getInterfacesAndClasses(File pack, String packageName,
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
						Class<?> clazz = Class.forName(className, false, ListClassFromAPackage.class.getClassLoader()); 
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
	
	private static void print(Class<?> clazz){
		System.out.println(getTabs(clazz) + getHierarchicalComposeName(clazz));
		for(Class<?> subclazz : clazz.getDeclaredClasses())
			print(subclazz);
	}
	
	/**
	 * @param args ignorados
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException{
	
		File root = new File("/media/DATA/Samuel/Proyectos/jspacewars/bin");
		String rootPath = root.getCanonicalPath();
		
		SortedSet<Class<?>> packageInterfacesSet = new TreeSet<Class<?>>(COMPARADOR_DE_INTERFACES);
		packageClassesCollection = new LinkedList<Class<?>>();
		SortedSet<Class<?>> listadoDeClasesOrdenado = new TreeSet<Class<?>>(COMPARADOR_DE_CLASES);
		
		for(File pack: getPackages(root)){
			String packageName = getPackageName(pack, rootPath);
			getInterfacesAndClasses(pack, packageName, packageInterfacesSet, packageClassesCollection);

			if( packageInterfacesSet.size() > 0 || packageClassesCollection.size() > 0 ){
				System.out.format("package: %s\n", packageName.length() > 0 ? packageName : "(default  package)" );
				for(Class<?> clazz:packageInterfacesSet)
					print(clazz);
				if(packageClassesCollection.size() > 0){
					listadoDeClasesOrdenado.clear();
					for(Class<?> clazz:packageClassesCollection)
						listadoDeClasesOrdenado.add(clazz);
					for(Class<?> clazz:listadoDeClasesOrdenado)
						print(clazz);
				}
			}
		}
	}
}
