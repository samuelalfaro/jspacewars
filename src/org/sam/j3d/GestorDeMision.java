package org.sam.j3d;

import org.sam.util.Modificador;


/**
 * @author Samuel
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class GestorDeMision  implements Modificador{
	private GestorDeElementos gestorDeEnemigos;

	public GestorDeMision(){
		gestorDeEnemigos = new GestorDeElementos();
	}

	private float time;
	private int cont;
	
	public void iniciar(){
		time = 0.0f;
		cont = 0;
	}
	
	public boolean modificar(float steep) {
		time += steep;
		if(time < 1.0f)
			return false;
		time-= 1.0f;
		
		//System.out.println("añadiendo nave");
		Elemento nuevaNave;
		if(cont == 25){
			nuevaNave = CacheDeElementos.newObject(0x20002);
			nuevaNave.setPosicion(25.5f,3.0f,0.0f);
			nuevaNave.aplicarTransformacion();
			gestorDeEnemigos.add(nuevaNave);
		}else if(cont == 50){
			nuevaNave = CacheDeElementos.newObject(0x30001);
			nuevaNave.setPosicion(29.5f,0.0f,0.0f);
			nuevaNave.aplicarTransformacion();
			gestorDeEnemigos.add(nuevaNave);
			cont = 0;
		}
//		switch(cont%4){
//		case 0:
		if(cont % 4 ==0){
			nuevaNave = CacheDeElementos.newObject(0x10001);
			nuevaNave.setPosicion(29.5f,-2.0f,0.0f);
			nuevaNave.aplicarTransformacion();
			gestorDeEnemigos.add(nuevaNave);
//			break;
//		case 1:
			nuevaNave = CacheDeElementos.newObject(0x10002);
			nuevaNave.setPosicion(29.5f,0.0f,0.0f);
			nuevaNave.aplicarTransformacion();
			gestorDeEnemigos.add(nuevaNave);
//			break;
//		case 2:
			nuevaNave = CacheDeElementos.newObject(0x10002);
			nuevaNave.setPosicion(29.5f,5.0f,0.0f);
			nuevaNave.aplicarTransformacion();
			gestorDeEnemigos.add(nuevaNave);
//			break;
//		default:
			nuevaNave = CacheDeElementos.newObject(0x10001);
			nuevaNave.setPosicion(29.5f,7.0f,0.0f);
			nuevaNave.aplicarTransformacion();
			gestorDeEnemigos.add(nuevaNave);
		}
//		nuevaNave.aplicarTransformacion();
//		gestorDeEnemigos.add(nuevaNave);
		cont++;
		return true;
	}
	
	public GestorDeElementos getEnemigos(){
		return gestorDeEnemigos;
	}
}
