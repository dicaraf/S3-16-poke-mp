package model.environment

import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

/**
  * Audio represents a music in the game
  */
trait Audio {
  /**
    * Plays an audio
    */
  def play(): Unit

  /**
    * Pauses an audio
    */
  def pause(): Unit

  /**
    * Stops an audio
    */
  def stop(): Unit

  /**
    * Plays an audio in loop
    */
  def loop(): Unit
}

object Audio{
  def apply(song: String): Audio = new AudioImpl(song)
}

/**
  * @inheritdoc
  * @param song song to play
  */
class AudioImpl(private val song: String) extends  Audio{

  private val audio: AudioInputStream = AudioSystem getAudioInputStream getClass.getResource(song)
  private val clip: Clip = AudioSystem.getClip
  clip open audio

  /**
    * @inheritdoc
    */
  override def play(): Unit = clip.start()

  /**
    * @inheritdoc
    */
  override def pause(): Unit = {
    clip.stop()
    clip setMicrosecondPosition 0
  }

  /**
    * @inheritdoc
    */
  override def stop(): Unit = clip.close()

  /**
    * @inheritdoc
    */
  override def loop(): Unit = clip loop Clip.LOOP_CONTINUOUSLY
}