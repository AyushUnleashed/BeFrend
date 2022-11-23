package com.ayushunleashed.mitram.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.ayushunleashed.mitram.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class SignInActivity : AppCompatActivity() {

    private val RC_SIGN_IN: Int = 123
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth

    private lateinit var gSignInButton: SignInButton
    private lateinit var btnLoginWithEmail: MaterialButton
    private lateinit var progressBar: ProgressBar
    lateinit var db:FirebaseFirestore

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        updateUI(currentUser)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        setupViews()
        supportActionBar?.hide()
        mAuth = Firebase.auth
        db  = FirebaseFirestore.getInstance()


// Configure Google Sign In
        //Make google Sign in client first,
        //To make google sign in client we need google sign in options so we create that first.

        //gso builder
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("584245718070-rmeqcca7nb9irbe156hu07u2ij6sv2g4.apps.googleusercontent.com")
            .requestEmail()
            .build()

        // using gso to make google sign in client.
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        btnLoginWithEmail = findViewById(R.id.btnLoginWithEmail);

        // when sign in button is clicked , start signIn() function.
        gSignInButton.setOnClickListener {
            Log.d("GENERAL","Clicked on Signin Button")
            signIn()
        }

        btnLoginWithEmail.setOnClickListener{
            Log.d("GENERAL","Clicked on Login with Email Button")
            val intent = Intent(this@SignInActivity, SignUpEmail::class.java)
            startActivity(intent);
        }
    }

    fun setupViews()
    {
        gSignInButton = findViewById(R.id.gSignInButton)
        progressBar = findViewById(R.id.progressBar)

    }

    // to Sign in You use gi
    private fun signIn() {
        //Toast.makeText(this,"Opened Signin",Toast.LENGTH_SHORT).show()
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            //Toast.makeText(this,"On Activity Result",Toast.LENGTH_SHORT).show()
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task);
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        //Toast.makeText(this,"handle Sign In Result",Toast.LENGTH_SHORT).show()
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = completedTask.getResult(ApiException::class.java)!!
            Log.d("TAG", "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            Log.w("TAG", "Google sign in failed", e)
        }

    }

    private fun firebaseAuthWithGoogle(idToken: String) {

        //Toast.makeText(this,"firebaseAuthWithGoogle",Toast.LENGTH_SHORT).show()
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        gSignInButton.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    val user = mAuth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("0TAG", "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(firebaseuser: FirebaseUser?) {
        //Toast.makeText(this,"Updating UI",Toast.LENGTH_SHORT).show()

        if (firebaseuser != null) {

            gSignInButton.visibility = View.GONE
            progressBar.visibility = View.VISIBLE

            val intent = Intent(this@SignInActivity, FragmentHomeActivity::class.java)
            startActivity(intent);
            finish()

        } else {
            gSignInButton.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            //Toast.makeText(this,"Login Failed",Toast.LENGTH_SHORT).show()
        }

    }

}