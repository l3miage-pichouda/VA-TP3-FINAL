package fr.uga.l3miage.spring.tp3.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@Schema(description = "Représente un examen")
public class ExamResponse {
    @Schema(description = "Id de l'examen")
    private Long id;
    @Schema(description = "Date de début de l'examen")
    private LocalDateTime startDate;
    @Schema(description = "Date de fin de l'examen")
    private LocalDateTime endDate;
    @Schema(description = "Nom de l'examen")
    private String name;
    @Schema(description = "Poids/coefficient de l'examen")
    private int weight;
    @Schema(description = "Fiches d'évaluations des candidats à l'examen")
    private Set<CandidateEvaluationGridResponse> evaluations;
}
