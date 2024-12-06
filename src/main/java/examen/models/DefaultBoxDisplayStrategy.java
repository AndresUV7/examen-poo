package examen.models;

import examen.models.GameInterfaces.IBoxDisplayStrategy;

public class DefaultBoxDisplayStrategy implements IBoxDisplayStrategy {
    @Override
    public String getRepresentation(Box box) {
        if (box.isRevealed()) {
            return box instanceof MinedBox ? "X" : 
                String.valueOf(((EmptyBox) box).getAdjacentMinesCount());
        } else if (box.isFlagged()) {
            return "F";
        }
        return "?";
    }
}