package com.example.twitterclone

import com.google.android.gms.tasks.Task
import com.google.firebase.storage.UploadTask
import io.mockk.every
import io.mockk.mockk


/**
 * It turns out mocking Firebase is a HUGE pain :).
 * These two functions ease that pain somewhat.
 */
inline fun <reified T> mockTask(result: T?, exception: Exception? = null): Task<T> {
    val task: Task<T> = mockk(relaxed = true)
    every { task.isComplete } returns true
    every { task.exception } returns exception
    every { task.isCanceled } returns false
    every { task.result } returns result
    return task
}

fun mockUploadTask(exception: Exception? = null): UploadTask {
    val task: UploadTask = mockk(relaxed = true)
    every { task.isComplete } returns true
    every { task.exception } returns exception
    every { task.isCanceled } returns false
    val snapshot: UploadTask.TaskSnapshot = mockk(relaxed = true)
    every { task.result } returns snapshot
    return task
}
