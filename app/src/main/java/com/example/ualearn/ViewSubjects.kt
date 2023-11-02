package com.example.ualearn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ViewSubjects : Fragment() {

    private lateinit var subjectsRecyclerView: RecyclerView
    private val subjectsList: MutableList<String> = mutableListOf()
    private lateinit var subjectsAdapter: SubjectsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_subjects, container, false)
        subjectsRecyclerView = view.findViewById(R.id.subjectsRecyclerView)

        subjectsAdapter = SubjectsAdapter(subjectsList)
        subjectsRecyclerView.adapter = subjectsAdapter

        subjectsRecyclerView.layoutManager = LinearLayoutManager(context)

        fetchSubjectsFromFirebase()

        return view
    }

    private fun fetchSubjectsFromFirebase() {
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid

        val subjectsRef = FirebaseDatabase.getInstance().getReference("SUBJECTS/$uid")

        subjectsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                subjectsList.clear()
                for (subjectSnapshot in dataSnapshot.children) {
                    val subjectName = subjectSnapshot.key
                    subjectName?.let {
                        subjectsList.add(it)
                    }
                }

                subjectsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                Toast.makeText(requireContext(), "Failed to retrieve subjects: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
