package org.mazeApp;

import javafx.application.Application;
import org.mazeApp.view.TerminalView;

public class Launcher {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("terminal")) {
            TerminalView.main(new String[]{}); // Appelle le main terminal
        } else {
            Main.main(args); // Lance l'interface graphique JavaFX
        }
    }
}
