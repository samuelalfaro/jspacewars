package org.sam.j3d.particulas;

public class FactoriaDeParticulas{
	private static boolean shadersActivos = false;

	public static Particulas createParticulas(int nParticulas){
		return shadersActivos
			? new ParticulasConShaders(nParticulas)
			: new ParticulasSinShaders(nParticulas);
	}
	
	public static boolean isShadersActivos() {
		return shadersActivos;
	}

	public static void setShadersActivos(boolean shadersActivos) {
		FactoriaDeParticulas.shadersActivos = shadersActivos;
	}
}
