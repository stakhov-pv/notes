package ru.stakhovpv.notes;

import org.hamcrest.core.IsNot;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.stakhovpv.notes.domain.Note;
import ru.stakhovpv.notes.repos.NoteRepository;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest()
@AutoConfigureMockMvc
public class MainControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MainController mainController;

    @Autowired
    NoteRepository noteRepository;

    @Test
    public void rootPageTest() throws Exception {
        this.mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Notes list")))
                .andExpect(content().string(containsString("Create Note")));
    }

    @Test
    public void addNote1_expectNameInListTest() throws Exception {
        this.mockMvc.perform(post("/add")
                .param("name", "note1name")
                .param("note", "note1text"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("text/html; charset=utf-8"))
                .andExpect(content().string(containsString("note1name")));
    }

    @Test
    public void addNote2WithEmptyName_expectTextInListTest() throws Exception {
        this.mockMvc.perform(post("/add")
                .param("name","")
                .param("note","note2text"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("text/html; charset=utf-8"))
                .andExpect(content().string(containsString("note2text")));
    }

    @Test
    public void addNote3WithEmptyNameAndText_expectEmptyNoteInListTest() throws Exception {
        this.mockMvc.perform(post("/add")
                .param("name","")
                .param("note",""))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("text/html; charset=utf-8"))
                .andExpect(content().string(containsString("Empty note")));
    }

    @Test
    public void addNoteWithNoParam_expectErrorTest() throws Exception {
        this.mockMvc.perform(post("/add"))
                .andDo(print()).andExpect(status().is4xxClientError());
    }

    @Test
    public void findNote_expectNameInListTest() throws Exception {
        //add some notes
        //note4 with search criteria in name
        this.mockMvc.perform(post("/add")
                .param("name", "note4filterName")
                .param("note", "note4Text"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("text/html; charset=utf-8"))
                .andExpect(content().string(containsString("note4filterName")));

        //note5 with search criteria in text
        this.mockMvc.perform(post("/add")
                .param("name", "note5name")
                .param("note", "note5filterText"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("text/html; charset=utf-8"))
                .andExpect(content().string(containsString("note5name")));

        //note6 with no search criteria
        this.mockMvc.perform(post("/add")
                .param("name", "note6name")
                .param("note", "note6text"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("text/html; charset=utf-8"))
                .andExpect(content().string(containsString("note6name")));

        //try to filter with search criteria, and check - note4 and note5 found, note6 is not
        this.mockMvc.perform(post("/filter")
                .param("text", "filter"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("text/html; charset=utf-8"))
                .andExpect(content().string(containsString("note4filterName")))
                .andExpect(content().string(containsString("note5name")))
                .andExpect(content().string(IsNot.not(containsString("note6name"))));
    }

    @Test
    public void findNoteWithNoParam_expectErrorTest() throws Exception {
        this.mockMvc.perform(post("/filter"))
                .andDo(print()).andExpect(status().is4xxClientError());
    }

    @Test
    public void editNote_expectShowEditTemplateTest() throws Exception {
        Note note = noteRepository.save(new Note("note8name","note8text"));

        //update first note and expect that second note unchanged
        this.mockMvc.perform(get("/edit")
                .param("id", note.getId().toString()))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("text/html; charset=utf-8"))
                .andExpect(content().string(containsString("note8name")))
                .andExpect(content().string(containsString("note8text")))
                .andExpect(content().string(containsString("<button type=\"submit\">Save</button>")));
    }

    @Test
    public void editNoteWithInvalidId_expectNoNoteErrorTest() throws Exception {
        //update first note and expect that second note unchanged
        this.mockMvc.perform(get("/edit")
                .param("id", "666"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("text/html; charset=utf-8"))
                .andExpect(content().string(containsString("Error:")))
                .andExpect(content().string(containsString("Invalid edit request: Note not found.")));
    }

    @Test
    public void updateNote_expectChangedNameInListTest() throws Exception {
        //add two notes with same name and text
        Note savedFirstNote = noteRepository.save(new Note("note7name","note7text"));
        Note savedSecondNote = noteRepository.save(new Note("note7name","note7text"));

        //update first note and expect that second note unchanged
        this.mockMvc.perform(post("/update")
                .param("id", savedFirstNote.getId().toString())
                .param("updatedName", "")
                .param("updatedNote", "note7UpdatedText"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("text/html; charset=utf-8"))
                .andExpect(content().string(containsString("note7UpdatedText")))
                .andExpect(content().string(containsString("note7name")));
    }

    @Test
    public void updateNoteWithNoParam_expectErrorTest() throws Exception {
        this.mockMvc.perform(post("/update"))
                .andDo(print()).andExpect(status().is4xxClientError());

    }

    @Test
    public void updateNoteWithInvalidId_expectNoNoteErrorTest() throws Exception {
        //update first note and expect that second note unchanged
        this.mockMvc.perform(post("/update")
                .param("id", "666")
                .param("updatedName", "666")
                .param("updatedNote", "666"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("text/html; charset=utf-8"))
                .andExpect(content().string(containsString("Error:")))
                .andExpect(content().string(containsString("Invalid update request: Note not found.")));
    }

    @Test
    public void deleteNote_expectChangedNameInListTest() throws Exception {
        //add two notes with same name and text
        Note note = noteRepository.save(new Note("note9toDeleteName","note9toDeleteText"));

        //check that note is here
        this.mockMvc.perform(get("/"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("text/html; charset=utf-8"))
                .andExpect(content().string(containsString("note9toDeleteName")));

        //update first note and expect that second note unchanged
        this.mockMvc.perform(get("/delete")
                .param("id", note.getId().toString()))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("text/html; charset=utf-8"))
                .andExpect(content().string(IsNot.not(containsString("note9toDeleteName"))));
    }

    @Test
    public void deleteNoteWithNoParam_expectErrorTest() throws Exception {
        this.mockMvc.perform(get("/delete"))
                .andDo(print()).andExpect(status().is4xxClientError());
    }

    @Test
    public void deleteNoteWithInvalidId_expectNoNoteErrorTest() throws Exception {
        //update first note and expect that second note unchanged
        this.mockMvc.perform(get("/delete")
                .param("id", "666"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("text/html; charset=utf-8"))
                .andExpect(content().string(containsString("Error:")))
                .andExpect(content().string(containsString("Invalid delete request: Note not found.")));
    }


}
