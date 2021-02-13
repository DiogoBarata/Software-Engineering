package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.Clarification
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService
import spock.lang.Specification

@DataJpaTest
class GetClarificationServiceSpockPerformanceTest extends Specification{
    static final String CLARIFICATION_REQUEST = "Request"
    static final String USERNAME = "Username"

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    @Autowired
    ClarificationRepository clarificationRepository

    @Autowired
    ClarificationService clarificationService

    def "performance testing to create 1000 clarifications"(){
        given: "a questionAnswer"
        def questionAnswer = new QuestionAnswer();
        questionAnswerRepository.save(questionAnswer)

        when: "create 1000 clarifications"
        1.upto(2, {
            clarificationRepository.save(new Clarification(CLARIFICATION_REQUEST +it, USERNAME, questionAnswer))
        })

        then:
        true
    };
/*
    def "performance testing to get 1000 clarifications"(){
        given: "a questionAnswer"
        def questionAnswer = new QuestionAnswer();
        questionAnswerRepository.save(questionAnswer)
        and: "a 1000 clarifications"
        1.upto(2, {
            clarificationRepository.save(new Clarification(CLARIFICATION_REQUEST + it, USERNAME, questionAnswer))
        })

        when:
        1.upto(2, { clarificationService.getClarifications()})

        then:
        true
    };*/

    @TestConfiguration
    static class ServiceImplTestContextConfiguration{

        @Bean
        ClarificationService clarificationService() {
            return  new ClarificationService()
        }
    }
}
