package org.sam.pruebas.interpoladores;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.sam.interpoladores.GettersFactory;
import org.sam.interpoladores.Getter;
import org.sam.interpoladores.MetodoDeInterpolacion;

public class PruebaInterpoladoresPolinomicos{

	static class Resultados extends JPanel implements MouseListener{
		private static final long serialVersionUID = 1L;
		final int 	N_PUNTOS = 20;
		final float keys[];
		final float values[][];
		
		Getter.Float<float[]> interpolador;

		Resultados(){
			keys = new float[N_PUNTOS];
			values = new float[N_PUNTOS][4];

			rellenar();
			
			this.addMouseListener(this);
		}

		private void rellenar(){
			for (int i = 0; i < N_PUNTOS; i++){
				//keys[i] = Math.random();
				keys[i] = 1.0f*i/(N_PUNTOS-1);
				values[i][0] = (float)((Math.random() - 0.5)*getHeight())/2;
				values[i][1] = (float)((Math.random() - 0.5)*getHeight())/2;
				values[i][2] = (float)((Math.random() - 0.5)*getHeight())/2;
				values[i][3] = (float)((Math.random() - 0.5)*getHeight())/2;
			}
			interpolador = GettersFactory.Float.create(keys, values, MetodoDeInterpolacion.Predefinido.CATMULL_ROM_SPLINE);
		}
		
		public void paintComponent(Graphics g){
			g.clearRect(0,0,getWidth(),getHeight());
			g.translate(0,getHeight()/2);

			g.setColor(Color.BLACK);
			g.drawLine(0,0,getWidth(),0);

			for(int i=0; i < N_PUNTOS; i++){
				g.setColor(Color.RED); // valor X
				g.fillRect((int)(keys[i]*getWidth()-2),-(int)(values[i][0]+2),5,5);
				g.setColor(Color.MAGENTA.darker()); // valor Y
				g.fillRect((int)(keys[i]*getWidth()-2),-(int)(values[i][1]+2),5,5);
				g.setColor(Color.BLUE);  // valor Z
				g.fillRect((int)(keys[i]*getWidth()-2),-(int)(values[i][2]+2),5,5);
				g.setColor(Color.YELLOW);  // valor W
				g.fillRect((int)(keys[i]*getWidth()-2),-(int)(values[i][3]+2),5,5);

			}
			
			float[] valoresAnteriores = interpolador.get(0).clone();
			float[] valoresActuales;
	 
			for(int x=0; x < getWidth(); x++){
				
				valoresActuales = interpolador.get((float)x/getWidth()).clone();
				
				g.setColor(Color.RED); // valor X
				g.drawLine(x,-((int)valoresAnteriores[0]),x,-((int)valoresActuales[0]));
				g.setColor(Color.MAGENTA.darker()); // valor Y
				g.drawLine(x,-((int)valoresAnteriores[1]),x,-((int)valoresActuales[1]));
				g.setColor(Color.BLUE);  // valor Z
				g.drawLine(x,-((int)valoresAnteriores[2]),x,-((int)valoresActuales[2]));
				g.setColor(Color.YELLOW);  // valor W
				g.drawLine(x,-((int)valoresAnteriores[3]),x,-((int)valoresActuales[3]));

				valoresAnteriores = valoresActuales;
			}
		}

		public void mouseClicked(MouseEvent e){
		}
		public void mousePressed(MouseEvent e){
		}
		public void mouseReleased(MouseEvent e){
			rellenar();
			repaint();
		}
		public void mouseEntered(MouseEvent e){
		}
		public void mouseExited(MouseEvent e){
		}
	}

	static public void main(String args[]){
		JPanel	resultados;

		resultados = new Resultados();
		
		JFrame frame = new JFrame("Prueba interpolador de Objetos");
		frame.setSize(800,600);
		frame.setDefaultCloseOperation(3);
		
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(resultados,BorderLayout.CENTER);
		frame.setVisible(true);
	}
}

