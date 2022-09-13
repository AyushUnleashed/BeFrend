package com.ayushunleashed.mitram.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.asynctaskcoffee.cardstack.CardContainerAdapter
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.models.UserModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur


class UserCardAdapter(var users: List<UserModel>,context: Context): CardContainerAdapter() {

    var layoutInflater: LayoutInflater = LayoutInflater.from(context)
    var thisContext:Context = context
    lateinit var myBlurView :View
    lateinit var userCardChipGroup :ChipGroup


    private fun loadChipsFromDB(position: Int){
        var count =0;
        for(item in users[position].interests){
            addChip(item)
            count++;
            if(count==3){
               return;
            }
        }
    }

    private fun addChip(input:String){
        val chip = layoutInflater.inflate(R.layout.single_chip_layout2, userCardChipGroup, false) as Chip
        chip.text = input
        chip.setOnCloseIconClickListener{
            userCardChipGroup.removeView(chip)
        }
        userCardChipGroup.addView(chip)
    }



    @SuppressLint("InflateParams")
    override fun getView(position: Int): View {
        val view = layoutInflater.inflate(R.layout.item_user,null)

        var tvUserName = view.findViewById<TextView>(R.id.tvUserName)
        var tvUserBio = view.findViewById<TextView>(R.id.tvUserBioDetailPage)
        var imgViewUserProfile = view.findViewById<ImageView>(R.id.imgViewUserProfile)
        var cardViewProfileDescription:CardView = view.findViewById(R.id.cardViewProfileDescription)
        var btnShowFullProfile = view.findViewById<ImageButton>(R.id.btnShowFullProfile)
        var myCardView =view.findViewById<CardView>(R.id.myCardView)
        val user = getItem(position)
        myBlurView = view.findViewById<BlurView>(R.id.blurView)
        tvUserName.text = users[position].displayName
        tvUserBio.text = users[position].bio
        Glide.with(imgViewUserProfile.context).load(users[position].imageUrl).placeholder(R.drawable.img_user_place_holder)
            .error(R.drawable.img_keep_calm_reload).into(imgViewUserProfile)

        userCardChipGroup = view.findViewById<ChipGroup>(R.id.userCardChipGroup)

        loadChipsFromDB(position)



        var radius =10f
        // Optional:
        // Set drawable to draw in the beginning of each blurred frame.
        // Can be used in case your layout has a lot of transparent space and your content
        // gets a too low alpha value after blur is applied.

        (myBlurView as BlurView?)?.setupWith(myCardView, RenderScriptBlur(thisContext))
            ?.setBlurRadius(radius) // or RenderEffectBlur

        val bundle = bundleOf("currentUser" to users[position])

        //defining nav controller for navigation
        var navController: NavController?=null


       btnShowFullProfile.setOnClickListener{

                //for navigating to product description fragment
                navController = Navigation.findNavController(view)
                navController!!.navigate(R.id.action_discoverFragment_to_fullUserProfileFragment,bundle)
        }



        return view
    }

    override fun getCount(): Int {
        return users.size
    }

    override fun getItem(position: Int): Any {
        return users[position]
    }
}


//
//class UserCardAdapter(var users: List<UserModel>,context: Context):RecyclerView.Adapter<UserCardAdapter.UserCardViewHolder>() {
//
//
//    inner class UserCardViewHolder(itemview: View):RecyclerView.ViewHolder(itemview)
//    {
//        var tvUserName:TextView
//        var tvUserBio:TextView
//        var imgViewUserProfile:ImageView
//
//        init {
//            tvUserBio = itemview.findViewById(R.id.tvUserBio)
//            tvUserName = itemview.findViewById(R.id.tvUserName)
//            imgViewUserProfile = itemview.findViewById(R.id.imgViewUserProfile)
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserCardViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user,parent,false)
//        return UserCardViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: UserCardViewHolder, position: Int) {
//
//        holder.tvUserName.text = users[position].displayName
//        holder.tvUserBio.text = users[position].bio
//        Glide.with(holder.imgViewUserProfile.context).load(users[position].imageUrl).into(holder.imgViewUserProfile)
//    }
//
//    override fun getItemCount(): Int {
//        return  users.size
//    }
//}