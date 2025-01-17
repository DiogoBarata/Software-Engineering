package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(
        name = "questions",
        indexes = {
                @Index(name = "question_indx_0", columnList = "key")
        })
public class Question implements DomainEntity {

    public enum Status {
        DISABLED, REMOVED, AVAILABLE, NONE
    }

    public enum State{
        SUBMITTED, APPROVED, DISAPPROVED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer key;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String title;

    @Column(name = "number_of_answers", columnDefinition = "integer default 0")
    private Integer numberOfAnswers = 0;

    @Column(name = "number_of_correct", columnDefinition = "integer default 0")
    private Integer numberOfCorrect = 0;

    @Enumerated(EnumType.STRING)
    private Status status = Status.NONE;

    @Column(name = "state", columnDefinition = "varchar(32) default 'APPROVED'")
    @Enumerated(EnumType.STRING)
    private State state = State.SUBMITTED;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "question")
    private Image image;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question", fetch = FetchType.EAGER, orphanRemoval=true)
    private List<Option> options = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question", orphanRemoval=true)
    private Set<QuizQuestion> quizQuestions = new HashSet<>();

    @ManyToMany(mappedBy = "questions")
    private Set<Topic> topics = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private String justification;
    private String username;

    public Question() {
    }

    public Question(Course course, QuestionDto questionDto) {

        if (questionDto == null){
            throw new TutorException(QUESTION_NOT_FOUND);
        }
        if (course == null){
            throw new TutorException(COURSE_NOT_FOUND);
        }

        checkConsistentQuestion(questionDto);
        this.title = questionDto.getTitle();
        this.key = questionDto.getKey();
        this.content = questionDto.getContent();
        this.status = Status.valueOf(questionDto.getStatus());
        this.state = State.valueOf(questionDto.getState());
        this.creationDate = LocalDateTime.parse(questionDto.getCreationDate(), Course.formatter);
        this.justification = questionDto.getJustification();

        this.course = course;
        course.addQuestion(this);

        if (questionDto.getImage() != null) {
            Image img = new Image(questionDto.getImage());
            setImage(img);
            img.setQuestion(this);
        }

        int index = 0;
        for (OptionDto optionDto : questionDto.getOptions()) {
            optionDto.setSequence(index++);
            Option option = new Option(optionDto);
            this.options.add(option);
            option.setQuestion(this);
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestion(this);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Option> getOptions() {
        return options;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
        image.setQuestion(this);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<QuizQuestion> getQuizQuestions() {
        return quizQuestions;
    }

    public Integer getNumberOfAnswers() {
        return numberOfAnswers;
    }

    public void setNumberOfAnswers(Integer numberOfAnswers) {
        this.numberOfAnswers = numberOfAnswers;
    }

    public Integer getNumberOfCorrect() {
        return numberOfCorrect;
    }

    public void setNumberOfCorrect(Integer numberOfCorrect) {
        this.numberOfCorrect = numberOfCorrect;
    }

    public Set<Topic> getTopics() {
        return topics;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void addOption(Option option) {
        options.add(option);
    }

    public void addQuizQuestion(QuizQuestion quizQuestion) {
        quizQuestions.add(quizQuestion);
    }

    public void addTopic(Topic topic) {
        topics.add(topic);
    }

    public void remove() {
        canRemove();
        getCourse().getQuestions().remove(this);
        course = null;
        getTopics().forEach(topic -> topic.getQuestions().remove(this));
        getTopics().clear();
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", key=" + key +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", numberOfAnswers=" + numberOfAnswers +
                ", numberOfCorrect=" + numberOfCorrect +
                ", status=" + status +
                ", state=" + state +
                ", image=" + image +
                ", options=" + options +
                ", topics=" + topics +
                ", justification=" + justification + '\'' +
                ", username=" + username + '\'' +
                '}';
    }

    public Integer getCorrectOptionId() {
        return this.getOptions().stream()
                .filter(Option::getCorrect)
                .findAny()
                .map(Option::getId)
                .orElse(null);
    }

    public void addAnswerStatistics(QuestionAnswer questionAnswer) {
        numberOfAnswers++;
        if (questionAnswer.getOption() != null && questionAnswer.getOption().getCorrect()) {
            numberOfCorrect++;
        }
    }


    public Integer getDifficulty() {
        if (numberOfAnswers == 0) {
            return null;
        }

        return numberOfCorrect * 100 / numberOfAnswers;
    }

    public void update(QuestionDto questionDto) {
        checkConsistentQuestion(questionDto);

        setTitle(questionDto.getTitle());
        setContent(questionDto.getContent());
        setUsername(questionDto.getUsername());

        questionDto.getOptions().forEach(optionDto -> {
            Option option = getOptionById(optionDto.getId());
            if (option == null) {
                throw new TutorException(OPTION_NOT_FOUND, optionDto.getId());
            }
            option.setContent(optionDto.getContent());
            option.setCorrect(optionDto.getCorrect());
        });
    }

    private void checkConsistentQuestion(QuestionDto questionDto) {
        if (questionDto.getTitle() == null ||
            questionDto.getTitle().trim().length() == 0 ||
            questionDto.getStatus() == null ||
            questionDto.getState() == null ||
            questionDto.getContent() == null ||
            questionDto.getContent().trim().length() == 0 ||
            questionDto.getOptions().stream().anyMatch(optionDto -> (optionDto.getContent())== null) ||
            questionDto.getOptions().stream().anyMatch(optionDto -> optionDto.getContent().trim().length() == 0)) {
            throw new TutorException(QUESTION_MISSING_DATA);
        }

        if (questionDto.getOptions().stream().filter(OptionDto::getCorrect).count() != 1) {
            throw new TutorException(QUESTION_MULTIPLE_CORRECT_OPTIONS);
        }

        if (!questionDto.getOptions().stream().filter(OptionDto::getCorrect).findAny()
                .equals(getOptions().stream().filter(Option::getCorrect).findAny().map(OptionDto::new))
                && getQuizQuestions().stream().flatMap(quizQuestion -> quizQuestion.getQuestionAnswers().stream())
                .findAny().isPresent()) {
            throw new TutorException(QUESTION_CHANGE_CORRECT_OPTION_HAS_ANSWERS);
        }

    }

    public void updateTopics(Set<Topic> newTopics) {
        Set<Topic> toRemove = this.topics.stream().filter(topic -> !newTopics.contains(topic)).collect(Collectors.toSet());

        toRemove.forEach(topic -> {
            this.topics.remove(topic);
            topic.getQuestions().remove(this);
        });

        newTopics.stream().filter(topic -> !this.topics.contains(topic)).forEach(topic -> {
            this.topics.add(topic);
            topic.getQuestions().add(this);
        });
    }

    private Option getOptionById(Integer id) {
        return getOptions().stream().filter(option -> option.getId().equals(id)).findAny().orElse(null);
    }

    private void canRemove() {
        if (!getQuizQuestions().isEmpty()) {
            throw new TutorException(QUESTION_IS_USED_IN_QUIZ, getQuizQuestions().iterator().next().getQuiz().getTitle());
        }
    }

    public void setOptionsSequence() {
        int index = 0;
        for (Option option: getOptions()) {
            option.setSequence(index++);
        }
    }

    public boolean belongsToAssessment(Assessment chosenAssessment) {
        return chosenAssessment.getTopicConjunctions().stream().map(TopicConjunction::getTopics).collect(Collectors.toList()).contains(this.topics);
    }
}