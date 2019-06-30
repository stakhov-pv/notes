package ru.stakhovpv.notes.repos;

import org.springframework.data.repository.CrudRepository;
import ru.stakhovpv.notes.domain.Note;

import java.util.List;

public interface NoteRepository extends CrudRepository<Note,Integer> {
    List<Note> findByHeaderIgnoreCaseContainingOrNoteIgnoreCaseContaining(String header, String note);
}
