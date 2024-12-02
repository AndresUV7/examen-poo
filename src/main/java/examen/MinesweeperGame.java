package examen;

import examen.models.Board;

//Clase principal para iniciar el juego de Buscaminas
public class MinesweeperGame {

	public static void main(String[] args) {
		System.out.println("Bienvenido al juego de Buscaminas");

		Board mediumBoard = Board.builder()
				.rows(10)
				.columns(10)
				.totalMines(10)
				.build();

		System.out.println("Tablero Mediano - Generación Inicial:");
		mediumBoard.generateBoard();
		mediumBoard.printBoard();
		
		// Prueba de revelación de minas
		System.out.println("\nTablero Mediano - Revelación de Minas:");
		mediumBoard.revealAllBoxes(); 
		mediumBoard.printBoardAux();
	}

}
