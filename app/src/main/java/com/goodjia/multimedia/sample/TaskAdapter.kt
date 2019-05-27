package com.goodjia.multimedia.sample

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.goodjia.multimedia.Task
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_task.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class TaskAdapter(val tasks: List<Task>?, val onItemClickListener: OnItemClickListener? = null) :
    RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.item_task, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tasks?.size ?: 0
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.setContent(p1, tasks?.get(p1))
    }

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        fun setContent(position: Int, task: Task?) {
            tv.text = "$position"
            tv.onClick {
                onItemClickListener?.onClicked(position, task)
            }
        }
    }

    interface OnItemClickListener {
        fun onClicked(position: Int, task: Task?)
    }
}