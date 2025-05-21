package org.mazeApp;

import org.mazeApp.view.TerminalView;


/**
 * Entry point of the MazeApp application.
 * <p>
 * This launcher determines whether to run the application in terminal mode or
 * graphical (JavaFX) mode, based on the command-line arguments provided.
 * </p>
 * 
 * @author Abdellah, Felipe, Jeremy, Shawrov, Melina
 * @version 1.0
 */
public class Launcher {

        /**
     * Launches the application in either terminal or GUI mode.
     *
     * @param args the command-line arguments; if "terminal" is passed as the first argument,
     *             the terminal interface will be launched instead of the JavaFX GUI
     */
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("terminal")) {
            TerminalView.main(new String[]{}); // Call the main terminal
        } else {
            Main.main(args); // Launch the javafx UI
        }
    }
}
