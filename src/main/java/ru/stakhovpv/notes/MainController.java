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
    private static final String REQUEST_ADD = "add";
    private static final String REQUEST_FILTER = "filter";
    private static final String REQUEST_EDIT = "edit";
    private static final String REQUEST_UPDATE = "update";
    private static final String REQUEST_DELETE = "delete";

    private static final String KEY_NOTES = "notes";
    private static final String KEY_FOUNDBY = "foundBy";
    private static final String KEY_ID = "id";
    private static final String KEY_HEADER = "header";
    private static final String KEY_NOTE = "note";
    private static final String KEY_ERROR_MESSAGE = "message";

    private static final String TEMPLATE_MAIN = "main";
    private static final String TEMPLATE_EDIT = "edit";
    private static final String TEMPLATE_ERROR = "error";

    private static final String ERROR_MESSAGE_INVALID_UPDATE_REQUEST_NOTE_NOT_FOUND = "Invalid update request: Note not found.";
    private static final String ERROR_MESSAGE_INVALID_EDIT_REQUEST_NOTE_NOT_FOUND = "Invalid edit request: Note not found.";
    private static final String ERROR_MESSAGE_INVALID_DELETE_REQUEST_NOTE_NOT_FOUND = "Invalid delete request: Note not found.";

    @Autowired
    private NoteRepository noteRepository;

    @GetMapping
    public String main(Map<String, Object> model) {
        Iterable<Note> notes = noteRepository.findAll();
        model.put(KEY_NOTES,notes);
        return TEMPLATE_MAIN;
    }

    @PostMapping(REQUEST_ADD)
    public String add(@RequestParam String header, @RequestParam String note, Map<String, Object> model) {
        Note newNote = new Note(header,note);
        noteRepository.save(newNote);
        return main(model);
    }

    @PostMapping(REQUEST_FILTER)
    public String filter(@RequestParam String text, Map<String, Object> model) {
        Iterable<Note> findNotes = noteRepository.findByHeaderIgnoreCaseContainingOrNoteIgnoreCaseContaining(text,text);
        model.put(KEY_NOTES,findNotes);
        model.put(KEY_FOUNDBY,text);
        return TEMPLATE_MAIN;
    }

    @GetMapping(REQUEST_EDIT)
    public String edit(@RequestParam Integer id, Map<String, Object> model) {
        Optional<Note> checkNote = noteRepository.findById(id);
        if (checkNote.isPresent()) {
            Note note = checkNote.get();
            model.put(KEY_ID, note.getId());
            model.put(KEY_HEADER, note.getHeader());
            model.put(KEY_NOTE, note.getNote());
            return TEMPLATE_EDIT;
        } else {
            return errorMessage(ERROR_MESSAGE_INVALID_EDIT_REQUEST_NOTE_NOT_FOUND, model);
        }
    }

    @PostMapping(REQUEST_UPDATE)
    public String update(@RequestParam Integer id, String updatedHeader, String updatedNote, Map<String, Object> model) {
        Optional<Note> checkNote = noteRepository.findById(id);
        if (checkNote.isPresent()) {
            Note noteToUpdate = checkNote.get();
            noteToUpdate.setHeader(updatedHeader);
            noteToUpdate.setNote(updatedNote);
            noteRepository.save(noteToUpdate);
            return main(model);
        } else {
           return errorMessage(ERROR_MESSAGE_INVALID_UPDATE_REQUEST_NOTE_NOT_FOUND, model);
        }
    }

    @GetMapping(REQUEST_DELETE)
    public String delete(@RequestParam Integer id, Map<String, Object> model) {
        Optional<Note> checkNote = noteRepository.findById(id);
        if (checkNote.isPresent()) {
            noteRepository.deleteById(id);
            return main(model);
        } else {
            return errorMessage(ERROR_MESSAGE_INVALID_DELETE_REQUEST_NOTE_NOT_FOUND, model);
        }
    }

    private String errorMessage(String message, Map<String, Object> model) {
        model.put(KEY_ERROR_MESSAGE, message);
        return TEMPLATE_ERROR;
    }

}