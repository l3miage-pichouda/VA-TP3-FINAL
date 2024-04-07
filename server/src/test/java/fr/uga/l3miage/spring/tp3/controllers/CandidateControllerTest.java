package fr.uga.l3miage.spring.tp3.controllers;

import fr.uga.l3miage.spring.tp3.models.CandidateEntity;
import fr.uga.l3miage.spring.tp3.models.CandidateEvaluationGridEntity;
import fr.uga.l3miage.spring.tp3.models.ExamEntity;
import fr.uga.l3miage.spring.tp3.repositories.CandidateRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@AutoConfigureTestDatabase
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")

public class CandidateControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private CandidateRepository candidateRepository;

    @AfterEach
    public void clear() {
        candidateRepository.deleteAll();
    }

    @Test
    void getCandidateAverageDontThrow(){
        final HttpHeaders headers = new HttpHeaders();
        final HashMap<String,Long> urlParam = new HashMap<>();
        CandidateEntity candidateEntity = CandidateEntity
                .builder()
                .id(1L)
                .email("")
                .build();
        ExamEntity examEntity = ExamEntity
                .builder()
                .weight(1)
                .build();
        CandidateEvaluationGridEntity candidateEvaluationGridEntity2 = CandidateEvaluationGridEntity
                .builder()
                .grade(15)
                .build();
        candidateEvaluationGridEntity2.setExamEntity(examEntity);
        candidateEntity.setCandidateEvaluationGridEntities(Set.of(candidateEvaluationGridEntity2));
        candidateRepository.save(candidateEntity);

        urlParam.put("idCandidate", 1L);
        ResponseEntity<Double> rep = testRestTemplate.exchange("/api/candidates/{idCandidate}/average", HttpMethod.GET, new HttpEntity<>(null, headers), Double.class,urlParam);
        assertThat(rep.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getCandidateAverageThrow(){
        final HttpHeaders headers = new HttpHeaders();
        final HashMap<String, Object> urlParam = new HashMap<>();
        urlParam.put("idCandidate", "Le candidat n'a pas été trouvé");
        ResponseEntity<ChangeSetPersister.NotFoundException> rep = testRestTemplate.exchange("/api/candidates/{idCandidate}", HttpMethod.GET, new HttpEntity<>(null, headers), ChangeSetPersister.NotFoundException.class, urlParam);
        assertThat(rep.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

}
