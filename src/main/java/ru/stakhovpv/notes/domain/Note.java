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
    private String header;
    private String note;

    public Note() {
    }

    public Note(String header, String note) {
        this.header = header;
        this.note = note;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getHeaderOrNote() {
        if (header !=null && header.trim().length()>0) return header;
        else if (note !=null && note.trim().length()>0) return note;
        return EMPTY_NOTE_NAME;
    }
}
