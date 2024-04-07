package fr.uga.l3miage.spring.tp3.controllers;
import fr.uga.l3miage.spring.tp3.enums.SessionStatus;
import fr.uga.l3miage.spring.tp3.models.EcosSessionEntity;
import fr.uga.l3miage.spring.tp3.models.EcosSessionProgrammationEntity;
import fr.uga.l3miage.spring.tp3.models.EcosSessionProgrammationStepEntity;
import fr.uga.l3miage.spring.tp3.repositories.EcosSessionProgrammationRepository;
import fr.uga.l3miage.spring.tp3.repositories.EcosSessionProgrammationStepRepository;
import fr.uga.l3miage.spring.tp3.repositories.EcosSessionRepository;
import fr.uga.l3miage.spring.tp3.request.SessionCreationRequest;
import fr.uga.l3miage.spring.tp3.request.SessionProgrammationCreationRequest;
import fr.uga.l3miage.spring.tp3.responses.CandidateEvaluationGridResponse;
import fr.uga.l3miage.spring.tp3.responses.SessionResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestDatabase
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")
public class SessionControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private EcosSessionRepository sessionRepository;

    @Autowired
    private EcosSessionProgrammationRepository sessionProgrammationRepository;
    @Autowired
    private EcosSessionProgrammationStepRepository sessionProgrammationStepRepository;
    @AfterEach
    public void clear(){
        sessionRepository.deleteAll();
    }
    @Test
    void createSessionSuccess(){
        SessionCreationRequest request = SessionCreationRequest.builder()
                .name("test")
                .startDate(LocalDateTime.MIN)
                .endDate(LocalDateTime.MAX)
                .examsId(new HashSet<>())
                .ecosSessionProgrammation(SessionProgrammationCreationRequest.builder().steps(new HashSet<>()).build())
                .build();

        ResponseEntity<SessionResponse> responseEntity = testRestTemplate.postForEntity("/api/sessions/create", request, SessionResponse.class);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(201);
        assertThat(responseEntity.getBody()).isNotNull();
    }

    @Test
    void createSessionWithInvalidExamId() {

        Set<Long> invalidExamIds = new HashSet<>();
        invalidExamIds.add(-1L);
        SessionCreationRequest request = SessionCreationRequest.builder()
                .name("test")
                .startDate(LocalDateTime.MIN)
                .endDate(LocalDateTime.MAX)
                .examsId(invalidExamIds)
                .ecosSessionProgrammation(SessionProgrammationCreationRequest.builder().steps(new HashSet<>()).build())
                .build();

        // Envoyer la requête et obtenir la réponse
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("/api/sessions/create", request, String.class);

        // Vérifier que la réponse est bien un statut 400 (BAD_REQUEST)
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
    @Test
    public void endSessionEvaluationSuccess() {
        final HttpHeaders headers = new HttpHeaders();
        EcosSessionEntity session = EcosSessionEntity.builder()
                .name("Test Session")
                .status(SessionStatus.EVAL_STARTED)
                .build();
        sessionRepository.save(session);
        EcosSessionProgrammationEntity programmationEntity = EcosSessionProgrammationEntity.builder()
                .label("Programmation Test")
                .build();
        sessionProgrammationRepository.save(programmationEntity);
        // Définir une date/heure dans le passé pour lastStep
        LocalDateTime stepDateTime = LocalDateTime.now().minusDays(1);
        EcosSessionProgrammationStepEntity lastStep = EcosSessionProgrammationStepEntity.builder()
                .dateTime(stepDateTime)
                .ecosSessionProgrammationEntity(programmationEntity)
                .description("Final step")
                .build();
        sessionProgrammationStepRepository.save(lastStep);
        Set<EcosSessionProgrammationStepEntity> steps = Set.of(lastStep);
        programmationEntity.setEcosSessionProgrammationStepEntities(steps);
        sessionProgrammationRepository.save(programmationEntity);
        session.setEcosSessionProgrammationEntity(programmationEntity);
        sessionRepository.save(session);

        final Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("id",session.getId() );
        // Envoi de la requête pour terminer l'évaluation
        ResponseEntity<Set<CandidateEvaluationGridResponse>> response = testRestTemplate.exchange(
                "/api/sessions/{id}/end-eval",
                HttpMethod.PUT,
                new HttpEntity<>(null, headers), // Pas de corps de requête nécessaire pour cet appel
                new ParameterizedTypeReference<>() {},
                urlParams); // Utiliser l'ID de la session sauvegardée

        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Ajouter des assertions supplémentaires si nécessaire
    }

    @Test
    public void whenUpdateSessionStateCausesConflict_thenReceiveConflictResponse() {
        EcosSessionEntity session = EcosSessionEntity.builder()
                .name("Test Session")
                .status(SessionStatus.EVAL_ENDED)
                .build();
        sessionRepository.save(session);
        EcosSessionProgrammationEntity programmationEntity = EcosSessionProgrammationEntity.builder()
                .label("Programmation Test")
                .build();
        sessionProgrammationRepository.save(programmationEntity);
        // Définir une date/heure dans le passé pour lastStep
        LocalDateTime stepDateTime = LocalDateTime.now().minusDays(1);
        EcosSessionProgrammationStepEntity lastStep = EcosSessionProgrammationStepEntity.builder()
                .dateTime(stepDateTime)
                .ecosSessionProgrammationEntity(programmationEntity)
                .description("Final step")
                .build();
        sessionProgrammationStepRepository.save(lastStep);
        Set<EcosSessionProgrammationStepEntity> steps = Set.of(lastStep);
        programmationEntity.setEcosSessionProgrammationStepEntities(steps);
        sessionProgrammationRepository.save(programmationEntity);
        session.setEcosSessionProgrammationEntity(programmationEntity);
        sessionRepository.save(session);

        final Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("id",session.getId() );
        // Envoi de la requête pour terminer l'évaluation
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/sessions/{id}/end-eval",
                HttpMethod.PUT,
                null, // Pas de corps de requête nécessaire pour cet appel
                String.class,
                urlParams); // Utiliser l'ID de la session sauvegardée

        // Vérifier que le code de statut est 409 CONFLIT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        // Vérifier les détails de la réponse
        // Vous devrez ajuster ces assertions selon le format exact de votre message d'erreur
        assertThat(response.getBody()).contains("URI");
        assertThat(response.getBody()).contains("Message d'erreur");
        assertThat(response.getBody()).contains("État actuel de la session");
    }

}
