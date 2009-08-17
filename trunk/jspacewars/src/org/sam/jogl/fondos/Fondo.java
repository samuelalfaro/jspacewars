/**
 * 
 */
package org.sam.jogl.fondos;

import org.sam.jogl.Dibujable;
import org.sam.util.Modificable;

/**
 * @author samuel
 *
 */
public interface Fondo extends Dibujable, Modificable{

	public void setProporcionesPantalla(float proporcionesPantalla);

}