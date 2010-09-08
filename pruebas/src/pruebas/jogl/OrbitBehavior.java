/* 
 * OrbitBehavior.java
 * 
 * Copyright (c) 2008-2010
 * Samuel Alfaro Jiménez <samuelalfaro at gmail.com>.
 * All rights reserved.
 * 
 * This file is part of jSpaceWars.
 * 
 * jSpaceWars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jSpaceWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jSpaceWars. If not, see <http://www.gnu.org/licenses/>.
 */
package pruebas.jogl;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.media.opengl.glu.GLU;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * @author samuel
 *
 */
public class OrbitBehavior{

	private class OrbitListener implements MouseListener, MouseMotionListener, MouseWheelListener{
		
		private transient Point ptCurrentMousePosit, ptLastMousePosit;
		
		private transient final Point3d  eyePos;
		private transient final Point3d  targetPos;
		private transient final Vector3d upDir;
		private transient final Vector3d eyeDir;
		private transient final Vector3d sideDir;
		
		private transient final AxisAngle4d rotH = new AxisAngle4d();
		private transient final Matrix4d t1 = new Matrix4d(
				1.0, 0.0, 0.0, 0.0,
				0.0, 1.0, 0.0, 0.0,
				0.0, 0.0, 1.0, 0.0,
				0.0, 0.0, 0.0, 1.0
		);
		private transient final AxisAngle4d rotV = new AxisAngle4d();
		private transient final Matrix4d t2 = new Matrix4d(
				1.0, 0.0, 0.0, 0.0,
				0.0, 1.0, 0.0, 0.0,
				0.0, 0.0, 1.0, 0.0,
				0.0, 0.0, 0.0, 1.0
		);

		private transient final Matrix4d tc = new Matrix4d();

		OrbitListener(){
			eyePos =     new Point3d(0, 0, 5);
			targetPos =  new Point3d(0, 0, 0);
			upDir =      new Vector3d(0, 1, 0);
			
			eyeDir =     new Vector3d();
			eyeDir.set(targetPos);
			eyeDir.sub(eyePos);
			eyeDir.normalize();
			
			sideDir = new Vector3d();
			sideDir.cross( eyeDir, upDir );
			sideDir.normalize();
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseEntered(MouseEvent e) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseExited(MouseEvent e) {
		}

		private transient double dist;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			ptLastMousePosit = e.getPoint();

			dist = eyePos.distance(targetPos);

			eyeDir.set(targetPos);
			eyeDir.sub(eyePos);
			eyeDir.normalize();
			upDir.normalize();
			sideDir.cross( eyeDir, upDir );
			sideDir.normalize();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseReleased(MouseEvent e) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseDragged(MouseEvent e) {

			ptCurrentMousePosit = e.getPoint();

			rotH.set(upDir,      (double)(ptCurrentMousePosit.x - ptLastMousePosit.x)/200 );
			rotV.set(sideDir, (double)(ptCurrentMousePosit.y - ptLastMousePosit.y)/200 );

			t1.setRotation(rotH);
			t2.setRotation(rotV);

			tc.mul(t1, t2);

			tc.transform(eyeDir);
			eyeDir.normalize();
			eyePos.set(eyeDir);
			eyePos.negate();
			eyePos.scale(dist);
			eyePos.add(targetPos);
			
			tc.transform(upDir);
			upDir.normalize();

			ptLastMousePosit = ptCurrentMousePosit;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseMoved(MouseEvent e) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			int x = e.getWheelRotation();

			dist = eyePos.distance(targetPos);

			eyeDir.set(targetPos);
			eyeDir.sub(eyePos);
			eyeDir.normalize();

			eyePos.set(eyeDir);
			eyePos.negate();
			eyePos.scale( (1 + x/25.0f)*dist );
			eyePos.add(targetPos);
		}
		
		private void setLookAt(GLU glu){
			glu.gluLookAt(
					this.eyePos.x,    this.eyePos.y,    this.eyePos.z,
					this.targetPos.x, this.targetPos.y, this.targetPos.z,
					this.upDir.x,     this.upDir.y,     this.upDir.z
				);
		}
	}

	private final transient OrbitListener myListener;
	
	public OrbitBehavior(){
		myListener = new OrbitListener();
	}
	
	public void setEyePos(double x, double y, double z){
		myListener.eyePos.x = x;
		myListener.eyePos.y = y;
		myListener.eyePos.z = z;
	}
	
	public void setTargetPos(double x, double y, double z){
		myListener.targetPos.x = x;
		myListener.targetPos.y = y;
		myListener.targetPos.z = z;
	}
	
	public void setUpDir(double x, double y, double z){
		myListener.upDir.x = x;
		myListener.upDir.y = y;
		myListener.upDir.z = z;
		myListener.upDir.normalize();
	}
	
	public void setLookAt(GLU glu){
	    myListener.setLookAt(glu);
	}
	
	public void addMouseListeners(Component component){
		component.addMouseListener(myListener);
		component.addMouseMotionListener(myListener);
		component.addMouseWheelListener(myListener);
	}
}
