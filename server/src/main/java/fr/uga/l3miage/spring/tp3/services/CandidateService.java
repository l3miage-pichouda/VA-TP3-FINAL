package fr.uga.l3miage.spring.tp3.services;

import fr.uga.l3miage.spring.tp3.components.CandidateComponent;
import fr.uga.l3miage.spring.tp3.exceptions.rest.CandidateNotFoundRestException;
import fr.uga.l3miage.spring.tp3.exceptions.technical.CandidateNotFoundException;
import fr.uga.l3miage.spring.tp3.models.CandidateEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
