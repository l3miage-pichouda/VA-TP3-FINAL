package fr.uga.l3miage.spring.tp3.components;

import fr.uga.l3miage.spring.tp3.components.ExamComponent;
import fr.uga.l3miage.spring.tp3.exceptions.technical.ExamNotFoundException;
import fr.uga.l3miage.spring.tp3.models.ExamEntity;
import fr.uga.l3miage.spring.tp3.models.SkillEntity;
import fr.uga.l3miage.spring.tp3.repositories.ExamRepository;
import fr.uga.l3miage.spring.tp3.repositories.SkillRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class ExamComponentTest {
    @Autowired
    private ExamComponent examComponent;

    @MockBean
    private SkillRepository skillRepository;

    @MockBean
    private ExamRepository examRepository;

    @Test
    void getAllCardioExam() {
        SkillEntity skillEntity = new SkillEntity();
        skillEntity.setName("cardio");

        when(skillRepository.findByNameLike("cardio")).thenReturn(Optional.of(skillEntity));
        when(examRepository.findAllBySkillEntitiesContaining(skillEntity)).thenReturn(new HashSet<>());

        Set<ExamEntity> examEntities = examComponent.getAllCardioExam();

        assertThat(examEntities).isEmpty();
    }

    @Test
    void getAllById() throws ExamNotFoundException {
        Set<Long> examIds = new HashSet<>();
        examIds.add(1L);

        ExamEntity examEntity = new ExamEntity();
        examEntity.setId(1L);

        when(examRepository.findAllById(examIds)).thenReturn(List.of(examEntity));

        Set<ExamEntity> examEntities = examComponent.getAllById(examIds);

        assertThat(examEntities).containsExactly(examEntity);
    }

    @Test
    void getAllById_notFound() {
        Set<Long> examIds = new HashSet<>();
        examIds.add(1L);

        when(examRepository.findAllById(examIds)).thenReturn(List.of());

        assertThatThrownBy(() -> examComponent.getAllById(examIds))
                .isInstanceOf(ExamNotFoundException.class)
                .hasMessage("Un exam n'a pas été trouvé");
    }
}
