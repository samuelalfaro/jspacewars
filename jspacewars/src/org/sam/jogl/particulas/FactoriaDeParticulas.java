package org.sam.jogl.particulas;

public class FactoriaDeParticulas{

	private static boolean pointSpritesEnabled = false;
	@SuppressWarnings("unused")
	private static boolean shadersEnabled = false;

	public static Particulas createParticulas(int nParticulas){
//		return shadersEnabled ?
//			new ParticulasConShaders(nParticulas) :
		return pointSpritesEnabled	?
//				new ParticulasPointSprites(nParticulas) :
				new Estrellas(nParticulas) :
				new ParticulasQuads(nParticulas);
	}
	
	public static boolean isPointSpritesEnabled() {
		return pointSpritesEnabled;
	}

	public static void setPointSpritesEnabled(boolean pointSpritesEnabled) {
		FactoriaDeParticulas.pointSpritesEnabled = pointSpritesEnabled;
	}

	public static boolean isShadersEnabled() {
		return false;
//		return shadersEnabled;
	}

	public static void setShadersEnabled(boolean shadersEnabled) {
		throw new UnsupportedOperationException();
//		FactoriaDeParticulas.shadersEnabled = shadersEnabled;
	}
}
