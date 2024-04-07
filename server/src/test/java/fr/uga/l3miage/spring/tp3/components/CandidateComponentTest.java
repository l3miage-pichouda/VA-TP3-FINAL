package fr.uga.l3miage.spring.tp3.components;
import fr.uga.l3miage.spring.tp3.components.CandidateComponent;
import java.util.Optional;
import fr.uga.l3miage.spring.tp3.models.CandidateEntity;
import fr.uga.l3miage.spring.tp3.repositories.CandidateEvaluationGridRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import fr.uga.l3miage.spring.tp3.repositories.CandidateRepository ;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class CandidateComponentTest {
    @Autowired
    private CandidateComponent candidateComponent;
    @MockBean
    private CandidateRepository candidateRepository;
    @MockBean
    private CandidateEvaluationGridRepository candidateEvaluationGridRepository;
    @Test
    void getAllEliminatedCandidate(){
        //Given
        when(candidateEvaluationGridRepository.findAllByGradeIsLessThanEqual(5)).thenReturn(Set.of());
        //when
        Set<CandidateEntity> candidateEntities = candidateComponent.getAllEliminatedCandidate();
        //then
        assertThat(candidateEntities).isEmpty();
    }


    @Test
    void getCandidatById() throws Exception{
        //Given
        CandidateEntity candidateEntity = CandidateEntity.builder()
                .id(1L)
                .build(
                );
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidateEntity));
        //when
        CandidateEntity candidate = candidateComponent.getCandidatById(1L);
        //then
        assertThat(candidate).isEqualTo(candidateEntity);
    }

}
