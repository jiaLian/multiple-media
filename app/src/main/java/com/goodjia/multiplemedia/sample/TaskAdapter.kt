package com.goodjia.multiplemedia.sample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.goodjia.multiplemedia.Task
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_task.*

class TaskAdapter(val tasks: List<Task>?, val onItemClickListener: OnItemClickListener? = null) :
    RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int) =
        ViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.item_task, p0, false))

    override fun getItemCount() = tasks?.size ?: 0

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.setContent(p1, tasks?.get(p1))
    }

    inner class ViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        fun setContent(position: Int, task: Task?) {
            tv.text = "$position"
            tv.setOnClickListener {
                onItemClickListener?.onClicked(position, task)
            }
        }
    }

    interface OnItemClickListener {
        fun onClicked(position: Int, task: Task?)
    }
}