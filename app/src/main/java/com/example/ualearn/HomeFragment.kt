package com.example.ualearn

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.ualearn.SubjectModel
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class HomeFragment : Fragment() {

    lateinit var subjectNameEditText: EditText
    lateinit var submitButton: Button
    lateinit var subjectsReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        subjectNameEditText = view.findViewById(R.id.SubjectName)
        submitButton = view.findViewById(R.id.button)

        subjectsReference = FirebaseDatabase.getInstance().getReference("subjects")

        submitButton.setOnClickListener {
            addSubjectToFirebase()
        }

        return view
    }

    // Inside your addSubjectToFirebase() method
    private fun addSubjectToFirebase() {
        val subjectName = subjectNameEditText.text.toString().trim()

        if (TextUtils.isEmpty(subjectName)) {
            Toast.makeText(requireActivity(), "Please enter a subject name", Toast.LENGTH_SHORT).show()
            return
        }

        val subjectKey = subjectsReference.push().key!!
        val subject = SubjectModel(subjectKey, subjectName)
        subjectsReference.child(subjectKey).setValue(subject)

        subjectNameEditText.text.clear()
    }

}
