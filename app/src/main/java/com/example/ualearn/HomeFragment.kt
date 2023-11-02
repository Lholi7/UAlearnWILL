package com.example.ualearn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class HomeFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Find the "Submit" button and the EditText
        val submitButton = view.findViewById<Button>(R.id.button)
        val subjectEditText = view.findViewById<EditText>(R.id.SubjectName)

        // Set a click listener for the "Submit" button
        submitButton.setOnClickListener {
            // Get the text from the EditText
            val subject = subjectEditText.text.toString().trim()

            // Check if the subject name is not empty
            if (subject.isNotEmpty()) {
                // Save the subject name to Firebase under "SUBJECTS" category
                val uid = auth.currentUser?.uid
                val databaseReference = FirebaseDatabase.getInstance().getReference("SUBJECTS/$uid")
                val timestamp = System.currentTimeMillis()

                databaseReference.child("$timestamp").setValue(subject)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showToast("Subject saved to Firebase")
                        } else {
                            showToast("Failed to save subject: ${task.exception?.message}")
                        }
                    }
                    .addOnFailureListener { e ->
                        showToast("Failed to save subject: ${e.message}")
                    }

                // Clear the EditText after saving
                subjectEditText.text.clear()
            } else {
                showToast("Please enter a subject name")
            }
        }

        return view
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
