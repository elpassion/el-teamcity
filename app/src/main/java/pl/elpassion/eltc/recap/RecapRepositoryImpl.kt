package pl.elpassion.eltc.recap

import android.app.Application
import android.preference.PreferenceManager
import com.elpassion.android.commons.sharedpreferences.createSharedPrefs
import com.elpassion.sharedpreferences.moshiadapter.moshiConverterAdapter
import java.util.*

class RecapRepositoryImpl(private val application: Application) : RecapRepository {

    private val repository = createSharedPrefs<Date?>({
        PreferenceManager.getDefaultSharedPreferences(application)
    }, moshiConverterAdapter())

    override var lastFinishDate: Date?
        get() = repository.read(LAST_FINISHED_DATE_KEY)
        set(value) {
            repository.write(LAST_FINISHED_DATE_KEY, value)
        }

    companion object {
        private const val LAST_FINISHED_DATE_KEY = "last_finished_date"
    }
}