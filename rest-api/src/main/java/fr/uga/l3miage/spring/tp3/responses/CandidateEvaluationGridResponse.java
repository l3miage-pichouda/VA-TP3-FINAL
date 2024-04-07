package fr.uga.l3miage.spring.tp3.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Décrit une feuille d'évaluation d'un candidat à l'examen")
public class CandidateEvaluationGridResponse {
    @Schema(description = "Id de la feuille d'évaluation")
    private Long sheetNumber;
    @Schema(description = "Note obtenue à l'évaluation")
    private Double grade;
}
