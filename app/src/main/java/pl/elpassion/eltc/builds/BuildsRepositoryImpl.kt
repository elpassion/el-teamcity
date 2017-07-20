package pl.elpassion.eltc.builds

import android.app.Application
import android.preference.PreferenceManager
import com.elpassion.android.commons.sharedpreferences.createSharedPrefs
import com.elpassion.sharedpreferences.moshiadapter.moshiConverterAdapter
import com.squareup.moshi.Types
import pl.elpassion.eltc.Project

class BuildsRepositoryImpl(private val application: Application) : BuildsRepository {

    private val repository = createSharedPrefs<List<Project>>({
        PreferenceManager.getDefaultSharedPreferences(application)
    }, moshiConverterAdapter(type = Types.newParameterizedType(List::class.java, Project::class.java)))

    override var selectedProjects: List<Project>
        get() = repository.read(SELECTED_PROJECTS_KEY) ?: emptyList()
        set(value) {
            repository.write(SELECTED_PROJECTS_KEY, value)
        }

    companion object {
        private const val SELECTED_PROJECTS_KEY = "selected_projects"
    }
}