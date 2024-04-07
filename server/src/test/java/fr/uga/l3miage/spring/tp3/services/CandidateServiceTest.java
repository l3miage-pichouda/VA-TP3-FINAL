package fr.uga.l3miage.spring.tp3.services;

import fr.uga.l3miage.spring.tp3.components.CandidateComponent;
import fr.uga.l3miage.spring.tp3.exceptions.rest.CandidateNotFoundRestException;
import fr.uga.l3miage.spring.tp3.exceptions.technical.CandidateNotFoundException;
import fr.uga.l3miage.spring.tp3.models.CandidateEntity;
import fr.uga.l3miage.spring.tp3.models.CandidateEvaluationGridEntity;
import fr.uga.l3miage.spring.tp3.models.ExamEntity;
import fr.uga.l3miage.spring.tp3.repositories.CandidateEvaluationGridRepository;
import fr.uga.l3miage.spring.tp3.repositories.CandidateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class CandidateServiceTest {
    @Autowired
    private CandidateService candidateService;

    @MockBean
    private CandidateComponent candidateComponent;

    @Test
    void TestGetCandidateAverageSuccess() throws CandidateNotFoundException {
        // given
        Long candidateId = 12345L;
        ExamEntity mathExam = ExamEntity.builder().name("Biologie").weight(2).build();
        ExamEntity historyExam = ExamEntity.builder().name("Mathématiques").weight(3).build();
        CandidateEvaluationGridEntity grid1 = CandidateEvaluationGridEntity.builder()
                .grade(8.0)
                .examEntity(mathExam)
                .build();
        CandidateEvaluationGridEntity grid2 = CandidateEvaluationGridEntity.builder()
                .grade(6.0)
                .examEntity(historyExam)
                .build();
        Set<CandidateEvaluationGridEntity> grids = new HashSet<>();
        grids.add(grid1);
        grids.add(grid2);

        CandidateEntity mockCandidate = CandidateEntity.builder()
                .candidateEvaluationGridEntities(grids)
                .build();
        when(candidateComponent.getCandidatById(candidateId)).thenReturn(mockCandidate);

        // La logique de calcul attendue
        double expectedAverage = ((8.0 * 2.0) + (6.0 * 3.0)) / (2.0+3.0);

        // when
        Double actualAverage = candidateService.getCandidateAverage(candidateId);

        // then
        assertEquals(expectedAverage, actualAverage, "The calculated average should match the expected value.");
    }

    @Test
    void getCandidateAverageCandidateNotFound() throws  CandidateNotFoundException {
        // given
        Long candidateId = 2L;
        when(candidateComponent.getCandidatById(candidateId)).thenThrow(new CandidateNotFoundException("Candidate not found", candidateId));

        // when-then
        assertThrows(CandidateNotFoundRestException.class, () -> candidateService.getCandidateAverage(candidateId), "Should throw CandidateNotFoundRestException when candidate is not found.");
    }
}