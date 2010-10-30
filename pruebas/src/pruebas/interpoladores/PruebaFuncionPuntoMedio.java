/* 
 * PruebaFuncionPuntoMedio.java
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
package pruebas.interpoladores;

import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.sam.interpoladores.*;

public class PruebaFuncionPuntoMedio {

	private final static int N_FUNCIONES = 11;

	@SuppressWarnings("serial")
	static class Resultados extends JPanel {
		Funcion.Double[][] funciones;

		Resultados() {
			funciones = new Funcion.Double[2][N_FUNCIONES];
		}

		// rellenamos con valores especificos del punto medio
		private void rellenar() {
			funciones[0][0] = GeneradorDeFunciones.Predefinido.RACIONAL_PUNTO_MEDIO
				.generaFuncion(0.75, 50, 0.15, getHeight() - 50, 0.0001);
			funciones[1][0] = GeneradorDeFunciones.Predefinido.EXPONENCIAL_PUNTO_MEDIO
				.generaFuncion(0.75, 50, 0.15, getHeight() - 50, 0.0001);

			for (int i = 1; i < N_FUNCIONES - 1; i++) {
				funciones[0][i] = GeneradorDeFunciones.Predefinido.RACIONAL_PUNTO_MEDIO
					.generaFuncion(0.75, 50, 0.15, getHeight() - 50, ((double) i) / (N_FUNCIONES - 1));
				funciones[1][i] = GeneradorDeFunciones.Predefinido.EXPONENCIAL_PUNTO_MEDIO
					.generaFuncion(0.75, 50, 0.15, getHeight() - 50, ((double) i) / (N_FUNCIONES - 1));
			}
			funciones[0][N_FUNCIONES - 1] = GeneradorDeFunciones.Predefinido.RACIONAL_PUNTO_MEDIO
				.generaFuncion(0.75, 50, 0.15, getHeight() - 50, 0.9999);
			funciones[1][N_FUNCIONES - 1] = GeneradorDeFunciones.Predefinido.EXPONENCIAL_PUNTO_MEDIO
				.generaFuncion(0.75, 50, 0.15, getHeight() - 50, 0.9999);

		}

		public void paintComponent(Graphics g) {
			rellenar();
			Graphics2D g2 = (Graphics2D) g;
			g2.clearRect(0, 0, getWidth(), getHeight());
			g2.scale(1, -1);
			g2.translate(0, -getHeight());

			g2.setColor(Color.BLACK);
			g2.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);

			double x1 = 0, x2;
			for (int x = 50; x < getWidth() - 50; x++) {
				x2 = (double) (x - 49) / (getWidth() - 100);
				for (int i = 0; i < N_FUNCIONES; i++) {

					g2.setColor(new Color(255, 0, (255 * i)	/ (N_FUNCIONES - 1)));
					g2.drawLine(x, (int) funciones[0][i].f(x1), x, (int) funciones[0][i].f(x2));

					g2.setColor(new Color(0, (255 * i) / (N_FUNCIONES - 1), 0));
					g2.drawLine(x, (int) funciones[1][i].f(x1), x, (int) funciones[1][i].f(x2));
				}
				x1 = x2;
			}
		}
	}

	static public void main(String args[]) {
		JPanel resultados;

		resultados = new Resultados();

		JFrame frame = new JFrame("Prueba Funciones punto medio: FuncionExponencial y Cociente de Polinomios");
		frame.setSize(600, 600);
		frame.setDefaultCloseOperation(3);

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(resultados, BorderLayout.CENTER);
		frame.setVisible(true);
	}
}
