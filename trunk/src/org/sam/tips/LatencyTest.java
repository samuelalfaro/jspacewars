package org.sam.tips;

/*
 * $RCSfile$
 *
 * Copyright (c) 2005 Sun Microsystems, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 *
 * $Revision$
 * $Date$
 * $State$
 */

import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.Box;
import java.util.Enumeration;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.GraphicsConfiguration;


/**
 *
 * @author  kcr
 */
public class LatencyTest extends javax.swing.JFrame {
	private static final long serialVersionUID = 1L;
	
	SimpleUniverse u = null;
    Canvas3D canvas = null;
    View view = null;
    TransformGroup objTrans = null;
    Appearance app;
    BranchGroup timerBG = null;
    BranchGroup elapsedBG = null;
    PostBehavior postBeh = null;
    double angle = 0.0;
    static final double deltaAngle = 0.1;
    boolean isColor1 = true;
    int numTransformUpdates = 0;
    int numColorUpdates = 0;

    public BranchGroup createSceneGraph() {
	// Create the root of the branch graph
	BranchGroup objRoot = new BranchGroup();

	// Create the TransformGroup node and initialize it to the
	// identity. Enable the TRANSFORM_WRITE capability so that
	// our code can modify it at run time. Add it to
	// the root of the subgraph.
	objTrans = new TransformGroup();
	objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	objRoot.addChild(objTrans);

	// Create a simple Shape3D node; add it to the scene graph.
        app = new Appearance();
        app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
        ColoringAttributes ca = new ColoringAttributes();
        ca.setColor(0.1f, 0.1f, 0.4f);
        app.setColoringAttributes(ca);
	objTrans.addChild(new Box(0.4f, 0.4f, 0.4f, app));

        postBeh = new PostBehavior();
        postBeh.setSchedulingBounds(new BoundingSphere(new Point3d(), Double.POSITIVE_INFINITY));
        objRoot.addChild(postBeh);

        return objRoot;
    }

    private Canvas3D initScene() {
	GraphicsConfiguration config =
	    SimpleUniverse.getPreferredConfiguration();

	Canvas3D c = new MyCanvas3D(config);

	BranchGroup scene = createSceneGraph();
	u = new SimpleUniverse(c);

	ViewingPlatform viewingPlatform = u.getViewingPlatform();
	// This will move the ViewPlatform back a bit so the
	// objects in the scene can be viewed.
	viewingPlatform.setNominalViewingTransform();
        
        // Get the view and save it locally
        view = u.getViewer().getView();

	u.addBranchGraph(scene);

	return c;
    }
    
    void updateTransform() {
        System.err.println("----- update transform " + (++numTransformUpdates));
        angle += deltaAngle;
        Transform3D t3d = new Transform3D();
        t3d.rotY(angle);
        objTrans.setTransform(t3d);
    }
    
    void updateColor() {
        System.err.println("----- update color " + (++numColorUpdates));
        isColor1 = !isColor1;
        ColoringAttributes ca = new ColoringAttributes();
        if (isColor1) {
            ca.setColor(0.1f, 0.1f, 0.4f);
        } else {
            ca.setColor(0.7f, 0.1f, 0.4f);
        }
        app.setColoringAttributes(ca);
    }

    /**
     * Creates new form LatencyTest 
     */
    public LatencyTest() {
	// Initialize the GUI components
        initComponents();

	// Create the scene and add the Canvas3D to the drawing panel
	canvas = initScene();
	drawingPanel.add(canvas, java.awt.BorderLayout.CENTER);
    }
    
    class MyCanvas3D extends Canvas3D {
		private static final long serialVersionUID = 1L;
		private int frameNum = 0;

        MyCanvas3D(GraphicsConfiguration config) {
            super(config);
        }
        
        public void postSwap() {
            System.err.println("MyCanvas3D.postSwap : " + (frameNum++) + "  " + view.getFrameNumber());

            // Sleep for 1 sec
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//            }
        }
    }
    
