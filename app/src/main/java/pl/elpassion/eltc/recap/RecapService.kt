package pl.elpassion.eltc.recap

import android.app.job.JobParameters
import android.app.job.JobService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pl.elpassion.eltc.DI
import pl.elpassion.eltc.util.SchedulersSupplier
import pl.elpassion.eltc.util.log

class RecapService : JobService() {

    private var parameters: JobParameters? = null

    private val controller by lazy {
        RecapController(
                loginRepository = DI.provideLoginRepository(),
                recapRepository = DI.Recap.provideRepository(),
                api = DI.provideTeamCityApi(),
                notifier = DI.Recap.provideNotifier(),
                onFinish = { jobFinished(parameters, false) },
                schedulers = SchedulersSupplier(
                        backgroundScheduler = Schedulers.io(),
                        uiScheduler = AndroidSchedulers.mainThread()))
    }

    override fun onStartJob(parameters: JobParameters?): Boolean {
        this.parameters = parameters
        controller.onStart()
        log("Recap job started")
        return true
    }

    override fun onStopJob(parameters: JobParameters?): Boolean {
        controller.onStop()
        log("Recap job interrupted")
        return true
    }
}