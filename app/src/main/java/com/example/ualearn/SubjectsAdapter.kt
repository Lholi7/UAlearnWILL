package com.example.ualearn

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.util.*

class SubjectsAdapter(var context: Context, var subjects: List<String>) :
    RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder>(), Filterable {

    private var filter: FilterSubjects = FilterSubjects(this)
    private var subjectsListFull: List<String> = ArrayList(subjects)

    inner class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subjectNameTextView: TextView = itemView.findViewById(R.id.subjectNameTextView)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.deleteBtn)

        init {
            // Set up an OnClickListener for the subject item
            itemView.setOnClickListener {
                val subjectName = subjects[adapterPosition]

                // Start ViewTopics activity with the selected subjectName
                val intent = Intent(context, ViewTopics::class.java)
                intent.putExtra("subjectName", subjectName)
                context.startActivity(intent)
            }

            // Set up an OnClickListener for the delete button
            deleteBtn.setOnClickListener {
                // Handle the delete button click event
                showDeleteConfirmationDialog(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.subject_item, parent, false)
        return SubjectViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val currentSubject = subjects[position]
        holder.subjectNameTextView.text = currentSubject
    }

    override fun getItemCount(): Int {
        return subjects.size
    }

    override fun getFilter(): Filter {
        return filter
    }

    private fun showDeleteConfirmationDialog(position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Subject")
        builder.setMessage("Are you sure you want to delete this subject?")
        builder.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
            deleteSubject(position)
        }
        builder.setNegativeButton("No") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun deleteSubject(position: Int) {
        val deletedSubject = subjectsListFull[position]

        val subjectsReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("subjects")

        // Find the subjectId of the subject to be deleted
        subjectsReference.orderByChild("subjectName").equalTo(deletedSubject)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val subjectId = dataSnapshot.children.first().key

                        // Remove the subject from the subjects reference
                        subjectsReference.child(subjectId!!).removeValue()

                        // Notify the adapter about the item removal
                        subjectsListFull = subjectsListFull.filter { it != deletedSubject }
                        subjects = ArrayList(subjectsListFull)
                        notifyItemRemoved(position)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error
                }
            })
    }
}
