package com.ayushunleashed.mitram.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.asynctaskcoffee.cardstack.CardContainer
import androidx.lifecycle.ViewModelProvider
import com.asynctaskcoffee.cardstack.CardListener
import com.asynctaskcoffee.cardstack.pulse
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.SharedViewModel
import com.ayushunleashed.mitram.adapters.UserCardAdapter
import com.ayushunleashed.mitram.models.UserModel
import com.ayushunleashed.mitram.daos.UserDao
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


class DiscoverFragment : Fragment() ,CardListener{
    lateinit var thisContext: Context

   lateinit var sharedViewModel:SharedViewModel

    lateinit var userCardAdapter: UserCardAdapter
    lateinit var usersList:MutableList<UserModel>

    lateinit var currentUser: FirebaseUser
    lateinit var db:FirebaseFirestore
    lateinit var currentUserModel:UserModel

    lateinit var cardContainer: CardContainer
    lateinit var userImage:ImageView
    lateinit var btnReloadDiscoverUsers: FloatingActionButton
    lateinit var btnRightSwipe:FloatingActionButton
    lateinit var btnLeftSwipe:FloatingActionButton
    lateinit var tvNoUsersToShow:TextView
    private lateinit var progressBar: ProgressBar

    lateinit var myView:View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (container != null) {
            thisContext = container.getContext()
        };

        myView = inflater.inflate(R.layout.fragment_discover, container, false)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        setupViews(myView)
        setupButtons()

        /*Customization*/
        cardContainer.maxStackSize = 2
        cardContainer.setOnCardActionListener(this)
        currentUser = FirebaseAuth.getInstance().currentUser!!
        db  = FirebaseFirestore.getInstance()

