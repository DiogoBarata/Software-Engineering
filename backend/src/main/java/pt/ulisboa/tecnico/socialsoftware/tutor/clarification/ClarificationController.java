package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.validation.Valid;
import java.util.List;

import org.slf4j.Logger;

@RestController
public class ClarificationController {
    private static Logger logger = LoggerFactory.getLogger(ClarificationController.class);
    @Autowired
    private ClarificationService clarificationService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("questionAnswer/{questionAnswerId}/clarifications")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionAnswerId, 'QUESTION_ANSWER.ACCESS')")
    public ClarificationDto createClarification(@PathVariable Integer questionAnswerId, @RequestBody ClarificationDto clarificationDto){
        return clarificationService.createClarification(questionAnswerId, clarificationDto);
    }

    @PutMapping("/clarifications/{clarificationId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#clarificationId, 'CLARIFICATION.ACCESS')")
    public ClarificationDto updateClarification(@PathVariable Integer clarificationId, @Valid @RequestBody ClarificationDto clarificationDto){
        logger.debug("clarificationDto {}", clarificationDto);
        return clarificationService.updateClarification(clarificationId, clarificationDto);
    }

    @GetMapping("/clarifications/{clarificationId}/answer")
    @PreAuthorize("(hasRole('ROLE_TEACHER')) and hasPermission(#clarificationId, 'CLARIFICATION.ACCESS')" +
                "or (hasRole('ROLE_STUDENT') and hasPermission(#clarificationId, 'CLARIFICATION.ACCESS'))")
    public ClarificationDto getClarification(@PathVariable int clarificationId){
        return clarificationService.findClarificationById(clarificationId);
    }

    @GetMapping("/clarifications/unanswered")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public List<ClarificationDto> getNotAnsweredClarifications(){
        return clarificationService.getClarificationController();
    }

    @GetMapping("/clarifications")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<ClarificationDto> getClarificationsByUsername(){
        return clarificationService.getClarifications();
    }

    @PutMapping("/clarifications/{clarificationId}/teacher")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity updateClarification(@PathVariable Integer clarificationId, @RequestBody String teacherClarification){
        clarificationService.updateTeacherClarification(clarificationId, teacherClarification);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clarifications/{clarificationId}/delete")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity removeClarification(@PathVariable Integer clarificationId) {
        clarificationService.removeClarification(clarificationId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/clarifications/{clarificationId}/additionalRequest")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ClarificationDto resendClarificationRequest(@PathVariable Integer clarificationId, @RequestBody ClarificationDto clarificationDto) {
        return clarificationService.resendClarificationRequest(clarificationId, clarificationDto);
    }

    @PutMapping("/clarifications/{clarificationId}/additionalResponse")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ClarificationDto resendClarificationResponse(@PathVariable Integer clarificationId, @RequestBody ClarificationDto clarificationDto) {
        return clarificationService.resendClarificationResponse(clarificationId, clarificationDto);
    }

    @PutMapping("/clarifications/{clarificationId}/privacy")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity updatePrivacy(@PathVariable Integer clarificationId, @RequestBody ClarificationDto clarificationDto){
        Clarification.Privacy var;
        if(clarificationDto.getPrivacy() == Clarification.Privacy.PUBLIC){
            var = Clarification.Privacy.PRIVATE;
        }
        else {
            var = Clarification.Privacy.PUBLIC;
        }
        clarificationService.setPrivacyClarification(clarificationId, var);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{quizQuestionId}/public/clarifications")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<ClarificationDto> getClarificationByQuestion(@PathVariable Integer quizQuestionId){
        return clarificationService.getClarificationsByQuestion(quizQuestionId);
    }
}
