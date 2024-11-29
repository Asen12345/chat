package com.ilya.myspb

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform