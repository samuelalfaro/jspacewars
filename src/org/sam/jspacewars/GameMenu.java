package org.sam.jspacewars;

import org.fenggui.Container;
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
	
	public GameMenu() {
		
		this.setLayoutManager( new RowLayout(false) );
		this.getAppearance().add(
				new GradientBackground( new Color(0.0f, 1.0f, 1.0f, 0.25f), new Color(0.0f, 0.0f, 1.0f, 0.5f) ) );
		this.getAppearance().setPadding( new Spacing(10, 10) );
		
		player1 = MyGameMenuButton.derive("1 Player");
		player1.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(ButtonPressedEvent e) {
			}
		});
		
		player2 = MyGameMenuButton.derive("2 Players");
		player2.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(ButtonPressedEvent e) {
			}
		});

		options = MyGameMenuButton.derive("Options");
		options.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(ButtonPressedEvent e) {
				GameMenu.this.buildOptionsMenu();
			}
		});

		sound = MyGameMenuButton.derive("Sound");
		sound.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(ButtonPressedEvent e) {
			}
		});

		graphics = MyGameMenuButton.derive("Graphics ...");
		graphics.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(ButtonPressedEvent e) {
			}
		});
		
		network = MyGameMenuButton.derive("Network");
		network.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(ButtonPressedEvent e) {
			}
		});

		back = MyGameMenuButton.derive("Back");
		back.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(ButtonPressedEvent e) {
				GameMenu.this.buildMainMenu();
			}
		});

		quit = MyGameMenuButton.derive("Quit");
		quit.addButtonPressedListener(new IButtonPressedListener() {
			public void buttonPressed(ButtonPressedEvent e) {
				GameMenu.this.getDisplay().removeWidget( GameMenu.this );
				System.exit(0);
			}
		});
	}

	private void buildMainMenu() {
		this.removeAllWidgets();
		this.addWidget(player1);
		this.addWidget(player2);
		this.addWidget(options);
		this.addWidget(quit);
		this.pack();
		StaticLayout.center( this, this.getDisplay() );
	}

	private void buildOptionsMenu() {
		this.removeAllWidgets();
		this.addWidget(graphics);
		this.addWidget(sound);
		this.addWidget(network);
		this.addWidget(back);
		this.pack();
		StaticLayout.center( this, this.getDisplay() );
	}

	/* (non-Javadoc)
	 * @see org.fenggui.Container#addedToWidgetTree()
	 */
	@Override
	public void addedToWidgetTree(){
		super.addedToWidgetTree();
		buildMainMenu();
	}
}
