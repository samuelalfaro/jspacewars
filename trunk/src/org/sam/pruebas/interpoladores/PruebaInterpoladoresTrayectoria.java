package org.sam.pruebas.interpoladores;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.sam.interpoladores.*;

public class PruebaInterpoladoresTrayectoria{

	static class Resultados extends JPanel implements MouseListener{
		private static final long serialVersionUID = 1L;
		
		final int 	N_PUNTOS = 10;
		final float values[][];
		
		Trayectoria.Float<float[]> interpolador;

		Resultados(){
			values = new float[N_PUNTOS][3];

			rellenar(800,600);
			this.addMouseListener(this);
		}

		@SuppressWarnings("unchecked")
		private void rellenar(int w, int h){
			for (int i = 0; i < N_PUNTOS; i++){
				values[i][0] = (float)Math.random()*w;
				values[i][1] = (float)((Math.random() - 0.5)*h)/2;
			}
			interpolador = (Trayectoria.Float<float[]>)GettersFactory.Float.create(Keys.PROPORCIONALES, 1.0f, 0.0f, values, MetodoDeInterpolacion.Predefinido.CATMULL_ROM_SPLINE);
		}
		
		public void paintComponent(Graphics g){
			g.clearRect(0,0,getWidth(),getHeight());
			g.translate(0,getHeight()/2);

			g.setColor(Color.BLACK);
			g.drawLine(0,0,getWidth(),0);

			g.setColor(Color.BLUE); 
			g.fillRect((int)(values[0][0]-2),-(int)(values[0][1]+2),5,5);
			g.setColor(Color.RED);
			g.fillRect((int)(values[N_PUNTOS-1][0]-2),-(int)(values[N_PUNTOS-1][1]+2),5,5);
			g.setColor(Color.DARK_GRAY);
			for(int i=1; i < N_PUNTOS-1; i++){
				g.fillRect((int)(values[i][0]-2),-(int)(values[i][1]+2),5,5);
			}
			
			float[] valoresAnteriores = interpolador.get(0).clone();
			float[] valoresActuales;
	 
			float ialfa= 0.001f;
			for(float alfa=0.0f; alfa < 1.0+ialfa; alfa+=ialfa){
				
				valoresActuales = interpolador.get(alfa).clone();
				
				g.drawLine(
					(int)valoresAnteriores[0],-((int)valoresAnteriores[1]),
					(int)valoresActuales[0],-((int)valoresActuales[1])
				);
				
				valoresAnteriores = valoresActuales;
			}
			
			g.setColor(Color.ORANGE.darker());
			valoresAnteriores = interpolador.get(0).clone();
			ialfa= 0.1f;
			for(float alfa=0.0f; alfa < 1.0+ialfa; alfa+=ialfa){
				valoresActuales = interpolador.get(alfa).clone();
				g.drawRect((int)valoresActuales[0]-1, -(int)valoresActuales[1]-1, 3, 3);
				g.drawLine(
						(int)valoresAnteriores[0],-((int)valoresAnteriores[1]),
						(int)valoresActuales[0],-((int)valoresActuales[1])
					);
				valoresAnteriores = valoresActuales;
			}
		}

		public void mouseClicked(MouseEvent e){
		}
		public void mousePressed(MouseEvent e){
		}
		public void mouseReleased(MouseEvent e){
			rellenar(getWidth(), getHeight());
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
		
		JFrame frame = new JFrame("Prueba interpoladores de Trayectoria");
		frame.setSize(800,600);
		frame.setDefaultCloseOperation(3);
		
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(resultados,BorderLayout.CENTER);
		frame.setVisible(true);
	}
}