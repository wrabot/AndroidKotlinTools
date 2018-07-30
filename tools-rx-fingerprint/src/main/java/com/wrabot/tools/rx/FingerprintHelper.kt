/**
 * Copyright (c) 2017-present, Wilfrid Rabot.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package com.wrabot.tools.rx

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties.*
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.support.v4.os.CancellationSignal
import android.util.Base64
import io.reactivex.Observable
import java.nio.charset.Charset
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

/**
 * An helper which cipher or decipher strings with fingerprint.
 * @param context the Android context
 * @param name the internal key name
 */
@Suppress("unused")
class FingerprintHelper(context: Context, private val name: String = "FINGERPRINT") {
    /**
     * The underlying compat helper which can be used to test if hardware is Detected and the device has enrolled fingerprints.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val fingerprintManager: FingerprintManagerCompat = FingerprintManagerCompat.from(context)

    /**
     * An event emitted when ciphering or deciphering with fingerprint.
     */
    sealed class Event {
        data class Error(val error: CharSequence) : Event()
        data class Help(val help: CharSequence) : Event()
        object Failure : Event()
        data class Success(val result: String) : Event()
    }

    /**
     * Cipher a string
     * @param plain the plain string
     * @return Observable<Event> which completes when ciphering finishes
     */
    fun cipher(plain: String) = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) null else
        createCipher(Cipher.ENCRYPT_MODE).run {
            authenticate(plain.toByteArray(), this) {
                "${it.toBase64()} ${iv.toBase64()}"
            }
        }

    /**
     * Decipher a string from previously ciphered string
     * @param cipher the ciphered string
     * @return Observable<Event> which completes when deciphering finishes
     */
    fun decipher(cipher: String?) = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) null else {
        cipher?.let {
            it.split(' ').takeIf { it.size == 2 }?.let {
                authenticate(it[0].fromBase64(), createCipher(Cipher.DECRYPT_MODE, IvParameterSpec(it[1].fromBase64()))) {
                    it.toString(Charset.defaultCharset())
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun authenticate(data: ByteArray, cipher: Cipher, result: (ByteArray) -> String): Observable<Event> {
        val cancellationSignal = CancellationSignal()
        return Observable.create<Event> {
            try {
                val crypto = FingerprintManagerCompat.CryptoObject(cipher)
                val callback = object : FingerprintManagerCompat.AuthenticationCallback() {
                    override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence) = it.onNext(Event.Help(helpString))

                    override fun onAuthenticationFailed() = it.onNext(Event.Failure)

                    override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult) {
                        try {
                            it.onNext(Event.Success(result(result.cryptoObject.cipher!!.doFinal(data))))
                            it.onComplete()
                        } catch (error: Throwable) {
                            it.onError(error)
                        }
                    }

                    override fun onAuthenticationError(errMsgId: Int, errString: CharSequence) {
                        it.onNext(Event.Error(errString))
                        it.onComplete()
                    }
                }
                fingerprintManager.authenticate(crypto, 0, cancellationSignal, callback, null)
            } catch (error: Throwable) {
                it.onError(error)
            }
        }.doOnDispose { cancellationSignal.cancel() }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun createCipher(mode: Int, iv: IvParameterSpec? = null) = Cipher.getInstance("$KEY_ALGORITHM_AES/$BLOCK_MODE_CBC/$ENCRYPTION_PADDING_PKCS7").apply {
        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            init(mode, keyStore.getKey(name, null) as SecretKey, iv)
        } catch (error: Throwable) {
            val keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM_AES, "AndroidKeyStore")
            keyGenerator.init(KeyGenParameterSpec.Builder(name, PURPOSE_ENCRYPT or PURPOSE_DECRYPT)
                    .setBlockModes(BLOCK_MODE_CBC)
                    .setEncryptionPaddings(ENCRYPTION_PADDING_PKCS7)
                    .setUserAuthenticationRequired(true)
                    .build())
            init(mode, keyGenerator.generateKey())
        }
    }

    private fun String.fromBase64() = Base64.decode(this, 0)
    private fun ByteArray.toBase64() = Base64.encodeToString(this, Base64.NO_WRAP or Base64.NO_PADDING)
}
