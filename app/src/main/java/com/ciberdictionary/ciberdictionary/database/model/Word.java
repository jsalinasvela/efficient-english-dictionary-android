package com.ciberdictionary.ciberdictionary.database.model;

/**
 * Created by inmobitec on 6/24/18.
 */

public class Word {
    int id;
    String word_text;
    String type;
    String definition;
    String example;

    public Word(){

    }

    public Word(int id, String word_text, String type, String definition, String example){
        this.id=id;
        this.word_text=word_text;
        this.type=type;
        this.definition=definition;
        this.example=example;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord_text() {
        return word_text;
    }

    public void setWord_text(String word_text) {
        this.word_text = word_text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }
}
