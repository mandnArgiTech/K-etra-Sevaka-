package com.ksetrasevakah.core.backup

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.api.services.drive.DriveScopes
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.android.gms.common.api.Scope
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleSignInManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_APPDATA))
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    fun signInIntent(): Intent = googleSignInClient.signInIntent

    fun lastSignedInAccount(): GoogleSignInAccount? =
        GoogleSignIn.getLastSignedInAccount(context)

    fun credentialOrNull(): GoogleAccountCredential? {
        val acc = lastSignedInAccount()?.account ?: return null
        return GoogleAccountCredential.usingOAuth2(
            context,
            listOf(DriveScopes.DRIVE_APPDATA)
        ).apply { selectedAccount = acc }
    }

    fun signOut() {
        googleSignInClient.signOut()
    }
}
