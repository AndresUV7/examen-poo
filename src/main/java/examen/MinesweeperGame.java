package examen;

import java.util.Scanner;

import examen.controllers.GameController;
import examen.models.Game;
import examen.views.GameView;

public class MinesweeperGame {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GameView view = new GameView(scanner);
        Game game = Game.builder().build();  // Crea una instancia vacía del modelo
        GameController controller = new GameController(game, view);

        controller.initializeGame();
        controller.start();
    }
}
