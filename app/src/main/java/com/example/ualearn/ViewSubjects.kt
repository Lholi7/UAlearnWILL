package com.example.ualearn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*

class ViewSubjects : Fragment() {

    lateinit var subjectsRecyclerView: RecyclerView
    lateinit var subjectsReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_subjects, container, false)

        subjectsRecyclerView = view.findViewById(R.id.subjectsRecyclerView)
        subjectsRecyclerView.layoutManager = LinearLayoutManager(requireActivity())

        subjectsReference = FirebaseDatabase.getInstance().getReference("subjects")

        setupRecyclerView()

        return view
    }

    private fun setupRecyclerView() {
        val options = FirebaseRecyclerOptions.Builder<SubjectModel>()
            .setQuery(subjectsReference, SubjectModel::class.java)
            .build()

        val adapter = object : FirebaseRecyclerAdapter<SubjectModel, SubjectViewHolder>(options) {
            override fun onBindViewHolder(holder: SubjectViewHolder, position: Int, model: SubjectModel) {
                holder.setSubjectName(model.subjectName)

                // Pass the subject ID to the NotesFragment when a subject is clicked
                holder.itemView.setOnClickListener {
                    val subjectId = getRef(position).key
                    // Navigate to the NotesFragment and pass the subject ID
                    // You can use a ViewModel or any other approach to communicate between fragments
                    // For simplicity, I'm assuming you're using Navigation Component
                    //  val action: NavDirections = ViewSubjectsDirections.actionViewSubjectsToNotesFragment(subjectId)
                    //   findNavController().navigate(action)
                }

                // Add listener for the delete button
                holder.deleteBtn.setOnClickListener {
                    // Get the subject ID to be deleted
                    val subjectId = getRef(position).key
                    showDeleteConfirmationDialog(subjectId)
                }
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.subject_item, parent, false)
                return SubjectViewHolder(view)
            }
        }

        subjectsRecyclerView.adapter = adapter
        adapter.startListening()
    }

    private fun showDeleteConfirmationDialog(subjectId: String?) {
        subjectId?.let {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Delete Subject")
            builder.setMessage("Are you sure you want to delete this subject?")
            builder.setPositiveButton("Yes") { _, _ ->
                deleteSubject(it)
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }
    }

    private fun deleteSubject(subjectId: String) {
        subjectsReference.child(subjectId).removeValue()
    }
}
