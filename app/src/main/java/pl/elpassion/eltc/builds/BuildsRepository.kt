package pl.elpassion.eltc.builds

import pl.elpassion.eltc.Project

interface BuildsRepository {
    var selectedProjects: List<Project>
}