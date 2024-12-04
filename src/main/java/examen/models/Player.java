package examen.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

// Implementa el patrón builder
@Builder
// Genera getters, setters, toString, etc.
@Data
// Genera un constructor con todos los atributos
@AllArgsConstructor
public class Player {
    private String name;
    
}
