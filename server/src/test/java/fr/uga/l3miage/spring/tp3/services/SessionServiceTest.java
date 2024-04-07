package fr.uga.l3miage.spring.tp3.services;

import fr.uga.l3miage.spring.tp3.components.ExamComponent;
import fr.uga.l3miage.spring.tp3.components.SessionComponent;
import fr.uga.l3miage.spring.tp3.enums.SessionStatus;
import fr.uga.l3miage.spring.tp3.exceptions.rest.CreationSessionRestException;
import fr.uga.l3miage.spring.tp3.exceptions.technical.ExamNotFoundException;
import fr.uga.l3miage.spring.tp3.exceptions.technical.NotFoundSessionEntityException;
import fr.uga.l3miage.spring.tp3.mappers.SessionMapper;
import fr.uga.l3miage.spring.tp3.models.EcosSessionEntity;
import fr.uga.l3miage.spring.tp3.models.ExamEntity;
import fr.uga.l3miage.spring.tp3.request.SessionCreationRequest;
import fr.uga.l3miage.spring.tp3.request.SessionProgrammationCreationRequest;
import fr.uga.l3miage.spring.tp3.responses.CandidateEvaluationGridResponse;
import fr.uga.l3miage.spring.tp3.responses.ExamResponse;
import fr.uga.l3miage.spring.tp3.responses.SessionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class SessionServiceTest {
    @Autowired
    private SessionService sessionService;

    @MockBean
    private ExamComponent examComponent;

    @MockBean
    private SessionComponent sessionComponent;

    @SpyBean
    private SessionMapper sessionMapper;

    @Test
    void testCreateSessionSuccess() throws ExamNotFoundException {
        // given
        SessionProgrammationCreationRequest programmation = SessionProgrammationCreationRequest.builder()
                .steps(Set.of())
                .build();

        SessionCreationRequest request =SessionCreationRequest.builder()
                .name("Session Printemps 2024")
                .ecosSessionProgrammation(programmation)
                .examsId(Set.of())
                .build();

        EcosSessionEntity ecosSessionEntity = sessionMapper.toEntity(request);
        ecosSessionEntity.setExamEntities(Set.of());

        when(examComponent.getAllById(same(Set.of()))).thenReturn(Set.of());
        when(sessionComponent.createSession(any(EcosSessionEntity.class))).thenReturn(ecosSessionEntity);

        SessionResponse expectedResponse = sessionMapper.toResponse(ecosSessionEntity);
        // when
        SessionResponse actualResponse = sessionService.createSession(request);

        // then
        assertThat(actualResponse).usingRecursiveComparison().isEqualTo(expectedResponse);


    }

    @Test
    void testCreateSessionFailed() throws ExamNotFoundException{
        // given
        SessionCreationRequest request = SessionCreationRequest.builder()
                .name("Session Printemps 2024")
                .ecosSessionProgrammation(SessionProgrammationCreationRequest.builder()
                        .steps(Set.of()) // Assurez-vous que cela est conforme à votre structure de données
                        .build())
                .examsId(Set.of(1L)) // Supposons que cet ID d'examen n'existe pas
                .build();

        when(examComponent.getAllById(anySet())).thenThrow(new ExamNotFoundException("Examen non trouvé"));

        // when & then
        assertThrows(CreationSessionRestException.class, () -> {
            sessionService.createSession(request);
        });

        // Vérification supplémentaire pour s'assurer qu'aucune session n'est créée en cas d'échec
        verify(sessionComponent, never()).createSession(any(EcosSessionEntity.class));
    }
    @Test
    void testEndSessionSuccess() throws NotFoundSessionEntityException {
        //given
        Long id = 1234L;
        EcosSessionEntity session = EcosSessionEntity
                .builder()
                .id(id)
                .name("Session normale")
                .status(SessionStatus.EVAL_STARTED)
                .build();
        ExamEntity exam = ExamEntity
                .builder()
                .name("Biologie")
                .weight(2)
                .build();

        CandidateEvaluationGridResponse evaluation1 = CandidateEvaluationGridResponse.builder().grade(15.0).build();
        CandidateEvaluationGridResponse evaluation2 = CandidateEvaluationGridResponse.builder().grade(12.0).build();

        Set<CandidateEvaluationGridResponse> evaluations = new HashSet<>(Arrays.asList(evaluation1, evaluation2));
        Set<ExamEntity> exams = new HashSet<>(Collections.singletonList(exam));
        session.setExamEntities(exams); // Associe l'examen à la session

        // Simuler le comportement de sessionComponent pour retourner la session mise à jour
        when(sessionComponent.endSessionEvaluation(id)).thenReturn(session);

        // Simuler le comportement de sessionMapper pour convertir la session et l'examen en leurs DTOs correspondants
        SessionResponse sessionResponse = SessionResponse.builder()
                .id(id)
                .name("Session normale")
                .status(fr.uga.l3miage.spring.tp3.responses.enums.SessionStatus.EVAL_ENDED) // Le statut doit être mis à jour pour refléter la fin de la session
                .examEntities(exams.stream().map(examEntity -> {
                    return ExamResponse.builder()
                            .name(examEntity.getName())
                            .weight(examEntity.getWeight())
                            .evaluations(evaluations) // Associe les évaluations à l'examen
                            .build();
                }).collect(Collectors.toSet()))
                .build();

        when(sessionMapper.toResponse(any(EcosSessionEntity.class))).thenReturn(sessionResponse);

        // Exécuter la méthode à tester
        Set<CandidateEvaluationGridResponse> actualEvaluations = sessionService.endSessionEvaluation(id);

        // Assertions pour vérifier que les évaluations attendues sont retournées
        assertNotNull(actualEvaluations, "Le résultat des évaluations ne devrait pas être null");
        assertEquals(evaluations.size(), actualEvaluations.size(), "Le nombre d'évaluations retournées ne correspond pas");
        assertTrue(actualEvaluations.containsAll(evaluations), "Les évaluations retournées ne correspondent pas aux évaluations attendues");

        verify(sessionComponent,times(1)).endSessionEvaluation(id);
        verify(sessionMapper,times(1)).toResponse(any(EcosSessionEntity.class));
    }

}
