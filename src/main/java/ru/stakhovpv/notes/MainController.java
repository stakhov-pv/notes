package ru.stakhovpv.notes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.stakhovpv.notes.domain.Note;
import ru.stakhovpv.notes.repos.NoteRepository;

import java.util.Map;
import java.util.Optional;

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

        return main(model);
    }

    @PostMapping("filter")
    public String filter(@RequestParam String text, Map<String, Object> model) {
        Iterable<Note> findNotes = noteRepository.findByNameIgnoreCaseContainingOrTextIgnoreCaseContaining(text,text);
        model.put("notes",findNotes);
        model.put("foundBy",text);
        return "main";
    }

    @GetMapping("edit")
    public String edit(@RequestParam Integer id, Map<String, Object> model) {
        Optional<Note> checkNote = noteRepository.findById(id);
        if (checkNote.isPresent()) {
            Note note = checkNote.get();
            model.put("id", note.getId());
            model.put("name", note.getName());
            model.put("text", note.getText());
            return "edit";
        } else return "main"; //TODO: error
    }

    @PostMapping("update")
    public String update(@RequestParam Integer id, String updatedName, String updatedNote, Map<String, Object> model) {
        Optional<Note> checkNote = noteRepository.findById(id);
        if (checkNote.isPresent()) {
            Note noteToUpdate = checkNote.get();
            noteToUpdate.setName(updatedName);
            noteToUpdate.setText(updatedNote);
            noteRepository.save(noteToUpdate);
            return main(model);
        } else {
           return "main"; //TODO: error
        }
    }

}