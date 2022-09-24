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
import com.ayushunleashed.mitram.adapters.PeopleLikesCardAdapter
import com.ayushunleashed.mitram.databinding.FragmentLikesBinding
import com.ayushunleashed.mitram.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


class LikesFragment : Fragment() {

    private lateinit var usersList:MutableList<UserModel>
    lateinit var thisContext: Context
    private lateinit var currentUser:FirebaseUser
    lateinit var sharedViewModel: SharedViewModel
    lateinit var tvNoUsersToShow: TextView
    private lateinit var binding: FragmentLikesBinding
    lateinit var recyclerView:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            // Handle the back button event
            findNavController().navigate(R.id.action_likesFragment_to_discoverFragment)

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
        val view = inflater.inflate(R.layout.fragment_likes, container, false)
        binding = FragmentLikesBinding.bind(view)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUser = FirebaseAuth.getInstance().currentUser!!
        recyclerView = view.findViewById(R.id.myRecyclerView)
        tvNoUsersToShow =view.findViewById(R.id.tvNoUsers)

        binding.btnMyLikes.setOnClickListener {
            findNavController().navigate(R.id.action_likesFragment_to_likeRequestsSentFragment)
        }


        binding.refreshLayout.setOnRefreshListener {
            loadData(view)

        }

        if(sharedViewModel.loadedLikesFragmentBefore == true) {

            binding.tvUserCount.text = "( ${sharedViewModel.myLikesList.size} )"
            val adapter =
                sharedViewModel.myLikesList.let { PeopleLikesCardAdapter(it) }
            recyclerView.adapter = adapter
            recyclerView.layoutManager = StaggeredGridLayoutManager(
                1,
                StaggeredGridLayoutManager.VERTICAL
            )

            if(sharedViewModel.myLikesList.size == 0) {
                //Toast.makeText(requireContext(),"No Likes",Toast.LENGTH_SHORT).show()
                tvNoUsersToShow.visibility = View.VISIBLE


            }else {
                tvNoUsersToShow.visibility = View.GONE
            }
        }else
        {
            loadData(view)
            sharedViewModel.loadedLikesFragmentBefore = true
        }
        //loadData(view)
    }

    fun loadData(view: View)
    {        val db = FirebaseFirestore.getInstance()
        binding.progressBar.visibility = View.VISIBLE
        binding.myRecyclerView.visibility = View.GONE

        GlobalScope.launch(Dispatchers.IO) {

            val currentUserModel = db.collection("users").document(currentUser.uid).get().await()
                .toObject(UserModel::class.java)
            var users:MutableList<UserModel> = arrayListOf()

            val likedByArray = currentUserModel?.likedBy

            val addingUsers = launch {
                if (likedByArray != null) {
                    for (uid in likedByArray) {

                        var user:UserModel? = null
                        val job = launch {
                            user = db.collection("users").document(uid).get().await().toObject(UserModel::class.java)
                        }

                        job.join()

                        if (users != null) {
                            user?.let { users.add(it) }
                        }

                        Log.d("GENERAL", "${user?.displayName} is added to Likes Page array" );
                    }
                }
            }
            sharedViewModel.myLikesList = users

            addingUsers.join()

            withContext(Dispatchers.Main)
            {
                binding.tvUserCount.text = "( ${users.size} )"
                binding.progressBar.visibility = View.GONE
                binding.myRecyclerView.visibility = View.VISIBLE
                binding.refreshLayout.isRefreshing = false
                if (likedByArray != null) {
                    if(likedByArray.size == 0) {
                        //Toast.makeText(requireContext(),"No Likes",Toast.LENGTH_SHORT).show()
                            tvNoUsersToShow.visibility = View.VISIBLE


                    }else {
                        tvNoUsersToShow.visibility = View.GONE
                    }
                }

                if (users != null) {
                    Log.d("GENERAL", "liked by users list" + users.toString());
                    //Toast.makeText(requireContext(),"Size:${users.size}",Toast.LENGTH_SHORT).show()
                }

                val adapter = users?.let { PeopleLikesCardAdapter(it) }
                recyclerView.adapter = adapter
                recyclerView.layoutManager = StaggeredGridLayoutManager(
                    1,
                    StaggeredGridLayoutManager.VERTICAL
                )
            }
        }


    }
}