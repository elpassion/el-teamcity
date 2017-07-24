package pl.elpassion.eltc.builds

import android.annotation.SuppressLint
import android.app.DialogFragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elpassion.android.commons.recycler.adapters.basicAdapterWithLayoutAndBinder
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import kotlinx.android.synthetic.main.project_item.view.*
import kotlinx.android.synthetic.main.select_projects_dialog.*
import pl.elpassion.eltc.Project
import pl.elpassion.eltc.R

@SuppressLint("ValidFragment")
class SelectProjectsDialog(private val projects: List<SelectableProject>,
                           private val onProjectsSelected: (List<Project>) -> Unit) : DialogFragment() {

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
        projectsRecyclerView.adapter = basicAdapterWithLayoutAndBinder(
                projects.filterVisibleProjects(), R.layout.project_item, this::bindItem)
        showAllButton.setOnClickListener { applyClearedSelection() }
        confirmButton.setOnClickListener { applySelection() }
    }

    private fun applyClearedSelection() {
        projects.forEach { it.isSelected = false }
        projectsRecyclerView.adapter.notifyDataSetChanged()
        applySelection()
    }

    private fun List<SelectableProject>.filterVisibleProjects() =
            filterNot { it.project.name == "<Root project>" }

    private fun bindItem(holder: ViewHolderBinder<SelectableProject>,
                         item: SelectableProject) = with(holder.itemView) {
        projectName.text = item.project.name
        projectName.isChecked = item.isSelected
        projectName.setOnCheckedChangeListener { _, isChecked ->
            item.isSelected = isChecked
        }
    }

    private fun applySelection() {
        onProjectsSelected(projects.filter { it.isSelected }.map { it.project }.toMutableList())
        dismiss()
    }
}