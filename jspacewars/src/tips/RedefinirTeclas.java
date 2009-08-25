/**
 * 
 */
package tips;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

/**
 * @author samuel
 *
 */
public class RedefinirTeclas {

	@SuppressWarnings("serial")
	public static class LectorTecla extends JButton implements KeyListener{
		public LectorTecla(Color background){
			super("            ");
			this.setBackground(new Color(background.getRed(), background.getGreen(),  background.getBlue(), 255));
			this.setFocusPainted(true);
			this.setRequestFocusEnabled(true);
			this.setFocusable(true);
			this.addKeyListener(this);
		}

		/* (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyPressed(KeyEvent e) {
			System.out.println( (Character.isDefined(e.getKeyChar())?e.getKeyChar():'?') +" 0x"+Integer.toHexString(e.getKeyCode()).toUpperCase()+" : "+e.getKeyCode());
			this.setText(KeyEvent.getKeyText(e.getKeyCode()));
			this.transferFocus();
		}

		/* (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyReleased(KeyEvent e) {

		}

		/* (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyTyped(KeyEvent e) {
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("Redefine Teclas");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel(new GridLayout(0,2));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.add(new JLabel("Acelera"));	panel.add(new LectorTecla(frame.getBackground()));
		panel.add(new JLabel("Frena"));		panel.add(new LectorTecla(frame.getBackground()));
		panel.add(new JLabel("Subir"));		panel.add(new LectorTecla(frame.getBackground()));
		panel.add(new JLabel("Bajar"));		panel.add(new LectorTecla(frame.getBackground()));
		panel.add(new JLabel("Disparo"));	panel.add(new LectorTecla(frame.getBackground()));
		panel.add(new JLabel("Bomba"));		panel.add(new LectorTecla(frame.getBackground()));
		panel.add(new JLabel("Upgrade"));	panel.add(new LectorTecla(frame.getBackground()));
		
		frame.setMinimumSize(new Dimension(250, 0));

		frame.setContentPane(panel);
		frame.pack();
		frame.setVisible(true);
	}

}
