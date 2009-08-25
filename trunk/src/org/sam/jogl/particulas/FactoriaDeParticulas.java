package org.sam.jogl.particulas;

public class FactoriaDeParticulas{

	private static boolean pointSpritesEnabled = false;
	private static boolean shadersEnabled = false;
	private static boolean optimizedForStars = false;

	public static Particulas createParticulas(int nParticulas){
		return optimizedForStars	?
				new Estrellas(nParticulas) :
				new ParticulasQuads(nParticulas);
	}
	
	public static boolean isPointSpritesEnabled() {
		return pointSpritesEnabled;
	}

	public static void setPointSpritesEnabled(boolean pointSpritesEnabled) {
		throw new UnsupportedOperationException();
//		FactoriaDeParticulas.pointSpritesEnabled = pointSpritesEnabled;
	}

	public static boolean isShadersEnabled() {
		return shadersEnabled;
	}

	public static void setShadersEnabled(boolean shadersEnabled) {
		throw new UnsupportedOperationException();
//		FactoriaDeParticulas.shadersEnabled = shadersEnabled;
	}
	
	public static boolean isOptimizedForStars() {
		return optimizedForStars;
	}

	public static void setOptimizedForStars(boolean optimizedForStars) {
		FactoriaDeParticulas.optimizedForStars = optimizedForStars;
	}
}
