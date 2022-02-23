package com.java1337.work.jobcoin.app.util

object Closeable {

    def autoClose[C <: AutoCloseable, T](closeable:C)(f: C => T):T = {
        try {
            f(closeable)
        } finally {
            closeable.close()
        }
    }
}
