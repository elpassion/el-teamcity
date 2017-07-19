package pl.elpassion.eltc.builds

import pl.elpassion.eltc.Project

data class SelectableProject(val project: Project, var isSelected: Boolean)