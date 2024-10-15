package com.shadow3.codroid.jni

class NativeInotifyHelper {
    /*
        0: nothing wrong
        1: failed to watch
        2: path don't exist
     */
    external fun waitUntilUpdate(path: String): Int
}
