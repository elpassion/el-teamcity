package pl.elpassion.eltc.ui

import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import kotlinx.android.synthetic.main.select_projects_dialog.*
import pl.elpassion.eltc.Project
import pl.elpassion.eltc.R

class SelectProjectsDialog(private val projects: List<Project>) : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.select_projects_dialog, container)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRadioGroup()
    }

    private fun setupRadioGroup() {
        projects.forEach {
            projectsRadioGroup.addView(RadioButton(activity).apply {
                text = it.name
            })
        }
    }
}