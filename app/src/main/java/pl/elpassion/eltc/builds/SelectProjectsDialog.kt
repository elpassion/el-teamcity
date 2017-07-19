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

    private val selectedProjects = mutableListOf<Project>()

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
                projects, R.layout.project_item, this::bindItem)
        confirmButton.setOnClickListener { onProjectsSelected(selectedProjects); dismiss() }
    }

    private fun bindItem(holder: ViewHolderBinder<SelectableProject>,
                         item: SelectableProject) = with(holder.itemView) {
        projectName.text = item.project.name.let { if (it == "<Root project>") "All projects" else it }
        projectName.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedProjects.add(item.project)
            } else {
                selectedProjects.remove(item.project)
            }
        }
    }
}