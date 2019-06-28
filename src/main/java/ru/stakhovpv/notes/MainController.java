package ru.stakhovpv.notes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.stakhovpv.notes.domain.Note;
import ru.stakhovpv.notes.repos.NoteRepository;

import java.util.Map;

@Controller
public class MainController {

    @Autowired
    private NoteRepository noteRepository;

    @GetMapping
    public String main(
            Map<String, Object> model
    ) {
        Iterable<Note> notes = noteRepository.findAll();
        model.put("notes",notes);
        return "main";
    }

    @PostMapping("add")
    public String add(@RequestParam String name, @RequestParam String note, Map<String, Object> model) {
        Note newNote = new Note(name,note);
        noteRepository.save(newNote);

        Iterable<Note> notes = noteRepository.findAll();
        model.put("notes",notes);
        return "main";
    }

    @PostMapping("filter")
    public String filter(@RequestParam String text, Map<String, Object> model) {
        Iterable<Note> findNotes = noteRepository.findByNameIgnoreCaseContainingOrTextIgnoreCaseContaining(text,text);
        model.put("notes",findNotes);
        model.put("foundBy",text);
        return "main";
    }

}