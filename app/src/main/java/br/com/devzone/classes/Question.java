package br.com.devzone.classes;

import java.util.List;

public class Question {
    private String questionText;
    private List<String> answers;
    private String correctAnswer;

    public Question(String questionText, List<String> answers, String correctAnswer) {
        this.questionText = questionText;
        this.answers = answers;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
}
