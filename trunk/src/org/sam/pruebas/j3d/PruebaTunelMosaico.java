package org.sam.pruebas.j3d;

import javax.media.j3d.Behavior;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.TextureAttributes;

import org.sam.interpoladores.*;
import org.sam.j3d.Tools;
import org.sam.j3d.Tunel;
import org.sam.j3d.TunelMosaico;

public class PruebaTunelMosaico extends PantallaPruebas{
	
	private static final long serialVersionUID = 1L;
	//private final String textura = "resources/obj3d/formas/skycube/textura.jpg";
	//private final String textura = "resources/obj3d/texturas/tunel.jpg";
	//private final String textura = "resources/obj3d/texturas/smoke_particle2.jpg";
	private final String textura = "resources/obj3d/texturas/texture030.jpg";
	
	@SuppressWarnings("unchecked")
	protected BranchGroup createSceneGraph(Bounds bounds) {
		BranchGroup objRoot = new BranchGroup();
		objRoot.setCapability(BranchGroup.ALLOW_DETACH);
		
		Getter.Float<float[]> interpolador = GettersFactory.Float.create(
				new float[] {0.01f,0.25f,0.5f,0.75f,0.99f},
				new float[][]{
						{0.0f, 0.0f},
						{2.0f, -2.0f},
						{0.0f, 0.0f},
						{-2.0f, 2.0f},
						{0.0f, 0.0f}
				},MetodoDeInterpolacion.Predefinido.CATMULL_ROM_SPLINE);
		
		Tunel tunel = new TunelMosaico(6, 20, interpolador, textura, 0.0f, 10.0f, 0.0f, 0.2f);
		
		TextureAttributes texA = new TextureAttributes();
		texA.setTextureMode(TextureAttributes.COMBINE);
		texA.setCombineRgbMode(TextureAttributes.COMBINE_REPLACE);
		tunel.getAppearance().setTextureAttributes(texA);
		
		Tools.setBlendMode(	tunel, Tools.MODO_NORMAL ,0.0f);
		
		objRoot.addChild(tunel);
		Behavior comportamiento = tunel.getBehavior();
		comportamiento.setSchedulingBounds(bounds);
		objRoot.addChild(comportamiento);

        return objRoot;
	}
}