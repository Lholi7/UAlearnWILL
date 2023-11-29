package com.example.ualearn

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.EditText

class NotesFragment : Fragment() {

    private val databaseReference = FirebaseDatabase.getInstance().getReference("subjects")
    private lateinit var selectedSubject: SubjectModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notes, container, false)

        // Fetch subjects from Firebase
        fetchSubjects()

        // Save button click listener
        val saveButton = view.findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener {
            saveNote()
        }

        return view
    }

    private fun fetchSubjects() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val subjects = mutableListOf<SubjectModel>()

                for (childSnapshot in snapshot.children) {
                    val subject = childSnapshot.getValue(SubjectModel::class.java)
                    subject?.let {
                        subjects.add(it)
                    }
                }

                // Update UI with subjects
                updateSubjectUI(subjects)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })
    }

    private fun updateSubjectUI(subjects: List<SubjectModel>) {
        // Display subjects in a spinner or any other UI element
        val subjectNames = subjects.map { it.subjectName }.toTypedArray()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subjectNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val subjectsSpinner = view?.findViewById<Spinner>(R.id.subjectsSpinner)
        subjectsSpinner?.adapter = adapter

        // Handle subject selection
        subjectsSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                selectedSubject = subjects[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    private fun saveNote() {
        // Retrieve note content from EditText
        val noteEditText = view?.findViewById<EditText>(R.id.noteEditText)
        val noteContent = noteEditText?.text.toString()

        if (noteContent.isNotEmpty()) {
            // Check if a subject is selected
            if (::selectedSubject.isInitialized) {
                // Save the note with the selected subject ID
                val note = NoteModel(selectedSubject.id, noteContent)
                saveNoteToFirebase(note)

                // Optionally, you can clear the EditText after saving
                noteEditText?.text?.clear()

                // Show a message or perform other actions if needed
                Toast.makeText(requireContext(), "Note saved successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please select a subject", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Please enter a note", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveNoteToFirebase(note: NoteModel) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("notes")
        val noteId = databaseReference.push().key
        databaseReference.child(noteId ?: "").setValue(note)
    }
}
