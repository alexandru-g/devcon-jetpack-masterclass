package com.softvision.jetpackmasterclass.state

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.softvision.jetpackmasterclass.ui.state.MyState
import com.softvision.jetpackmasterclass.ui.state.MyViewModel
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MyViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    val observer: Observer<MyState> = mockk(relaxed = true)

    val tested = MyViewModel()

    @Before
    fun setup() {
        tested.state.observeForever(observer)
    }

    @Test
    fun testLabel() {

    }
}