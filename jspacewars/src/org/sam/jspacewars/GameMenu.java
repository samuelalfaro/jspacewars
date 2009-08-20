package org.sam.jspacewars;

import org.fenggui.Container;
import org.fenggui.Display;
import org.fenggui.decorator.background.GradientBackground;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.RowLayout;
import org.fenggui.layout.StaticLayout;
import org.fenggui.util.Color;
import org.fenggui.util.Spacing;

public class GameMenu extends Container{

	private final MyGameMenuButton player1, player2, options, quit;
	private final MyGameMenuButton sound, graphics, network, back;
	
	public GameMenu( final MyGameMenuButton prototipo,  final Display display ) {
		
		this.setLayoutManager( new RowLayout(false) );
		this.getAppearance().add(
				new GradientBackground( new Color(0.0f, 1.0f, 1.0f, 0.25f), new Color(0.0f, 0.0f, 1.0f, 0.5f) ) );
		this.getAppearance().setPadding( new Spacing(10, 10) );
		
		player1 = prototipo.derive("1 Player");
		player1.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(ButtonPressedEvent e) {
			}
		});
		
		player2 = prototipo.derive("2 Players");
		player2.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(ButtonPressedEvent e) {
			}
		});

		options = prototipo.derive("Options");
		options.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(ButtonPressedEvent e) {
				buildOptionsMenu( display );
			}
		});

		sound =prototipo.derive("Sound");
		sound.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(ButtonPressedEvent e) {
			}
		});

		graphics = prototipo.derive("Graphics ...");
		graphics.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(ButtonPressedEvent e) {
			}
		});
		
		network = prototipo.derive("Network");
		network.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(ButtonPressedEvent e) {
			}
		});

		back = prototipo.derive("Back");
		back.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(ButtonPressedEvent e) {
				buildMainMenu( display );
			}
		});

		quit = prototipo.derive("Quit");
		quit.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(ButtonPressedEvent e) {
				display.removeWidget( GameMenu.this );
				System.exit(0);
			}
		});

		buildMainMenu( display );
	}

	private void buildMainMenu( final Display display ) {
		this.removeAllWidgets();
		this.addWidget(player1);
		this.addWidget(player2);
		this.addWidget(options);
		this.addWidget(quit);
		this.pack();
		StaticLayout.center( this, display );
	}

	private void buildOptionsMenu( final Display display ) {
		this.removeAllWidgets();
		this.addWidget(graphics);
		this.addWidget(sound);
		this.addWidget(network);
		this.addWidget(back);
		this.pack();
		StaticLayout.center( this, display );
	}
}
