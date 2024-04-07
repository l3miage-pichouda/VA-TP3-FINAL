package fr.uga.l3miage.spring.tp3.controller;

import fr.uga.l3miage.spring.tp3.endpoints.SessionEndpoints;
import fr.uga.l3miage.spring.tp3.exceptions.technical.NotFoundSessionEntityException;
import fr.uga.l3miage.spring.tp3.request.SessionCreationRequest;
import fr.uga.l3miage.spring.tp3.responses.CandidateEvaluationGridResponse;
import fr.uga.l3miage.spring.tp3.responses.SessionResponse;
import fr.uga.l3miage.spring.tp3.services.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.Set;

@Controller
@RequiredArgsConstructor
public class SessionController implements SessionEndpoints {
    private final SessionService sessionService;

    @Override
    public SessionResponse createSession(SessionCreationRequest request) {
        return sessionService.createSession(request);
    }

    @Override
    public Set<CandidateEvaluationGridResponse> endSessionEvaluation(Long id) {
        return sessionService.endSessionEvaluation(id);
    }
}
