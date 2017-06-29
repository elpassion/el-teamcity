@file:Suppress("IllegalIdentifier")

package pl.elpassion.eltc

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TCModelTest {

    val api = mock<TCApi>()
    val model = TCModel(api)
    val observer = TestObserver<AppState>()

    @Before
    fun setup() {
        model.state.subscribe(observer)
    }

    @Test
    fun `Emit NoCredentials at the beginning`() {
        observer.assertValue(NoCredentials)
    }

    @Test
    fun `Display correct error on submitting unknown host`() {
        whenever(api.getBuilds(any())).thenReturn(Single.error(UnknownHostException))
        model.perform(SubmitCredentials("invalid", "user", "pass"))
        observer.assertLastValue(UnknownHost)
    }

    @Test
    fun `Display invalid credentials error on unauthorized call to teamcity api`() {
        whenever(api.getBuilds(any())).thenReturn(Single.error(InvalidCredentialsException))
        model.perform(SubmitCredentials("http://teamcity:8111", "user", "wrong_pass"))
        observer.assertLastValue(InvalidCredentials)
    }

    @Test
    fun `Display build list from api`() {
        val buildList = listOf(
                createBuild(id = 668),
                createBuild(id = 669))
        whenever(api.getBuilds(any())).thenReturn(Single.just(buildList))
        model.perform(SubmitCredentials("http://teamcity:8111", "user", "pass"))
        observer.assertLastValue(Builds(buildList))
    }

    private fun createBuild(id: Int) = Build(
            id = id,
            number = 7,
            status = "SUCCESS",
            state = "finished",
            branchName = "master",
            webUrl = "webUrl",
            statusText = "Tests passed: 1",
            queuedDate = "20170629T104035+0000",
            startDate = "20170629T104035+0000",
            finishDate = "20170629T104035+0000")
}

private fun <T> TestObserver<T>.assertLastValue(value: T) {
    assertEquals(value, values().last())
}
