package fr.uga.l3miage.spring.tp3.responses;

import fr.uga.l3miage.spring.tp3.responses.enums.SessionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@Schema(description = "Représente une session d'examens")
public class SessionResponse {
    private Long id;
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private SessionStatus status;

    private Set<ExamResponse> examEntities;

    private EcosSessionProgrammationResponse ecosSessionProgrammationEntity;
}
