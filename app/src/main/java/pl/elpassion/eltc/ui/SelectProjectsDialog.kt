package pl.elpassion.eltc.ui

import android.app.DialogFragment
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elpassion.android.commons.recycler.adapters.basicAdapterWithLayoutAndBinder
import com.elpassion.android.commons.recycler.basic.BasicViewHolder
import com.elpassion.android.commons.recycler.basic.asBasicList
import kotlinx.android.synthetic.main.build_item.view.*
import kotlinx.android.synthetic.main.select_projects_dialog.*
import pl.elpassion.eltc.Project
import pl.elpassion.eltc.R

class SelectProjectsDialog(private val projects: List<Project>) : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.select_projects_dialog, container)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        projectsRecyclerView.setHasFixedSize(true)
        projectsRecyclerView.layoutManager = LinearLayoutManager(activity)
        projectsRecyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        projectsRecyclerView.adapter = basicAdapterWithLayoutAndBinder(projects.asBasicList(), R.layout.project_item, this::bindItem)
    }

    private fun bindItem(holder: BasicViewHolder<Project>, item: Project) {
        holder.itemView.projectName.text = item.name
    }
}