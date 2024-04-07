package fr.uga.l3miage.spring.tp3.components;
import fr.uga.l3miage.spring.tp3.enums.SessionStatus;
import fr.uga.l3miage.spring.tp3.exceptions.technical.NotFoundSessionEntityException;
import fr.uga.l3miage.spring.tp3.models.EcosSessionEntity;
import fr.uga.l3miage.spring.tp3.models.EcosSessionProgrammationEntity;
import fr.uga.l3miage.spring.tp3.models.EcosSessionProgrammationStepEntity;
import fr.uga.l3miage.spring.tp3.repositories.EcosSessionProgrammationRepository;
import fr.uga.l3miage.spring.tp3.repositories.EcosSessionProgrammationStepRepository;
import fr.uga.l3miage.spring.tp3.repositories.EcosSessionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class SessionComponentTest {

    @MockBean
    private EcosSessionRepository ecosSessionRepository;

    @MockBean
    private EcosSessionProgrammationRepository ecosSessionProgrammationRepository;

    @MockBean
    private EcosSessionProgrammationStepRepository ecosSessionProgrammationStepRepository;

    @Autowired
    private SessionComponent sessionComponent;

    @Test
    void createSessionTest() {
        // Given
        EcosSessionProgrammationStepEntity stepEntity = new EcosSessionProgrammationStepEntity();

        EcosSessionProgrammationEntity programmationEntity = new EcosSessionProgrammationEntity();
        programmationEntity.setEcosSessionProgrammationStepEntities(Set.of(stepEntity));

        EcosSessionEntity session = new EcosSessionEntity();
        session.setEcosSessionProgrammationEntity(programmationEntity);

        when(ecosSessionRepository.save(any(EcosSessionEntity.class))).thenReturn(session);

        // When
        EcosSessionEntity savedSession = sessionComponent.createSession(session);

        // Then
        assertThat(savedSession.getEcosSessionProgrammationEntity().getEcosSessionProgrammationStepEntities())
                .isNotNull()
                .containsExactlyInAnyOrder(stepEntity);
    }

    @Test
    public void endSessionEvaluationSuccess() throws NotFoundSessionEntityException {
        // Création de la session et de la dernière étape passée directement dans la méthode de test
        EcosSessionProgrammationStepEntity lastStep = EcosSessionProgrammationStepEntity.builder()
                .dateTime(LocalDateTime.now().minusDays(1)) // Assurez-vous que la dernière étape est passée
                .description("Etape finale")
                .build();

        EcosSessionProgrammationEntity programmationEntity = EcosSessionProgrammationEntity.builder()
                .label("Programmation test")
                .ecosSessionProgrammationStepEntities(Set.of(lastStep))
                .build();

        EcosSessionEntity session = EcosSessionEntity.builder()
                .name("Examens Session 1")
                .status(SessionStatus.EVAL_STARTED)
                .ecosSessionProgrammationEntity(programmationEntity)
                .build();

        // Configurer le mock pour retourner la session
        when(ecosSessionRepository.findById(anyLong())).thenReturn(Optional.of(session));

        // Appel de la méthode à tester
        EcosSessionEntity updatedSession = sessionComponent.endSessionEvaluation(anyLong());

        // Vérifier les assertions
        assertNotNull(updatedSession);
        assertEquals(SessionStatus.EVAL_ENDED, updatedSession.getStatus());
        verify(ecosSessionRepository).save(session); // Vérifier que save a été appelé avec la session mise à jour
    }

    @Test
    public void endSessionEvaluationFailsWhenSessionNotFound() {
        // Configuration pour simuler l'absence de session correspondant à l'ID fourni
        long nonExistentSessionId = 999L; // ID hypothétique pour une session inexistante
        when(ecosSessionRepository.findById(nonExistentSessionId)).thenReturn(Optional.empty());

        // Exécuter et s'attendre à ce que NotFoundSessionEntityException soit lancée
        Exception exception = assertThrows(NotFoundSessionEntityException.class, () -> {
            sessionComponent.endSessionEvaluation(nonExistentSessionId);
        });

        // Vérifier que le message de l'exception contient l'ID de la session introuvable
        String expectedMessage = String.format("La session %d est introuvable", nonExistentSessionId);
        assertTrue(exception.getMessage().contains(expectedMessage));
    }
    @Test
    public void endSessionEvaluationFailLastStepNotPassed() {
        // Création de la session avec la dernière étape dans le futur
        EcosSessionProgrammationStepEntity futureLastStep = EcosSessionProgrammationStepEntity.builder()
                .dateTime(LocalDateTime.now().plusDays(1))
                .description("Etape non terminée")
                .build();

        EcosSessionProgrammationEntity programmationEntity = EcosSessionProgrammationEntity.builder()
                .label("Programmation test non terminé")
                .ecosSessionProgrammationStepEntities(Set.of(futureLastStep))
                .build();

        EcosSessionEntity sessionWithFutureStep = EcosSessionEntity.builder()
                .name("Session non terminée")
                .status(SessionStatus.EVAL_STARTED)
                .ecosSessionProgrammationEntity(programmationEntity)
                .build();

        // Configurer le mock pour retourner la session avec la future dernière étape
        when(ecosSessionRepository.findById(anyLong())).thenReturn(Optional.of(sessionWithFutureStep));

        // Exécuter et vérifier que l'exception est lancée
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            sessionComponent.endSessionEvaluation(anyLong());
        });

        assertEquals("The last step is not passed yet.", exception.getMessage());
    }

    @Test
    public void endSessionEvaluationFailsWhenNotInEvalStartedState() {
        EcosSessionProgrammationStepEntity lastStep = EcosSessionProgrammationStepEntity.builder()
                .dateTime(LocalDateTime.now().minusDays(1)) // Assurez-vous que la dernière étape est passée
                .description("Etape finale")
                .build();

        EcosSessionProgrammationEntity programmationEntity = EcosSessionProgrammationEntity.builder()
                .label("Programmation test")
                .ecosSessionProgrammationStepEntities(Set.of(lastStep))
                .build();
        // Création d'une session dans un état différent de EVAL_STARTED
        EcosSessionEntity sessionNotInEvalStarted = EcosSessionEntity.builder()
                .name("Examen déjà terminé")
                .status(SessionStatus.EVAL_ENDED)
                .ecosSessionProgrammationEntity(programmationEntity)// Exemple d'un autre état
                .build();

        // Configurer le mock pour retourner cette session spécifique
        when(ecosSessionRepository.findById(anyLong())).thenReturn(Optional.of(sessionNotInEvalStarted));

        // Exécuter et s'attendre à ce qu'une IllegalStateException soit levée
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            sessionComponent.endSessionEvaluation(anyLong());
        });

        // Vérifier que le message de l'exception correspond au cas où la session n'est pas en EVAL_STARTED
        assertEquals("Session is not in EVAL_STARTED state.", exception.getMessage());
    }


}
