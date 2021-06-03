package com.mdm.app.extension

import java.math.BigInteger
import java.security.MessageDigest

fun getSHA512(str:String):String{
    val md: MessageDigest = MessageDigest.getInstance("SHA-512")
    val messageDigest = md.digest(str.toByteArray())

    val no = BigInteger(1, messageDigest)

    var hashtext: String = no.toString(16)

    while (hashtext.length < 32) {
        hashtext = "0$hashtext"
    }

    return hashtext
}