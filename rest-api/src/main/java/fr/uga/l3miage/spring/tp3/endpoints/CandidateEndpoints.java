package fr.uga.l3miage.spring.tp3.endpoints;

import fr.uga.l3miage.spring.tp3.exceptions.CandidatNotFoundResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Gestion des candidat")
@RestController
@RequestMapping("/api/candidates")
public interface CandidateEndpoints {


    @Operation(description = "Calculer la moyenne d'un étudiant en fonction de sont ...")
    @ApiResponse(responseCode = "200", description = "La note à pu être calculer")
    @ApiResponse(responseCode = "404", description = "Le candidat n'a pas été trouvé" , content = @Content(schema = @Schema(implementation = CandidatNotFoundResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{candidateId}/average")
    Double getCandidateAverage(@PathVariable Long candidateId);

    @Operation(description = "Ajouter une collection d'étudiants à un centre de test")
    @ApiResponse(responseCode = "202", description = "Étudiants ajoutés avec succès")
    @ApiResponse(responseCode = "404", description = "Centre de test ou étudiant non trouvé", content = @Content(schema = @Schema(implementation = CandidatNotFoundResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/addStudents")
    Boolean addStudentsToTestCenter(@RequestParam Long testCenterId, @RequestBody List<Long> studentIds);
}
