@file:Suppress("IllegalIdentifier")

package pl.elpassion.eltc

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import java.util.*

class TeamCityModelTest {

    val repository = mock<Repository>()
    val api = mock<TeamCityApi>()
    val model = TeamCityModel(api, repository)
    val observer = TestObserver<AppState>()

    @Before
    fun setup() {
        model.state.subscribe(observer)
    }

    @Test
    fun `Emit InitialState before app start`() {
        observer.assertValue(InitialState)
    }

    @Test
    fun `Display correct error on submitting unknown host`() {
        whenever(api.getBuilds(any())).thenReturn(Single.error(UnknownHostException))
        model.perform(SubmitCredentials("invalid", "user:pass"))
        observer.assertLastValue(UnknownHostState)
    }

    @Test
    fun `Display invalid credentials error on unauthorized call to teamcity api`() {
        whenever(api.getBuilds(any())).thenReturn(Single.error(InvalidCredentialsException))
        model.perform(SubmitCredentials("http://teamcity:8111", "user:wrong_pass"))
        observer.assertLastValue(InvalidCredentialsState)
    }

    @Test
    fun `Display build list from api`() {
        val buildList = listOf(
                createBuild(id = 668),
                createBuild(id = 669))
        whenever(api.getBuilds(any())).thenReturn(Single.just(buildList))
        model.perform(SubmitCredentials("http://teamcity:8111", "user:pass"))
        observer.assertLastValue(BuildsState(buildList))
    }
    
    @Test
    fun `Call api with passed credentials`() {
        whenever(api.getBuilds(any())).thenReturn(Single.just(emptyList()))
        model.perform(SubmitCredentials("http://teamcity:8111", "user1:pass1"))
        verify(api).getBuilds(credentials = "user1:pass1")
    }

    @Test
    fun `Save credentials in repository`() {
        whenever(api.getBuilds(any())).thenReturn(Single.just(emptyList()))
        model.perform(SubmitCredentials("http://teamcity:8111", "user:pass"))
        verify(repository).authData = AuthData("http://teamcity:8111", "user:pass")
    }

    @Test
    fun `Start loading if credentials are available in repository on app start`() {
        whenever(repository.authData).thenReturn(AuthData("http://teamcity:8111", "user:pass"))
        model.perform(StartApp)
        observer.assertLastValue(LoadingState)
    }

    private fun createBuild(id: Int) = Build(
            id = id,
            number = 7,
            status = "SUCCESS",
            state = "finished",
            branchName = "master",
            webUrl = "webUrl",
            statusText = "Tests passed: 1",
            queuedDate = Date(),
            startDate = Date(),
            finishDate = Date())
}