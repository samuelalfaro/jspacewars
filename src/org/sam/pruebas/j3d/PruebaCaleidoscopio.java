package org.sam.pruebas.j3d;

import javax.media.j3d.Behavior;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.TransparencyAttributes;

import org.sam.j3d.Caleidoscopio;

public class PruebaCaleidoscopio extends PantallaPruebas{
	private static final long serialVersionUID = 1L;
	private final String textura = "resources/obj3d/texturas/texture030.png";
	
	protected BranchGroup createSceneGraph(Bounds bounds) {
		BranchGroup objRoot = new BranchGroup();
		objRoot.setCapability(BranchGroup.ALLOW_DETACH);
		
		Caleidoscopio caleidoscopio = new Caleidoscopio(3,textura);
		TextureAttributes texA = new TextureAttributes();
		texA.setTextureMode(TextureAttributes.COMBINE);
		texA.setCombineRgbMode(TextureAttributes.COMBINE_REPLACE);

		caleidoscopio.getAppearance().setTextureAttributes(texA);
		TransparencyAttributes traA = new TransparencyAttributes(TransparencyAttributes.BLENDED,0.0f);
		traA.setSrcBlendFunction(TransparencyAttributes.BLEND_SRC_ALPHA );
		traA.setDstBlendFunction(TransparencyAttributes.BLEND_ONE);
		caleidoscopio.getAppearance().setTransparencyAttributes(traA);
		objRoot.addChild(caleidoscopio);
		Behavior comportamiento = caleidoscopio.getComportamiento();
		comportamiento.setSchedulingBounds(bounds);
		objRoot.addChild(comportamiento);

        return objRoot;
	}
}