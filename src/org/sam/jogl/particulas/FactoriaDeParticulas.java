package org.sam.jogl.particulas;

public class FactoriaDeParticulas{

	private static boolean pointSpritesEnabled = false;
	private static boolean shadersEnabled = false;
	private static boolean optimizedFor2D = false;

	public static Particulas createParticulas(int nParticulas){
		return optimizedFor2D	?
				new Estrellas(nParticulas) :
				new ParticulasQuads(nParticulas);
	}
	
	public static boolean isPointSpritesEnabled() {
		return pointSpritesEnabled;
	}

	/**
	 * @param pointSpritesEnabled ignorado operacion no soportada 
	 */
	public static void setPointSpritesEnabled(boolean pointSpritesEnabled) {
		throw new UnsupportedOperationException();
		//TODO implementar o eliminar completamente
		//FactoriaDeParticulas.pointSpritesEnabled = pointSpritesEnabled;
	}

	public static boolean isShadersEnabled() {
		return shadersEnabled;
	}

	/**
	 * @param shadersEnabled  ignorado operacion no soportada 
	 */
	public static void setShadersEnabled(boolean shadersEnabled) {
		throw new UnsupportedOperationException();
		//TODO implementar o eliminar completamente
		//FactoriaDeParticulas.shadersEnabled = shadersEnabled;
	}
	
	public static boolean isOptimizedFor2D() {
		return optimizedFor2D;
	}

	public static void setOptimizedFor2D(boolean optimizedFor2D) {
		FactoriaDeParticulas.optimizedFor2D = optimizedFor2D;
	}
}
