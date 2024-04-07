package fr.uga.l3miage.spring.tp3.components;

import fr.uga.l3miage.spring.tp3.enums.SessionStatus;
import fr.uga.l3miage.spring.tp3.exceptions.technical.NotFoundSessionEntityException;
import fr.uga.l3miage.spring.tp3.models.EcosSessionEntity;
import fr.uga.l3miage.spring.tp3.models.EcosSessionProgrammationStepEntity;
import fr.uga.l3miage.spring.tp3.repositories.EcosSessionProgrammationRepository;
import fr.uga.l3miage.spring.tp3.repositories.EcosSessionProgrammationStepRepository;
import fr.uga.l3miage.spring.tp3.repositories.EcosSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SessionComponent {
    private final EcosSessionRepository ecosSessionRepository;
    private final EcosSessionProgrammationRepository ecosSessionProgrammationRepository;
    private final EcosSessionProgrammationStepRepository ecosSessionProgrammationStepRepository;


    public EcosSessionEntity createSession(EcosSessionEntity entity){
        ecosSessionProgrammationStepRepository.saveAll(entity.getEcosSessionProgrammationEntity().getEcosSessionProgrammationStepEntities());
        ecosSessionProgrammationRepository.save(entity.getEcosSessionProgrammationEntity());
        return ecosSessionRepository.save(entity);
    }

    public EcosSessionEntity endSessionEvaluation(Long id) throws NotFoundSessionEntityException{
        EcosSessionEntity session = ecosSessionRepository.findById(id)
                .orElseThrow(() -> new NotFoundSessionEntityException(String.format("La session %d est introuvable", id)));

        // Vérification que la dernière étape est passée
        if (!isLastStepPassed(session)) {
            throw new IllegalStateException("The last step is not passed yet.");
        }

        // Vérification que l'état précédent est EVAL_STARTED
        if (!session.getStatus().equals(SessionStatus.EVAL_STARTED)) {
            throw new IllegalStateException("Session is not in EVAL_STARTED state.");
        }

        // Mise à jour de l'état de la session
        session.setStatus(SessionStatus.EVAL_ENDED);
        // Enregistrer les modifications dans la base de données
        ecosSessionRepository.save(session);

        // Retourner l'entité EcosSession mise à jour
        return session;
    }

    private boolean isLastStepPassed(EcosSessionEntity session) {
        if (session.getEcosSessionProgrammationEntity() == null ||
                session.getEcosSessionProgrammationEntity().getEcosSessionProgrammationStepEntities() == null ||
                session.getEcosSessionProgrammationEntity().getEcosSessionProgrammationStepEntities().isEmpty()) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();


        Optional<EcosSessionProgrammationStepEntity> lastStepOptional = session.getEcosSessionProgrammationEntity()
                .getEcosSessionProgrammationStepEntities()
                .stream()
                .max(Comparator.comparing(EcosSessionProgrammationStepEntity::getDateTime));

        if (lastStepOptional.isPresent()) {
            EcosSessionProgrammationStepEntity lastStep = lastStepOptional.get();
            return !lastStep.getDateTime().isAfter(now); // Vérifiez si la dernière étape est passée (dateTime n'est pas dans le futur)
        } else {
            // S'il n'y a pas d'étapes, considérez que la condition pour passer n'est pas remplie
            return false;
        }
    }
}
