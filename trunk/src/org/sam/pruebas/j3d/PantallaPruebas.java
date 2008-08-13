 package org.sam.pruebas.j3d;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;

import javax.media.j3d.*;
import javax.swing.JComponent;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

public abstract class PantallaPruebas extends JComponent{
	private static final long serialVersionUID = 1L;
	private static final int BOUNDSIZE = 1000;
	private Canvas3D canvas3D;
	
	public PantallaPruebas(){
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		canvas3D = new Canvas3D(config);

		SimpleUniverse u = new SimpleUniverse(canvas3D);
		u.getViewer().getView().setTransparencySortingPolicy(View.TRANSPARENCY_SORT_GEOMETRY);
		u.getViewer().getView().setBackClipDistance(10d);
		u.getViewer().getView().setFrontClipDistance(0.010d);

		ViewingPlatform viewingPlatform = u.getViewingPlatform();
		viewingPlatform.setNominalViewingTransform();
		
		TransformGroup steerTG = viewingPlatform.getViewPlatformTransform();
		Transform3D t3d = new Transform3D();
		steerTG.getTransform(t3d);

		/*
		Point3d  CAMERA_EYE = new Point3d(0,1,3);
		Point3d  CAMERA_CEN = new Point3d(0,0,0);
		Vector3d CAMERA_UP = new Vector3d(0,1,0);
		/*/
		Point3d  CAMERA_EYE = new Point3d(0,0,24);
		Point3d  CAMERA_CEN = new Point3d(0,0,0);
		Vector3d CAMERA_UP = new Vector3d(0,1,0);
		//*/
		
		t3d.lookAt( CAMERA_EYE, CAMERA_CEN, CAMERA_UP );
		t3d.invert();

		steerTG.setTransform(t3d);

		Bounds bounds = new BoundingSphere(new Point3d(0,0,0), BOUNDSIZE); 
		
		OrbitBehavior orbit = new OrbitBehavior(canvas3D, OrbitBehavior.REVERSE_ALL);
		orbit.setSchedulingBounds(bounds);
		viewingPlatform.setViewPlatformBehavior(orbit);

		BranchGroup scene = createSceneGraph(bounds);
		u.addBranchGraph(scene);
		
		this.setLayout(new BorderLayout());
		
		this.add(canvas3D, BorderLayout.CENTER);
		
		/* Pruebas con internals frames
 		JInternalFrame iFrame = new JInternalFrame("Prueba");
		iFrame.setBounds(100,100,200,200);
		iFrame.setResizable(true);
		iFrame.setIconifiable(true);
		iFrame.setMaximizable(true);
		iFrame.setClosable(true);
		iFrame.setVisible(true);
		iFrame.add(canvas3D);
		//iFrame.set
		this.add(iFrame);
		
		JDialog iDialog = new JDialog(); 
		JPanel iPanel = new JPanel(){
			public void paintComponent(Graphics g){
				Graphics2D g2 = (Graphics2D)g;
				g2.setPaint(new GradientPaint(0,0,new Color(0,0,255,128),0,this.getHeight()/2, Color.YELLOW, true));
				g2.fillRect(0,0,this.getWidth(),this.getHeight());
			}
		};
		iPanel.setOpaque(false);
		iDialog.setContentPane(iPanel);
		iDialog.setBounds(300,100,200,200);
		awtFrame.setResizable(true);
		awtFrame.setIconifiable(true);
		awtFrame.setMaximizable(true);
		awtFrame.setClosable(true);
		iDialog.setVisible(true);
		iFrame.add(new Button("toca pelotas"));
		this.add(iDialog);
		*/
	}

	public void setVisible(boolean flag){
		super.setVisible(flag);
//		if(flag)
//			canvas3D.startRenderer();
//		else
//			canvas3D.stopRenderer();
		canvas3D.setVisible(flag);
	}
	
	protected abstract BranchGroup createSceneGraph(Bounds bounds);
}