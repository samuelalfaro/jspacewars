package pruebas.jogl;
import java.awt.Point;
import java.awt.event.*;

import javax.vecmath.*;

/**
 * @author samuel
 *
 */
public class OrbitBehavior implements MouseListener, MouseMotionListener{

	private transient Point ptCurrentMousePosit, ptLastMousePosit;
	
	public transient final Point3f  eyePos =     new Point3f(0, 0, 5);
	public transient final Point3f  targetPos =  new Point3f(0, 0, 0);
	public transient final Vector3f upDir =      new Vector3f(0, 1, 0);
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
	
	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	private transient float dist;
	
	public void mousePressed(MouseEvent e) {
		ptLastMousePosit = e.getPoint();
		
		dist = eyePos.distance(targetPos);
		
		eyeDir.set(targetPos);
		eyeDir.sub(eyePos);
		eyeDir.normalize();
		upDir.normalize();
		crossEyeUp.cross( eyeDir, upDir );
	}

	public void mouseReleased(MouseEvent e) {
	}
	
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

	public void mouseMoved(MouseEvent e) {
	}
}