        getCurrentUser()
        return myView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadCurrentUserImage()
        if(sharedViewModel.loadedDiscoverFragmentBefore)
        {
            Log.d("SwipeLog","sharedViewModel.isUsersPresentForDiscoverFragment:${sharedViewModel.isUsersPresentForDiscoverFragment}")
            Log.d("SwipeLog","sharedViewModel.myUsersList.size:${sharedViewModel.myUsersList.size}")

            if(sharedViewModel.isUsersPresentForDiscoverFragment && sharedViewModel.myUsersList.size!=0)
            {
                loadUsersForDiscoverPageOrignal()
            }

            for(i in 0 until sharedViewModel.numOfSwipes)
            {
                sharedViewModel.myUsersList.removeAt(0)
            }
            sharedViewModel.numOfSwipes=0



            Log.d("SwipeLog","sharedViewModel.myUsersList:${sharedViewModel.myUsersList.size}")

            for(i in 0 until (sharedViewModel.myUsersList.size) )
            {
                Log.d("SwipeLog","user[$i]: ${sharedViewModel.myUsersList[i].displayName}")
            }
            //var count = sharedViewModel.discoverCardPosition
            setDiscoverPageAdapter()

            // No users to show
            if(sharedViewModel.myUsersList.size == 0)
            {
                tvNoUsersToShow.visibility = View.VISIBLE
                btnRightSwipe.visibility = View.GONE
                btnLeftSwipe.visibility = View.GONE
            }
            else{
                tvNoUsersToShow.visibility = View.GONE
                btnRightSwipe.visibility = View.VISIBLE
                btnLeftSwipe.visibility = View.VISIBLE
            }

        }
        else{ //first time app launches
            loadUsersForDiscoverPageOrignal()
        }

    }


    fun getCurrentUser()
    {
        val userDao = UserDao()
        runBlocking {
            currentUserModel =userDao.getUserById(currentUser!!.uid).await().toObject(UserModel::class.java)!!
            sharedViewModel.currentUserModel = currentUserModel
        }
    }

    fun setupButtons()
    {
        btnReloadDiscoverUsers.setOnClickListener{
            handleReloadUsersButton()
        }

        btnRightSwipe.setOnClickListener{
            it.pulse() // for sweet animation
            userCardAdapter.swipeRight()

        }
        btnLeftSwipe.setOnClickListener {
            it.pulse() // for sweet animation
            userCardAdapter.swipeLeft()
        }

    }

    fun loadCurrentUserImage()
    {
        Glide.with(userImage.context).load(currentUserModel.imageUrl).circleCrop().placeholder(R.drawable.img_user_place_holder)
            .error(R.drawable.img_user_profile_sample).into(userImage)
    }

    fun loadUsersForDiscoverPageOrignal()
    {
        sharedViewModel.loadedDiscoverFragmentBefore = true
        progressBar.visibility = View.VISIBLE

        GlobalScope.launch(Dispatchers.IO){

            // get current user model from database
            val currentUserModel = db.collection("users").document(currentUser.uid).get().await().toObject(UserModel::class.java)
            val usersYouLiked = currentUserModel!!.usersYouLiked
            val connections = currentUserModel.connections
            val combinedArray = usersYouLiked + connections

            //remove all users you already liked or are in connections
            // get all users

            val allUsers = db.collection("users").get().await().toObjects(UserModel::class.java)

            val arrayOfPeopleYouDontWant = mutableListOf<UserModel>()
            for( uid in combinedArray)
            {
                val eachUserNotToInclude = db.collection("users").document(uid).get().await().toObject(UserModel::class.java)
                if (eachUserNotToInclude != null) {
                    arrayOfPeopleYouDontWant.add(eachUserNotToInclude)
                }
            }

            usersList = (allUsers - arrayOfPeopleYouDontWant - currentUserModel) as MutableList<UserModel>
            sharedViewModel.myUsersList = usersList
            sharedViewModel.isUsersPresentForDiscoverFragment = if(usersList.size==0){
                false
            }else {true}




            // updating ui with users
            withContext(Dispatchers.Main)
            {
                progressBar.visibility = View.GONE
                if(usersList.size == 0)
                {
                    tvNoUsersToShow.visibility = View.VISIBLE
                    btnRightSwipe.visibility = View.GONE
                    btnLeftSwipe.visibility = View.GONE
                }
                else{
                    tvNoUsersToShow.visibility = View.GONE
                    btnRightSwipe.visibility = View.VISIBLE
                    btnLeftSwipe.visibility = View.VISIBLE
                }
                setDiscoverPageAdapter()
                Log.d("GENERAL","${sharedViewModel.myUsersList.size} users in Discover");
                for(user in sharedViewModel.myUsersList)
                {
                    Log.d("GENERAL","**user:"+user.displayName.toString());
                }
            }
        }
    }

    fun setDiscoverPageAdapter()
    {
        userCardAdapter = UserCardAdapter(sharedViewModel.myUsersList,thisContext)
        cardContainer.setAdapter(userCardAdapter)
    }

    /*fun loadUsersForDiscoverPage()
    {
        progressBar.visibility = View.VISIBLE

        GlobalScope.launch(Dispatchers.IO){

            // get current user model from database
            val currentUserModel = db.collection("users").document(currentUser.uid).get().await().toObject(UserModel::class.java)
            val usersYouLiked = currentUserModel!!.usersYouLiked
            val connections = currentUserModel.connections
            val combinedArray = usersYouLiked + connections


            for(uid in combinedArray)
            {
                val query = db.collection("users").whereNotIn("uid",combinedArray)
                usersList = query.get().await().toObjects(UserModel::class.java)
            }


            // updating ui with users
            withContext(Dispatchers.Main)
            {
                progressBar.visibility = View.GONE
                if(usersList.size == 0)
                {
                    tvNoUsersToShow.visibility = View.VISIBLE
                    btnRightSwipe.visibility = View.GONE
                    btnLeftSwipe.visibility = View.GONE
                    //Toast.makeText(requireContext(),"No Users To Show",Toast.LENGTH_SHORT).show()
                }
                else{
                    tvNoUsersToShow.visibility = View.GONE
                    btnRightSwipe.visibility = View.VISIBLE
                    btnLeftSwipe.visibility = View.VISIBLE
                }


                Log.d("GENERAL","${usersList.size} users in Discover");
                for(user in usersList)
                {
                    Log.d("GENERAL","**user:"+user.displayName.toString());
                }

                userCardAdapter = UserCardAdapter(usersList,thisContext)
                cardContainer.setAdapter(userCardAdapter)
            }
        }
    }*/

    fun setupViews(view: View)
    {
        progressBar = view.findViewById(R.id.progressBar)
        tvNoUsersToShow = view.findViewById(R.id.tvNoUsers)
        btnLeftSwipe = view.findViewById(R.id.btnLeftSwipe)
        btnRightSwipe = view.findViewById(R.id.btnRightSwipe)
        cardContainer = view.findViewById(R.id.cardContainer)
        userImage = view.findViewById(R.id.userImage)
        btnReloadDiscoverUsers = view.findViewById<FloatingActionButton>(R.id.btnReloadDiscoverUsers)
    }

    fun defaultProfiles():MutableList<UserModel>
    {
        var usersList = mutableListOf<UserModel>(
            UserModel("001","Ayush Yadav","https://via.placeholder.com/300","Def"),
            UserModel("002","Khushboo pratap singh","https://via.placeholder.com/300","Not Def"),
            UserModel("003","Yuvraj Singh","https://via.placeholder.com/300","depressed"),
            UserModel("004","Ramraj Gopal","https://via.placeholder.com/300","I  am legned"),
            UserModel("005","Kishor Kumar Singh","https://via.placeholder.com/300","Kishor OP"),
            UserModel("006","Kumar Shrivastava","https://via.placeholder.com/300","hi buddy"),
            UserModel("007","Rob Stark","https://via.placeholder.com/300","kuch bhi P"),
            UserModel("008","John snow","https://via.placeholder.com/300","John hun me")
        )

        return usersList;
    }

    override fun onLeftSwipe(position: Int, model: Any) {

        //sharedViewModel.myUsersList.removeAt(position)
        val model:UserModel = model as UserModel
        val displayName = model.displayName.toString()
        Toast.makeText(thisContext,"Left Swiped $displayName ",Toast.LENGTH_SHORT).show()

        // increasing number of swipes, later on we will remove users according to this, on fragment reload
        sharedViewModel.numOfSwipes++;

        Log.e(
            "SwipeLog",
            "onLeftSwipe pos: $position model: " + (model as UserModel).displayName.toString()
        )

    }

    override fun onRightSwipe(position: Int, model: Any) {

        sharedViewModel.numOfSwipes++;
        Log.e(
            "SwipeLog",
            "onRightSwipe pos: $position model: " + (model as UserModel).toString()
        )

        val userWhoGotRightSwiped:UserModel = model as UserModel

        // for matching
         GlobalScope.launch(Dispatchers.IO) {

            if(currentUserModel!!.likedBy.contains(userWhoGotRightSwiped.uid) || userWhoGotRightSwiped.usersYouLiked.contains(currentUserModel!!.uid))
            {
                // if you already have the person you liked on in your likes list

                // remove him from your like list
                currentUserModel!!.likedBy.remove(userWhoGotRightSwiped.uid)

                userWhoGotRightSwiped.usersYouLiked.remove(currentUserModel!!.uid)

                //precautions
                currentUserModel!!.usersYouLiked.remove(userWhoGotRightSwiped.uid)
                userWhoGotRightSwiped.likedBy.remove(currentUserModel!!.uid)


                // add him to your connection list
                currentUserModel!!.connections.add(userWhoGotRightSwiped.uid!!)

                // add yourself to his connection list
                userWhoGotRightSwiped.connections.add(currentUserModel!!.uid!!)

                //updating this to database
                // current user's like list and connections list both are updated
                db.collection("users").document(currentUser.uid).set(currentUserModel!!).await()
                // request user's connection list is updated
                db.collection("users").document(userWhoGotRightSwiped.uid).set(userWhoGotRightSwiped).await()

                withContext(Dispatchers.Main)
                {
                    Toast.makeText(thisContext,"It's a match",Toast.LENGTH_SHORT).show()
                }
            }
        }

        if(!userWhoGotRightSwiped.likedBy.contains(currentUser.uid) && !userWhoGotRightSwiped.connections.contains(currentUser.uid))
        {

            userWhoGotRightSwiped.likedBy.add(currentUser.uid)

            Log.e(
                "SwipeLog",
                "onRightSwipe pos: $position model: " + (model as UserModel).toString()
            )

            GlobalScope.launch(Dispatchers.IO) {
                db.collection("users").document(userWhoGotRightSwiped.uid!!).set(userWhoGotRightSwiped).
                addOnSuccessListener {
                    Toast.makeText(thisContext,"Like request Sent", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(thisContext,"Failed To sent like request", Toast.LENGTH_SHORT).show()
                }

                //adding this person you swiped right on to users you liked
                //val currentUserModel: UserModel? = db.collection("users").document(currentUser.uid).get().await().toObject(UserModel::class.java)
                currentUserModel!!.usersYouLiked.add(userWhoGotRightSwiped.uid)
                db.collection("users").document(currentUserModel!!.uid!!).set(currentUserModel!!).await()
            }
        }
        else
        {
            Toast.makeText(thisContext,"Already Exist not adding",Toast.LENGTH_SHORT).show()
        }

    }

    override fun onItemShow(position: Int, model: Any) {
        //sharedViewModel.discoverCardPosition = position
        Log.e("SwipeLog", "onItemShow pos: $position model: " + (model as UserModel).displayName.toString())
    }

    override fun onSwipeCancel(position: Int, model: Any) {
        Log.e(
            "SwipeLog",
            "onSwipeCancel pos: $position model: " + (model as UserModel).displayName.toString()
        )
    }

    override fun onSwipeCompleted() {
        Log.e(
            "SwipeLog",
            "Out of swipe data"
        )

        btnReloadDiscoverUsers.visibility = View.VISIBLE
        btnLeftSwipe.visibility = View.GONE
        btnRightSwipe.visibility = View.GONE
    }

    fun handleReloadUsersButton()
    {
        sharedViewModel.myUsersList.clear()
        sharedViewModel.numOfSwipes = 0
        loadUsersForDiscoverPageOrignal()
        //usersList = defaultProfiles()
        userCardAdapter = UserCardAdapter(usersList,thisContext)
        cardContainer.setAdapter(userCardAdapter)
        btnReloadDiscoverUsers.visibility = View.GONE
        btnLeftSwipe.visibility = View.VISIBLE
        btnRightSwipe.visibility = View.VISIBLE
    }


}