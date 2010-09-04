package org.sam.interpoladores;

public final class Keys{

	private Keys(){}
	
    public static final int HOMOGENEAS = 0;

    public static final int PROPORCIONALES = 1;

	static final double[] generateKeys(int tam){
		double keys[] = new double[tam];
		double k = 0.0, iK = 1.0/(tam-1);
		for (int i=0; i<tam; i++, k+=iK)
			keys[i] = k;
		return keys;
	}

	static final void scale(double[] keys, double scale) {
		for (int i = 0; i < keys.length; i++)
			keys[i] *= scale;
	}

	static final void translate(double[] keys, double translation) {
		for (int i = 0; i < keys.length; i++)
			keys[i] += translation;
	}

	static final void normalize(double[] keys){
		double scaleKeys = 1/(keys[keys.length-1]-keys[0]);
		for (int i = 1; i < keys.length; i++){
			keys[i]-=keys[0];
			keys[i]*=scaleKeys;
		}
	}

	static double longitudCurva(Funcion.Double f[], int pasos){
		double ant[] = new double[f.length];
		for(int i=0; i< f.length; i++)
			ant[i] = f[i].f(0);
		
		double len = 0;
		for(int p =1; p<= pasos; p++){
			double t = p/pasos;
			double len_seg  = 0.0;
			for(int i=0; i< f.length; i++){
				double ft = f[i].f(t);
				len_seg += Math.pow(ant[i] - ft, 2);
				ant[i] = ft;
			}
			len += Math.sqrt(len_seg);
		}
		return len;
	}
	
	static final double[] generateKeys(Funcion.Double[][] funciones){
		Funcion.Double f[] = new Funcion.Double[funciones.length];
		int len = funciones[0].length;
		double keys[] = new double[len+1];
		keys[0] = 0.0;
		for (int i=0; i< len; i++){
			for(int j=0; j < funciones.length; j++)
				f[j] = funciones[j][i];
			keys[i+1] = keys[i] + longitudCurva(f, 512);
		}
		normalize(keys);
		return keys;
	}

	static final void ajustarFunciones(double[] keys, Funcion.Double[][] funciones) {
		assert (keys.length == funciones[0].length+1);
		for (int i = 0; i < funciones[0].length; i++) {
			double s = 1 / (keys[i + 1] - keys[i]);
			double t = -keys[i];
			for (int j = 0; j < funciones.length; j++) {
				funciones[j][i].scaleIn(s);
				funciones[j][i].translateIn(t);
			}
		}
	}

	static final float[] toFloat(double keys[]){
		float[] fKeys = new float[keys.length];
		for(int i= 0; i< keys.length; i++)
			fKeys[i] = (float)keys[i];
		return fKeys;
	}

	static final boolean estaOrdenado(double keys[]){
		for(int i = 0, len = keys.length -1; i < len; )
			if(keys[i] > keys[++i])
				return false;
		return true;
	}
	
	static final boolean estaOrdenado(float keys[]){
		for(int i = 0, len = keys.length -1; i < len; )
			if(keys[i] > keys[++i])
				return false;
		return true;
	}
	
	static final boolean estaOrdenado(int keys[]){
		for(int i = 0, len = keys.length -1; i < len; )
			if(keys[i] > keys[++i])
				return false;
		return true;
	}
	
	/**
	 * Realiza la busqueda dicotomica en un vector.
	 * @param key Elemento buscado.
	 * @param keys Vector donde buscar el elemento.
	 * @return 
	 * <ul>
	 * <li> El índice del mayor elemento del vector que es menor o igual al elemento buscado.</li>
	 * <li><code>-1</code> en caso de que el vector no contenga ningun elemento menor que el buscado.</li>
	 * </ul>
	 */
	static final int findIndexKey(double key, double keys[]){
		int izq = 0;
		int der = keys.length - 1;
		int med = der >> 1;

		if(key < keys[0])
			return -1;
		if(key == keys[0])
			return 0;
		if(key >= keys[der])
			return der;
		// busqueda dicotomica
		while(izq < der){
			if(key == keys[med])
				return med;
			if(key < keys[med])
				der = med;
			else
				izq = med+1;
			med = (izq + der) >> 1;
		}
		return med-1;
	}

	/**
	 * Realiza la busqueda dicotomica en un vector.
	 * @param key Elemento buscado.
	 * @param keys Vector donde buscar el elemento.
	 * @return 
	 * <ul>
	 * <li> El índice del mayor elemento del vector que es menor o igual al elemento buscado.</li>
	 * <li><code>-1</code> en caso de que el vector no contenga ningun elemento menor que el buscado.</li>
	 * </ul>
	 */
	static final int findIndexKey(float key, float keys[]){
		int izq = 0;
		int der = keys.length - 1;
		int med = der >> 1;

		if(key < keys[0])
			return -1;
		if(key == keys[0])
			return 0;
		if(key >= keys[der])
			return der;
		// busqueda dicotomica
		while(izq < der){
			if(key == keys[med])
				return med;
			if(key < keys[med])
				der = med;
			else
				izq = med+1;
			med = (izq + der) >> 1;
		}
		return med-1;
	}
	
	/**
	 * Realiza la busqueda dicotomica en un vector.
	 * @param key Elemento buscado.
	 * @param keys Vector donde buscar el elemento.
	 * @return 
	 * <ul>
	 * <li> El índice del mayor elemento del vector que es menor o igual al elemento buscado.</li>
	 * <li><code>-1</code> en caso de que el vector no contenga ningun elemento menor que el buscado.</li>
	 * </ul>
	 */
	static final int findIndexKey(int key, int keys[]){
		int izq = 0;
		int der = keys.length - 1;
		int med = der >> 1;

		if(key < keys[0])
			return -1;
		if(key == keys[0])
			return 0;
		if(key >= keys[der])
			return der;
		// busqueda dicotomica
		while(izq < der){
			if(key == keys[med])
				return med;
			if(key < keys[med])
				der = med;
			else
				izq = med+1;
			med = (izq + der) >> 1;
		}
		return med-1;
	}
}
