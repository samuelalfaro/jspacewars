package org.sam.jogl.particulas;

public class FactoriaDeParticulas{
	private static boolean shadersActivos = false;
	private static boolean pointSpritesEnabled = false;

	public static Particulas createParticulas(int nParticulas){
		return shadersActivos ?
			new ParticulasConShaders(nParticulas) :
			pointSpritesEnabled	?
				new ParticulasPointSprites(nParticulas) :
				new ParticulasQuads(nParticulas);
	}
	
	public static boolean isShadersActivos() {
		return shadersActivos;
	}

	public static void setShadersActivos(boolean shadersActivos) {
		FactoriaDeParticulas.shadersActivos = shadersActivos;
	}

	public static boolean isPointSpritesEnabled() {
		return pointSpritesEnabled;
	}

	public static void setPointSpritesEnabled(boolean pointSpritesEnabled) {
		FactoriaDeParticulas.pointSpritesEnabled = pointSpritesEnabled;
	}
}
