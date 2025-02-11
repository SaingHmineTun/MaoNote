package it.saimao.maonote.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import it.saimao.maonote.AppDatabase;
import it.saimao.maonote.R;
import it.saimao.maonote.adapters.NoteAdapter;
import it.saimao.maonote.adapters.NoteItemClickListener;
import it.saimao.maonote.dao.NoteDao;
import it.saimao.maonote.databinding.ActivityMainBinding;
import it.saimao.maonote.entities.Note;

public class MainActivity extends AppCompatActivity implements NoteItemClickListener {

    private ActivityMainBinding binding;
    private NoteDao noteDao;
    private NoteAdapter noteAdapter;
    private List<Note> notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initDatabase();
        initListener();
    }


    private void initDatabase() {
        noteDao = AppDatabase.getInstance(this).getNoteDao();
        noteAdapter = new NoteAdapter();
        noteAdapter.setOnNoteItemClickListener(this);
        binding.rvNotes.setAdapter(noteAdapter);
        binding.rvNotes.setLayoutManager(new LinearLayoutManager(this));
        refreshNotes();
    }

    private void refreshNotes() {

        notes = noteDao.getAllNotes();
        noteAdapter.setNotes(notes);
    }

    private void initListener() {
        binding.fabAddNote.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditNoteActivity.class);
            startActivityForResult(intent, 123);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 123 && data != null) {
                // ADD NEW NOTE

                Note note;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    note = data.getSerializableExtra("note", Note.class);
                } else {
                    note = (Note) data.getSerializableExtra("note");
                }
                noteDao.addNote(note);
                refreshNotes();
                Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show();
            } else if (requestCode == 234) {
                // UPDATE EXISTING NOTE
                if (data != null) {
                    Note note;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        note = data.getSerializableExtra("note", Note.class);
                    } else {
                        note = (Note) data.getSerializableExtra("note");
                    }
                    noteDao.updateNote(note);
                    Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Delete note success!", Toast.LENGTH_SHORT).show();
                }
                refreshNotes();
            }
        }
    }

    @Override
    public void onItemClicked(Note note) {

        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra("note", note);
        startActivityForResult(intent, 234);

    }
}