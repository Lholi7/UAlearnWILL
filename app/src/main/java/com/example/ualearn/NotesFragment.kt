package com.example.ualearn

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class NotesFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var subjectTextView: TextView
    private lateinit var noteEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var subjects: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notes, container, false)

        subjectTextView = view.findViewById(R.id.subjectsTextView)
        noteEditText = view.findViewById(R.id.noteEditText)
        saveButton = view.findViewById(R.id.saveButton)

        // Initialize subjects list
        subjects = mutableListOf()

        // Fetch the user's subjects from Firebase
        fetchUserSubjects()

        // Set a listener to display subjects in a dialog
        subjectTextView.setOnClickListener {
            displaySubjectsDialog()
        }

        // Set a listener to save the note to the selected subject
        saveButton.setOnClickListener {
            val selectedSubject = subjectTextView.text.toString()
            val note = noteEditText.text.toString().trim()
            saveNoteToFirebase(selectedSubject, note)
        }

        return view
    }

    private fun fetchUserSubjects() {
        val uid = auth.currentUser?.uid
        val subjectsRef = FirebaseDatabase.getInstance().getReference("SUBJECTS/$uid")

        subjectsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                subjects.clear()
                for (subjectSnapshot in dataSnapshot.children) {
                    val subjectName = subjectSnapshot.key
                    subjectName?.let { subjects.add(it) }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                Toast.makeText(requireContext(), "Failed to retrieve subjects: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displaySubjectsDialog() {
        val subjectNames = subjects.toTypedArray()
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle("Select a Subject")
        dialog.setItems(subjectNames) { _, which ->
            val selectedSubject = subjectNames[which]
            subjectTextView.text = selectedSubject
        }
        dialog.show()
    }

    private fun saveNoteToFirebase(subject: String, note: String) {
        if (note.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter your notes", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = auth.currentUser?.uid
        val notesRef = FirebaseDatabase.getInstance().getReference("Notes/$uid/$subject").push()

        val noteData = HashMap<String, String>()
        noteData["text"] = note

        notesRef.setValue(noteData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Note saved to Firebase", Toast.LENGTH_SHORT).show()
                    noteEditText.text.clear()
                } else {
                    Toast.makeText(requireContext(), "Failed to save note: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to save note: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

