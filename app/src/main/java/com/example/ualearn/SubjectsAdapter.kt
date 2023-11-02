package com.example.ualearn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class SubjectsAdapter(private val subjects: List<String>) : RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder>() {

    inner class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subjectNameTextView: TextView = itemView.findViewById(R.id.subjectNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.subject_item, parent, false)
        return SubjectViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val currentSubject = subjects[position]
        holder.subjectNameTextView.text = currentSubject
    }

    override fun getItemCount(): Int {
        return subjects.size
    }
}
