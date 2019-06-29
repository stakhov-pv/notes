package ru.stakhovpv.notes.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Note {
    private final static String EMPTY_NOTE_NAME = "Empty note";

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;
    private String name;
    private String text;

    public Note() {
    }

    public Note(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNameOrText() {
        if (name!=null && name.trim().length()>0) return name;
        else if (text!=null && text.trim().length()>0) return text;
        return EMPTY_NOTE_NAME;
    }
}
