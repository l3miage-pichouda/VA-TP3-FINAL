package fr.uga.l3miage.spring.tp3.mappers;

import fr.uga.l3miage.spring.tp3.models.ExamEntity;
import fr.uga.l3miage.spring.tp3.responses.ExamResponse;
import org.mapstruct.Mapper;

@Mapper
public interface ExamMapper {

    ExamResponse toResponse(ExamEntity examEntity);

}
