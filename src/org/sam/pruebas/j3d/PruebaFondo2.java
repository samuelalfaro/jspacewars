package org.sam.pruebas.j3d;

import javax.media.j3d.*;

import org.sam.j3d.*;

public class PruebaFondo2 extends PantallaPruebas{
	private static final long serialVersionUID = 1L;
	//private static final int BOUNDSIZE = 10;
	
	protected BranchGroup createSceneGraph(Bounds bounds) {
		BranchGroup objRoot = new BranchGroup();
		objRoot.setCapability(BranchGroup.ALLOW_DETACH);

		Gestor gestor = new Gestor();
		ActualizadorBehavior actualizador = new ActualizadorBehavior(gestor,1, false);
		actualizador.setSchedulingBounds(bounds);
		objRoot.addChild(actualizador);
		
		Fondo fondo = new Fondo("resources/obj3d/texturas/cielo1.jpg");
		Background bg = fondo.getFondo(); 
		bg.setApplicationBounds(bounds);
		objRoot.addChild(bg);
		
		gestor.add(fondo.getModificador());
				
		TransformGroup trans = new TransformGroup();
		trans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		trans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		trans.setCapability(Node.ENABLE_PICK_REPORTING);
		
		trans.addChild(Tools.loadScene("file:./resources/obj3d/formas/nave01/forma.obj").getSceneGroup());
		
		objRoot.addChild(trans);

        objRoot.compile();
        return objRoot;
	}
}