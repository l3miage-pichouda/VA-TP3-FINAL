package fr.uga.l3miage.spring.tp3.repositories;

import fr.uga.l3miage.spring.tp3.enums.TestCenterCode;
import fr.uga.l3miage.spring.tp3.models.CandidateEntity;
import fr.uga.l3miage.spring.tp3.models.CandidateEvaluationGridEntity;
import fr.uga.l3miage.spring.tp3.models.TestCenterEntity;
import fr.uga.l3miage.spring.tp3.repositories.CandidateEvaluationGridRepository;
import fr.uga.l3miage.spring.tp3.repositories.CandidateRepository;
import fr.uga.l3miage.spring.tp3.repositories.TestCenterRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")
public class CandidateRepositoryTest {
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private TestCenterRepository testCenterRepository;
    @Autowired
    private CandidateEvaluationGridRepository candidateEvaluationGridRepository;

    @Test
    void testFindAllByTestCenterEntityCode(){
        TestCenterEntity testCenterEntity = TestCenterEntity
                .builder()
                .code(TestCenterCode.DIJ)
                .build();
        TestCenterEntity testCenterEntity1=TestCenterEntity
                .builder()
                .code(TestCenterCode.GRE)
                .build();
        CandidateEntity candidateEntity= CandidateEntity
                .builder()
                .firstname("John")
                .email("test@test2.fr")
                .testCenterEntity(testCenterEntity)
                .build();
        CandidateEntity candidateEntity1= CandidateEntity
                .builder()
                .firstname("Adrien")
                .email("test@test.fr")
                .testCenterEntity(testCenterEntity1)
                .build();
        testCenterRepository.save(testCenterEntity);
        testCenterRepository.save(testCenterEntity1);
        candidateRepository.save(candidateEntity);
        candidateRepository.save(candidateEntity1);

        Set<CandidateEntity> responses = candidateRepository.findAllByTestCenterEntityCode(TestCenterCode.DIJ);
        assertThat(responses).hasSize(1);
        assertThat(responses.stream().findFirst().get().getTestCenterEntity().getCode()).isEqualTo(TestCenterCode.DIJ);
    }

    @Test
    void testFindAllByCandidateEvaluationGridEntitiesGradeLessThan(){
        CandidateEntity candidateEntity= CandidateEntity
                .builder()
                .firstname("John")
                .email("test@test2.fr")
                .build();
        CandidateEntity candidateEntity1= CandidateEntity
                .builder()
                .firstname("Adrien")
                .email("test@test.fr")
                .build();
        candidateRepository.save(candidateEntity);
        candidateRepository.save(candidateEntity1);
        CandidateEvaluationGridEntity candidateEvaluationGridEntity =CandidateEvaluationGridEntity
                .builder()
                .grade(10.1)
                .candidateEntity(candidateEntity)
                .build();
        CandidateEvaluationGridEntity candidateEvaluationGridEntity1 =CandidateEvaluationGridEntity
                .builder()
                .candidateEntity(candidateEntity1)
                .grade(9)
                .build();
        candidateEvaluationGridRepository.save(candidateEvaluationGridEntity);
        candidateEvaluationGridRepository.save(candidateEvaluationGridEntity1);
        Set<CandidateEvaluationGridEntity> set1 = new HashSet<>();
        set1.add(candidateEvaluationGridEntity1);
        Set<CandidateEvaluationGridEntity>set2=new HashSet<>();
        set2.add(candidateEvaluationGridEntity);
        candidateEntity.setCandidateEvaluationGridEntities(set1);
        candidateEntity1.setCandidateEvaluationGridEntities(set2);
        candidateRepository.save(candidateEntity);
        candidateRepository.save(candidateEntity1);


        Set<CandidateEntity> responses = candidateRepository.findAllByCandidateEvaluationGridEntitiesGradeLessThan(10);
        assertThat(responses).hasSize(1);
        assertThat(responses.stream().findFirst().get().getCandidateEvaluationGridEntities().stream().findFirst().get().getGrade()).isEqualTo(9);
    }

    @Test
    void testFindAllByHasExtraTimeFalseAndBirthDateBefore(){
        CandidateEntity candidateEntity = CandidateEntity
                .builder()
                .hasExtraTime(false)
                .email("test@test.fr")
                .birthDate(LocalDate.of(2001,7,5))
                .build();
        CandidateEntity candidateEntity1=CandidateEntity
                .builder()
                .hasExtraTime(true)
                .email("test@test1.fr")
                .birthDate(LocalDate.of(2005,1,1))
                .build();
        CandidateEntity candidateEntity2=CandidateEntity
                .builder()
                .hasExtraTime(true)
                .email("test@test2.fr")
                .birthDate(LocalDate.of(2000,1,1))
                .build();
        CandidateEntity candidateEntity3=CandidateEntity
                .builder()
                .hasExtraTime(false)
                .email("test@test3.fr")
                .birthDate(LocalDate.of(2007,1,1))
                .build();

        candidateRepository.save(candidateEntity);
        candidateRepository.save(candidateEntity1);
        candidateRepository.save(candidateEntity2);
        candidateRepository.save(candidateEntity3);

        Set<CandidateEntity> responsesCandidateEntity = candidateRepository.findAllByHasExtraTimeFalseAndBirthDateBefore(LocalDate.of(2002,1,1));

        assertThat(responsesCandidateEntity).hasSize(1);
        assertThat(responsesCandidateEntity.stream().findFirst().get().getBirthDate()).isEqualTo(LocalDate.of(2001,7,5));
        assertThat(responsesCandidateEntity.stream().findFirst().get().isHasExtraTime()).isFalse();

    }
}
