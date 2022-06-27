package com.ayushunleashed.mitram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.asynctaskcoffee.cardstack.CardContainer
import com.asynctaskcoffee.cardstack.CardListener
import com.ayushunleashed.mitram.models.UserModel
import com.ayushunleashed.notezen.daos.UserDao
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class FragmentHomeActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView

    var db =  FirebaseFirestore.getInstance()
    var currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser


    override fun onResume() {
        super.onResume()
        db.collection("users").document(currentUser!!.uid).update("isOnline",true)
    }

    override fun onPause() {
        super.onPause()
        db.collection("users").document(currentUser!!.uid).update("isOnline",false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_home)

        supportActionBar?.hide()

        setupNav()
        getTokenFun()
    }





    fun getTokenFun(){

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            updateToken(it)
        }
    }

    fun updateToken(token:String)
    {
        currentUser = FirebaseAuth.getInstance().currentUser!!
        db  = FirebaseFirestore.getInstance()

        GlobalScope.launch {

            var currentUserModel = db.collection("users").document(currentUser!!.uid).get().await().toObject(
                UserModel::class.java)
            currentUserModel?.fcmToken = token

            db.collection("users").document(currentUser!!.uid).set(currentUserModel!!, SetOptions.merge())
        }
    }


    private fun setupNav()
    {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)

        // hides bottom navigation for other fragments
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.discoverFragment -> showBottomNav()
                R.id.connectionsFragment -> showBottomNav()
                R.id.likesFragment -> showBottomNav()
                R.id.profileFragment -> showBottomNav()
                R.id.messagesFragment -> showBottomNav()
                else -> hideBottomNav()
            }
        }
    }

    private fun showBottomNav() {
        bottomNavigationView.visibility = View.VISIBLE

    }

    private fun hideBottomNav() {
        bottomNavigationView.visibility = View.GONE

    }
}