package examen;

import java.util.Scanner;

import examen.controllers.GameController;
import examen.models.Game;
import examen.repositories.GameStateManagerAdapter;
import examen.views.GameView;

public class MinesweeperGame {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GameView view = new GameView(scanner);
        Game game = Game.builder().build();
        GameController controller = new GameController(game, view);
        controller.setGamePersistenceManager(new GameStateManagerAdapter());
        controller.initializeGame();
        controller.start();
    }
}
