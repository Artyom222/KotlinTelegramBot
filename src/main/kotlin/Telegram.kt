package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {
    val botToken = args[0]
    val urlGetMe = "https://api.telegram.org/bot$botToken/getMe"
    val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates"

    val client = HttpClient.newBuilder().build()
    val requestGetMe = HttpRequest.newBuilder().uri(URI.create(urlGetMe)).build()
    val requestForUpdates = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
    val responseGetMe = client.send(requestGetMe, HttpResponse.BodyHandlers.ofString())
    val responseForUpdates = client.send(requestForUpdates, HttpResponse.BodyHandlers.ofString())

    println(responseGetMe.body())
    println(responseForUpdates.body())
}