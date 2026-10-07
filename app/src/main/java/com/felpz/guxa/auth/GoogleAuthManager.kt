package com.felpz.guxa.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.felpz.guxa.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

data class GuxaUser(
    val idToken: String,
    val displayName: String?,
    val email: String?,
    val avatarUrl: String?
)

class GoogleAuthManager(context: Context) {
    private val credentialManager = CredentialManager.create(context)

    suspend fun signIn(context: Context): Result<GuxaUser> = runCatching {
        require(BuildConfig.GOOGLE_WEB_CLIENT_ID != "REPLACE_WITH_GOOGLE_WEB_CLIENT_ID") {
            "Google Login ainda não está configurado. Defina GUXA_GOOGLE_WEB_CLIENT_ID no build."
        }

        val option = GetGoogleIdOption.Builder()
            .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
            .setFilterByAuthorizedAccounts(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()

        val result = credentialManager.getCredential(context, request)
        val credential = result.credential

        require(
            credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) { "A credencial retornada não é uma credencial Google válida." }

        val googleCredential = try {
            GoogleIdTokenCredential.createFrom(credential.data)
        } catch (error: GoogleIdTokenParsingException) {
            throw IllegalStateException("Não foi possível interpretar o token Google.", error)
        }

        GuxaUser(
            idToken = googleCredential.idToken,
            displayName = googleCredential.displayName,
            email = googleCredential.id,
            avatarUrl = googleCredential.profilePictureUri?.toString()
        )
    }
}
