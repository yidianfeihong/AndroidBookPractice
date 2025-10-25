package com.example.beatbox

import com.example.beatbox.bean.Sound
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class SoundViewModelTest {

    private lateinit var sound: Sound
    private lateinit var beatBox: BeatBox
    private lateinit var subject: SoundViewModel

    @Before
    fun setUp() {
        sound = Sound("")
        beatBox = mock(BeatBox::class.java)
        subject = SoundViewModel(beatBox)
        subject.sound = sound
    }


    @Test
    fun exposesSoundNameAsTitle() {
        assertThat(subject.title, `is`(sound.name))
    }

    @Test
    fun callsBeatBoxPlayOnButtonClicked() {
        subject.onButtonClicked()
        verify(beatBox).play(sound)
    }
}