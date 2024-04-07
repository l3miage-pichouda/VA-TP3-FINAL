package fr.uga.l3miage.spring.tp3.controllers;
import fr.uga.l3miage.spring.tp3.controller.CandidateController;
import fr.uga.l3miage.spring.tp3.exceptions.CandidatNotFoundResponse;
import fr.uga.l3miage.spring.tp3.models.CandidateEntity;
import fr.uga.l3miage.spring.tp3.models.CandidateEvaluationGridEntity;
import fr.uga.l3miage.spring.tp3.models.ExamEntity;
import fr.uga.l3miage.spring.tp3.repositories.CandidateRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
@AutoConfigureTestDatabase
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")
class CandidateControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private CandidateRepository candidateRepository;
    @AfterEach
    public void clear(){
        candidateRepository.deleteAll();
    }


    @Test
    void getCandidateAverageFound() {

        // Given
        HttpHeaders headers = new HttpHeaders();

        CandidateEntity candidate = CandidateEntity.builder()
                .id(1L)
                .email("user@gmail.com")
                .candidateEvaluationGridEntities(new HashSet<>())
                .build();

        CandidateEvaluationGridEntity grid1 = CandidateEvaluationGridEntity.builder()
                .grade(5)
                .examEntity(ExamEntity.builder().weight(1).build())
                .build();

        CandidateEvaluationGridEntity grid2 = CandidateEvaluationGridEntity.builder()
                .grade(10)
                .examEntity(ExamEntity.builder().weight(1).build())
                .build();

        candidate.getCandidateEvaluationGridEntities().add(grid1);
        candidate.getCandidateEvaluationGridEntities().add(grid2);

        candidateRepository.save(candidate);

        // When
        ResponseEntity<String> responseEntity = testRestTemplate.exchange("/api/candidates/{candidateId}/average", HttpMethod.GET, null, String.class, 1L);
        // Then
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
    }


    @Test
    void getCandidateAverageNotFound() {

        // Given
        final Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("candidateId", 9999L);

        CandidatNotFoundResponse expectedResponse = CandidatNotFoundResponse.builder()
                .uri(null)
                .errorMessage(null)
                .candidateId(null)
                .build();

        // When
        ResponseEntity<CandidatNotFoundResponse> response = testRestTemplate.exchange("/api/candidates/{candidateId}/average", HttpMethod.GET, null, CandidatNotFoundResponse.class, urlParams);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectedResponse);
    }

}