    // Behavior that wakes up every 2 seconds and modifies the color
    class TimerBehavior extends Behavior {
        WakeupOnElapsedTime wakeup = new WakeupOnElapsedTime(2000);
        
        public void initialize() {
            wakeupOn(wakeup);
        }
        
        @SuppressWarnings("unchecked")
		public void processStimulus(Enumeration criteria) {
            System.err.println("TimerBehavior.processStimulus");
            if (updateTimerCheckBox.isSelected()) {
                updateColor();
            }
            wakeupOn(wakeup);
        }
    }
    
    // Behavior that wakes up everyframe and modifies the color
    class ElapsedFramesBehavior extends Behavior {
        WakeupOnElapsedFrames wakeup = new WakeupOnElapsedFrames(0);
        
        public void initialize() {
            wakeupOn(wakeup);
        }
        
        @SuppressWarnings("unchecked")
		public void processStimulus(Enumeration criteria) {
            System.err.println("ElapsedFramesBehavior.processStimulus");
            if (updateElapsedCheckBox.isSelected()) {
                updateColor();
            }
            wakeupOn(wakeup);
        }
    }
    
    // Behavior that wakes up upon a behavior post and modifies the transform
    class PostBehavior extends Behavior {
        WakeupOnBehaviorPost wakeup = new WakeupOnBehaviorPost(null, 0);
        
        public void initialize() {
            wakeupOn(wakeup);
        }
        
        @SuppressWarnings("unchecked")
		public void processStimulus(Enumeration criteria) {
            System.err.println("PostBehavior.processStimulus");
            if (updatePostCheckBox.isSelected()) {
                updateTransform();
            }
            wakeupOn(wakeup);
        }
    }
    
    // ----------------------------------------------------------------
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        timerButtonGroup = new javax.swing.ButtonGroup();
        elapsedButtonGroup = new javax.swing.ButtonGroup();
        cycleButtonGroup = new javax.swing.ButtonGroup();
        mainPanel = new javax.swing.JPanel();
        guiPanel = new javax.swing.JPanel();
        buttonPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        updateT3DButton = new javax.swing.JButton();
        updateColorButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        addElapsedButton = new javax.swing.JToggleButton();
        removeElapsedButton = new javax.swing.JToggleButton();
        jSeparator1 = new javax.swing.JSeparator();
        addTimerButton = new javax.swing.JToggleButton();
        removeTimerButton = new javax.swing.JToggleButton();
        jSeparator2 = new javax.swing.JSeparator();
        postButton = new javax.swing.JButton();
        updateElapsedCheckBox = new javax.swing.JCheckBox();
        updateTimerCheckBox = new javax.swing.JCheckBox();
        updatePostCheckBox = new javax.swing.JCheckBox();
        jSeparator4 = new javax.swing.JSeparator();
        jPanel3 = new javax.swing.JPanel();
        repaintButton = new javax.swing.JButton();
        selectionPanel = new javax.swing.JPanel();
        cycle0Button = new javax.swing.JRadioButton();
        cycle50Button = new javax.swing.JRadioButton();
        cycle500Button = new javax.swing.JRadioButton();
        jSeparator3 = new javax.swing.JSeparator();
        drawingPanel = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        exitMenuItem = new javax.swing.JMenuItem();

        setTitle("Latency Test");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        mainPanel.setLayout(new java.awt.BorderLayout());

        guiPanel.setLayout(new java.awt.GridBagLayout());

        guiPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        buttonPanel.setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel1.setBorder(new javax.swing.border.TitledBorder(null, "User Thread Updates", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Sans", 0, 10)));
        updateT3DButton.setText("Update Transform");
        updateT3DButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateT3DButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel1.add(updateT3DButton, gridBagConstraints);

        updateColorButton.setText("Update Color");
        updateColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateColorButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel1.add(updateColorButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        buttonPanel.add(jPanel1, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jPanel2.setBorder(new javax.swing.border.TitledBorder(null, "Behavior Updates", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Sans", 0, 10)));
        elapsedButtonGroup.add(addElapsedButton);
        addElapsedButton.setText("AddElapsed");
        addElapsedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addElapsedButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(addElapsedButton, gridBagConstraints);

        elapsedButtonGroup.add(removeElapsedButton);
        removeElapsedButton.setSelected(true);
        removeElapsedButton.setText("Remove Elapsed");
        removeElapsedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeElapsedButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(removeElapsedButton, gridBagConstraints);

        jSeparator1.setPreferredSize(new java.awt.Dimension(0, 8));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(jSeparator1, gridBagConstraints);

        timerButtonGroup.add(addTimerButton);
        addTimerButton.setText("Add Timer");
        addTimerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTimerButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(addTimerButton, gridBagConstraints);

        timerButtonGroup.add(removeTimerButton);
        removeTimerButton.setSelected(true);
        removeTimerButton.setText("Remove Timer");
        removeTimerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeTimerButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(removeTimerButton, gridBagConstraints);

        jSeparator2.setPreferredSize(new java.awt.Dimension(0, 8));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(jSeparator2, gridBagConstraints);

        postButton.setText("Post Message");
        postButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                postButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(postButton, gridBagConstraints);

        updateElapsedCheckBox.setSelected(true);
        updateElapsedCheckBox.setText("Enable Update");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(updateElapsedCheckBox, gridBagConstraints);

        updateTimerCheckBox.setSelected(true);
        updateTimerCheckBox.setText("Enable Update");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(updateTimerCheckBox, gridBagConstraints);

        updatePostCheckBox.setSelected(true);
        updatePostCheckBox.setText("Enable Update");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(updatePostCheckBox, gridBagConstraints);

        jSeparator4.setPreferredSize(new java.awt.Dimension(4, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        jPanel2.add(jSeparator4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        buttonPanel.add(jPanel2, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jPanel3.setBorder(new javax.swing.border.TitledBorder(null, "View Updates", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Sans", 0, 10)));
        repaintButton.setText("Repaint");
        repaintButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                repaintButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel3.add(repaintButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        buttonPanel.add(jPanel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        guiPanel.add(buttonPanel, gridBagConstraints);

        selectionPanel.setLayout(new java.awt.GridBagLayout());

        selectionPanel.setBorder(new javax.swing.border.TitledBorder(null, "Minimum Cycle Time", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Sans", 0, 10)));
        cycleButtonGroup.add(cycle0Button);
        cycle0Button.setSelected(true);
        cycle0Button.setText("0");
        cycle0Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cycle0ButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        selectionPanel.add(cycle0Button, gridBagConstraints);

        cycleButtonGroup.add(cycle50Button);
        cycle50Button.setText("50");
        cycle50Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cycle50ButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        selectionPanel.add(cycle50Button, gridBagConstraints);

        cycleButtonGroup.add(cycle500Button);
        cycle500Button.setText("500");
        cycle500Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cycle500ButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        selectionPanel.add(cycle500Button, gridBagConstraints);

        jSeparator3.setPreferredSize(new java.awt.Dimension(125, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        selectionPanel.add(jSeparator3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        guiPanel.add(selectionPanel, gridBagConstraints);

        mainPanel.add(guiPanel, java.awt.BorderLayout.NORTH);

        drawingPanel.setLayout(new java.awt.BorderLayout());

        drawingPanel.setPreferredSize(new java.awt.Dimension(500, 500));
        mainPanel.add(drawingPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        fileMenu.setText("File");
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(exitMenuItem);

        jMenuBar1.add(fileMenu);

        setJMenuBar(jMenuBar1);

        pack();
    }
    // </editor-fold>//GEN-END:initComponents

    private void cycle0ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cycle0ButtonActionPerformed
        view.setMinimumFrameCycleTime(0);
    }//GEN-LAST:event_cycle0ButtonActionPerformed

    private void cycle500ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cycle500ButtonActionPerformed
        view.setMinimumFrameCycleTime(500);
    }//GEN-LAST:event_cycle500ButtonActionPerformed

    private void cycle50ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cycle50ButtonActionPerformed
        view.setMinimumFrameCycleTime(50);
    }//GEN-LAST:event_cycle50ButtonActionPerformed

    private void removeElapsedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeElapsedButtonActionPerformed
        if (elapsedBG != null) {
            System.err.println("removing continuous elapsed frames behavior");
            elapsedBG.detach();
            elapsedBG = null;
        }
    }//GEN-LAST:event_removeElapsedButtonActionPerformed

    private void removeTimerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeTimerButtonActionPerformed
        if (timerBG != null) {
            System.err.println("removing timer behavior");
            timerBG.detach();
            timerBG = null;
        }
    }//GEN-LAST:event_removeTimerButtonActionPerformed

    private void addElapsedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addElapsedButtonActionPerformed
        if (elapsedBG == null) {
            System.err.println("adding new continuous elapsed frames behavior");
            elapsedBG = new BranchGroup();
            elapsedBG.setCapability(BranchGroup.ALLOW_DETACH);
            ElapsedFramesBehavior elapsedBeh = new ElapsedFramesBehavior();
            elapsedBeh.setSchedulingBounds(new BoundingSphere(new Point3d(), Double.POSITIVE_INFINITY));
            elapsedBG.addChild(elapsedBeh);
            u.addBranchGraph(elapsedBG);
        }
    }//GEN-LAST:event_addElapsedButtonActionPerformed

    private void addTimerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTimerButtonActionPerformed
        if (timerBG == null) {
            System.err.println("adding new timer behavior");
            timerBG = new BranchGroup();
            timerBG.setCapability(BranchGroup.ALLOW_DETACH);
            TimerBehavior timerBeh = new TimerBehavior();
            timerBeh.setSchedulingBounds(new BoundingSphere(new Point3d(), Double.POSITIVE_INFINITY));
            timerBG.addChild(timerBeh);
            u.addBranchGraph(timerBG);
        }
    }//GEN-LAST:event_addTimerButtonActionPerformed

    private void repaintButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_repaintButtonActionPerformed
        view.repaint();
    }//GEN-LAST:event_repaintButtonActionPerformed

    private void postButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_postButtonActionPerformed
        System.err.println("calling behavior post");
        postBeh.postId(1);
    }//GEN-LAST:event_postButtonActionPerformed

    private void updateColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateColorButtonActionPerformed
        updateColor();
    }//GEN-LAST:event_updateColorButtonActionPerformed

    private void updateT3DButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateT3DButtonActionPerformed
        updateTransform();
    }//GEN-LAST:event_updateT3DButtonActionPerformed

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new LatencyTest().setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton addElapsedButton;
    private javax.swing.JToggleButton addTimerButton;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JRadioButton cycle0Button;
    private javax.swing.JRadioButton cycle500Button;
    private javax.swing.JRadioButton cycle50Button;
    private javax.swing.ButtonGroup cycleButtonGroup;
    private javax.swing.JPanel drawingPanel;
    private javax.swing.ButtonGroup elapsedButtonGroup;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JPanel guiPanel;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton postButton;
    private javax.swing.JToggleButton removeElapsedButton;
    private javax.swing.JToggleButton removeTimerButton;
    private javax.swing.JButton repaintButton;
    private javax.swing.JPanel selectionPanel;
    private javax.swing.ButtonGroup timerButtonGroup;
    private javax.swing.JButton updateColorButton;
    private javax.swing.JCheckBox updateElapsedCheckBox;
    private javax.swing.JCheckBox updatePostCheckBox;
    private javax.swing.JButton updateT3DButton;
    private javax.swing.JCheckBox updateTimerCheckBox;
    // End of variables declaration//GEN-END:variables

}