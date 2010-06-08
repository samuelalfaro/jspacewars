package pruebas.jogl;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.media.opengl.glu.GLU;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * @author samuel
 *
 */
public class OrbitBehavior{

	private class OrbitListener implements MouseListener, MouseMotionListener, MouseWheelListener{
		
		private transient Point ptCurrentMousePosit, ptLastMousePosit;
		
		private transient final Vector3f eyeDir =     new Vector3f();
		private transient final Vector3f crossEyeUp = new Vector3f();
		
		private transient final AxisAngle4f rotH = new AxisAngle4f();
		private transient final Matrix4f t1 = new Matrix4f(
				1.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 1.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 1.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 1.0f
		);
		private transient final AxisAngle4f rotV = new AxisAngle4f();
		private transient final Matrix4f t2 = new Matrix4f(
				1.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 1.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 1.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 1.0f
		);

		private transient final Matrix4f tc = new Matrix4f();

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

		private transient float dist;

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
			crossEyeUp.cross( eyeDir, upDir );
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

			rotH.set(upDir,      ((float)(ptCurrentMousePosit.x - ptLastMousePosit.x))/200);
			rotV.set(crossEyeUp, ((float)(ptCurrentMousePosit.y - ptLastMousePosit.y))/200);

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
	}

	private transient final Point3f  eyePos =     new Point3f(0, 0, 5);
	private transient final Point3f  targetPos =  new Point3f(0, 0, 0);
	private transient final Vector3f upDir =      new Vector3f(0, 1, 0);
	
	private transient OrbitListener myListener = null;
	
	public void setEyePos(float x, float y, float z){
		eyePos.x = x;
		eyePos.y = y;
		eyePos.z = z;
	}
	
	public void setTargetPos(float x, float y, float z){
		targetPos.x = x;
		targetPos.y = y;
		targetPos.z = z;
	}
	
	public void setUpDir(float x, float y, float z){
		upDir.x = x;
		upDir.y = y;
		upDir.z = z;
		upDir.normalize();
	}
	
	public void setLookAt(GLU glu){
		glu.gluLookAt(
				this.eyePos.x,    this.eyePos.y,    this.eyePos.z,
				this.targetPos.x, this.targetPos.y, this.targetPos.z,
				this.upDir.x,     this.upDir.y,     this.upDir.z
			);
	}
	
	public void addMouseListeners(Component component){
		if(myListener == null)
			myListener = new OrbitListener();
		component.addMouseListener(myListener);
		component.addMouseMotionListener(myListener);
		component.addMouseWheelListener(myListener);
	}
}
