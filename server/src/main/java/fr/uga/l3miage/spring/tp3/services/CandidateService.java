package fr.uga.l3miage.spring.tp3.services;

import fr.uga.l3miage.spring.tp3.components.CandidateComponent;
import fr.uga.l3miage.spring.tp3.exceptions.rest.CandidateNotFoundRestException;
import fr.uga.l3miage.spring.tp3.exceptions.technical.CandidateNotFoundException;
import fr.uga.l3miage.spring.tp3.models.CandidateEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Period;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CandidateService {
    private final CandidateComponent candidateComponent;

    public Double getCandidateAverage(Long candidateId) {
        try {
            CandidateEntity candidateEntity = candidateComponent.getCandidatById(candidateId);
            return (candidateEntity.getCandidateEvaluationGridEntities().stream().reduce(0d, (average, grid) -> average + (grid.getGrade() * grid.getExamEntity().getWeight()), Double::sum))
                    / candidateEntity.getCandidateEvaluationGridEntities().stream().reduce(0,(acc,grid) -> acc + grid.getExamEntity().getWeight(),Integer::sum);
        } catch (CandidateNotFoundException e) {
            throw new CandidateNotFoundRestException(e.getMessage(),e.getCandidateId());
        }
    }
    public Boolean addStudentsToTestCenter(Long testCenterId, Set<Long> candidateIds) {
        boolean testCenterExists = true;
        if (!testCenterExists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le centre de test n'est pas trouvé");
        }

        for (Long candidateId : candidateIds) {
            try {
                CandidateEntity candidate = candidateComponent.getCandidatById(candidateId);
                LocalDate birthDate = candidate.getBirthDate();
                int age = Period.between(birthDate, LocalDate.now()).getYears();

                if (age < 18) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un ou plusieurs étudiants ont moins de 18 ans");
                }
            } catch (CandidateNotFoundException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Le candidat [%s] n'a pas été trouvé", candidateId));
            }
        }

        return true;
    }
}
