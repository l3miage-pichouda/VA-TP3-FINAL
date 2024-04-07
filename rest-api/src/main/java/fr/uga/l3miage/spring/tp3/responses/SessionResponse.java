package fr.uga.l3miage.spring.tp3.responses;

import fr.uga.l3miage.spring.tp3.responses.enums.SessionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class SessionResponse {
    private Long id;
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private SessionStatus status;

    private Set<ExamResponse> examEntities;

    private EcosSessionProgrammationResponse ecosSessionProgrammationEntity;
}
