package pt.ulisboa.tecnico.socialsoftware.tutor.question.service


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOPIC_NAME_IS_NOT_VALID
import spock.lang.Specification
import spock.lang.Unroll

@DataJpaTest
class CreateTopicTest extends Specification {
    public static final String COURSE_NAME = "Arquitetura de Software"
    public static final String NAME = 'name'

    @Autowired
    TopicService topicService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    TopicRepository topicRepository

    def course

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
    }

    def "create a topic"() {
        given:
        def topicDto = new TopicDto()
        topicDto.setName(NAME)

        when:
        topicService.createTopic(course.getId(), topicDto)

        then: "the topic is inside the repository"
        topicRepository.count() == 1L
        def result = topicRepository.findAll().get(0)
        result.getId() != null
        result.getName() == NAME
    }

    @Unroll("invalid arguments: #topic_name || errorMessage")
    def "invalid input"(){
        given: "a topicDto"
        def topicDto = new TopicDto()
        topicDto.setName(topic_name)

        when:
        topicService.createTopic(course.getId(), topicDto)
        then: "throw an exception"
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        topic_name  || errorMessage
        "    "      || TOPIC_NAME_IS_NOT_VALID
        null        || TOPIC_NAME_IS_NOT_VALID
    }


    def "create a topic with the same name"() {
        given: "createQuestion a question"
        Topic topic = new Topic()
        topic.setName(NAME)
        topic.setCourse(course)
        course.addTopic(topic)
        topicRepository.save(topic)
        and: 'topic dto'
        def topicDto = new TopicDto()
        topicDto.setName(NAME)

        when: 'createQuestion another with the same name'
        topicService.createTopic(course.getId(), topicDto)

        then: "an error occurs"
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.DUPLICATE_TOPIC
    }

    @TestConfiguration
    static class QuestionServiceImplTestContextConfiguration {
        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }

        @Bean
        TopicService topicService() {
            return new TopicService()
        }
    }

}
