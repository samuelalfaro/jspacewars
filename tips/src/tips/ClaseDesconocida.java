package tips;

import java.lang.reflect.*;

public class ClaseDesconocida{

	public int entero = 12;
	public String cadena = "hola mundo";
	public float f = 5f; 
	public double real = 3f;
	
	public boolean metodo1(){
		System.out.println("Metodo1 de la clase desconocida");
		return false;
	}
	
	public void metodo2(Object arg1[], int arg2, float arg3[], Integer arg4[][]){
		System.out.println("Metodo2 de la clase desconocida");
		if(arg1== null || arg2 == 0 || arg3 == null || arg4 == null)
			return;
	}
	
	public static void main(String[] args) throws Exception {
		Object obj = new ClaseDesconocida();
		Field[] campos = ClaseDesconocida.class.getFields();
		Field field = null;
		
		for(int i=0; i<campos.length; i++) {
			field = campos[i];
			System.out.println("la variable '"+field.getName()+"'\n\tes de tipo: "+field.getType().getName());
			if(field.getType().getName().equals("int")){
				int valor = field.getInt(obj);
				System.out.println("\ty vale: "+valor);
			}else if (field.getType().getName().equals("float")){
				System.out.println("\ty vale: "+field.getFloat(obj));
			}else if (field.getType().getName().equals("double")){
				System.out.println("\ty vale: "+field.getDouble(obj));
			}else{
				System.out.println("\t'"+field.getName()+"' vale: "+field.get(obj));
			}
		}
		
		Method[] metodos = ClaseDesconocida.class.getMethods();
		Method metodo = null;
		System.out.println("\nTiene los metodos:");
		for(int i=0; i<metodos.length; i++) {
			metodo = metodos[i];
			System.out.print("\t"+metodo.getName()+"(");
			Class<?> parametros[] = metodo.getParameterTypes();
			Class<?> parametro = null;
			for (int n=0; n<parametros.length; n++){
				parametro = parametros[n];
				System.out.print(" "+parametro.getName()+" ");
			}
			System.out.println("): "+metodo.getReturnType().getName());
		}
		
		try{
			int i = 0;
			do{
				metodo = metodos[i];
				i++;
			}while(!metodo.getName().equals("metodo2"));
			metodo.invoke(obj, new Object[]{new Object[]{},3,new float[]{}, new Integer[][]{null,null}});
		}catch(NullPointerException laClaseNoTieneMetodos){
		}catch(ArrayIndexOutOfBoundsException noSeHaEncontradoElMetodo){
		}
	}
}