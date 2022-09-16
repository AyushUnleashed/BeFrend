package com.ayushunleashed.mitram.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.SharedViewModel
import com.ayushunleashed.mitram.adapters.LikeRequestsSentCardAdapter
import com.ayushunleashed.mitram.databinding.FragmentLikeRequestsSentBinding
import com.ayushunleashed.mitram.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


class LikeRequestsSentFragment : Fragment() {

    private lateinit var usersList:MutableList<UserModel>
    lateinit var thisContext: Context
    private lateinit var currentUser:FirebaseUser
    lateinit var sharedViewModel: SharedViewModel
    lateinit var tvNoUsersToShow: TextView
    private lateinit var binding: FragmentLikeRequestsSentBinding
    lateinit var recyclerView:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            // Handle the back button event
            findNavController().navigate(R.id.action_likeRequestsSentFragment_to_likesFragment)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (container != null) {
            thisContext = container.context
        };
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_like_requests_sent, container, false)
        binding = FragmentLikeRequestsSentBinding.bind(view)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUser = FirebaseAuth.getInstance().currentUser!!
        recyclerView = view.findViewById(R.id.myRecyclerView)
        tvNoUsersToShow =view.findViewById(R.id.tvNoUsers)


        binding.refreshLayout.setOnRefreshListener {
            loadData(view)

        }

        if(sharedViewModel.loadedLikeRequestsSentFragmentBefore == true) {

            binding.tvUserCount.text = "( ${sharedViewModel.myLikeRequestsSentList.size} )"

            val adapter =
                sharedViewModel.myLikeRequestsSentList.let { LikeRequestsSentCardAdapter(it) }
            recyclerView.adapter = adapter
            recyclerView.layoutManager = StaggeredGridLayoutManager(
                1,
                StaggeredGridLayoutManager.VERTICAL
            )

            if(sharedViewModel.myLikeRequestsSentList.size==0){
                tvNoUsersToShow.visibility = View.VISIBLE
            }else {
                tvNoUsersToShow.visibility = View.GONE
            }

        }else
        {
            loadData(view)
            sharedViewModel.loadedLikeRequestsSentFragmentBefore = true
        }
        //loadData(view)
    }

    fun loadData(view: View)
    {        val db = FirebaseFirestore.getInstance()
        binding.progressBar.visibility = View.VISIBLE

        GlobalScope.launch(Dispatchers.IO) {

            val currentUserModel = db.collection("users").document(currentUser.uid).get().await()
                .toObject(UserModel::class.java)
            var users:MutableList<UserModel> = arrayListOf()

            val likeRequestsSentArray = currentUserModel?.usersYouLiked

            val addingUsers = launch {
                if (likeRequestsSentArray != null) {
                    for (uid in likeRequestsSentArray) {

                        var user:UserModel? = null
                        val job = launch {
                            user = db.collection("users").document(uid).get().await().toObject(UserModel::class.java)
                        }

                        job.join()

                        if (users != null) {
                            user?.let { users.add(it) }
                            Log.d("GENERAL", "${user?.displayName} is added to like requests page array" );

                        }else{
                            Log.d("GENERAL", "user is null, not added to like requests page array" );
                        }

                    }
                }
            }

            sharedViewModel.myLikeRequestsSentList = users


            addingUsers.join()

            withContext(Dispatchers.Main)
            {
                binding.progressBar.visibility = View.GONE
                binding.refreshLayout.isRefreshing = false
                binding.tvUserCount.text = "( ${users.size} )"
                if (users != null) {

                    if(users.size == 0) {
                        //Toast.makeText(requireContext(),"No Likes",Toast.LENGTH_SHORT).show()
                            tvNoUsersToShow.visibility = View.VISIBLE


                    }else {
                        tvNoUsersToShow.visibility = View.GONE
                    }
                }

                if (users != null) {
                    Log.d("GENERAL", "users you liked -> users list" + users.toString());
                    //Toast.makeText(requireContext(),"Size:${users.size}",Toast.LENGTH_SHORT).show()
                }

                val adapter = users?.let {  LikeRequestsSentCardAdapter(it) }
                recyclerView.adapter = adapter
                recyclerView.layoutManager = StaggeredGridLayoutManager(
                    1,
                    StaggeredGridLayoutManager.VERTICAL
                )
            }
        }


    }
}