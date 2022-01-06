package com.example.blind_test.front.models;

import java.util.List;

public class Question {
    private int questionId;
    private String resource;
    private String response;
    private List<String> choices;

    private Question(QuestionBuilder qb){
        this.questionId = qb.questionId;
        this.response = qb.response;
        this.resource = qb.resource;
        this.choices = qb.choices;
    }

    public static class QuestionBuilder{
        private int questionId;
        private String resource;
        private String response;
        private List<String> choices;
        public QuestionBuilder(int questionId){
            this.questionId = questionId;
        }
        public QuestionBuilder resource(String resource){
            this.resource = resource;
            return this;
        }
        public QuestionBuilder response(String response){
            this.response = response;
            return this;
        }
        public QuestionBuilder choices(List<String> choices){
            this.choices = choices;
            return this;
        }
        public Question build(){
            return new Question(this);
        }
    }
}
