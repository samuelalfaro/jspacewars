/**
 * 
 */
package org.sam.jogl.fondos;

import org.sam.elementos.Modificable;
import org.sam.jogl.Dibujable;

/**
 * @author samuel
 *
 */
public interface Fondo extends Dibujable, Modificable{

	public void setProporcionesPantalla(float proporcionesPantalla);

}