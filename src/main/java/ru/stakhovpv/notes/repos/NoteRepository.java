package ru.stakhovpv.notes.repos;

import org.springframework.data.repository.CrudRepository;
import ru.stakhovpv.notes.domain.Note;

public interface NoteRepository extends CrudRepository<Note,Integer> {

}
