package com.ayushunleashed.mitram.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.databinding.FragmentCollegeSelectBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CollegeSelectFragment : Fragment() {
    lateinit var thisContext: Context
    private lateinit var binding: FragmentCollegeSelectBinding
    var isCollegeSelected = false
    var isYearSelected = false
    var isStreamSelected = false
    var checkFields =false

    var collegeName =""
    var year =""
    var stream=""
    var collegeNameList:ArrayList<String> = arrayListOf()
    lateinit var  rootView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (container != null) {
            thisContext = container.getContext()
        };
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_college_select, container, false)
        rootView =view
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCollegeSelectBinding.bind(view)
        generateDummyCollegeNamesList()
        handleCollegeSelectorLogic()
        handleYearSelectorLogic()
        handleStreamSelectorLogic()
        handleButtons()


        binding.actvSelectCollege.setText("")
        binding.actvSelectYear.setText("")
        binding.actvSelectStream.setText("")

    }

    private fun handleYearSelectorLogic() {
        val years = resources.getStringArray(R.array.years)
        val yearsAdapter = ArrayAdapter(thisContext,android.R.layout.simple_spinner_dropdown_item,years)
        binding.actvSelectYear.setAdapter(yearsAdapter)
        binding.actvSelectYear.setOnItemClickListener { adapterView, view, i, l ->
            isYearSelected = true
            year = binding.actvSelectYear.text.toString()
        }
    }

    private fun handleStreamSelectorLogic() {
        val streams = resources.getStringArray(R.array.streams)
        val streamsAdapter = ArrayAdapter(thisContext,android.R.layout.simple_spinner_dropdown_item,streams)
        binding.actvSelectStream.setAdapter(streamsAdapter)
        binding.actvSelectStream.setOnItemClickListener { adapterView, view, i, l ->
            isStreamSelected = true
            stream = binding.actvSelectStream.text.toString()
        }
    }



    private fun handleButtons() {
//        binding.btnGetCollegesData.setOnClickListener {
//            Log.d("COLLEGE_LIST","List_Loading Start")
//            getCollegeNames()
//            Log.d("COLLEGE_LIST",collegeNameList.toString())
//        }
//
//        binding.btnEditCollegeName.setOnClickListener {
//            binding.actvSelectCollege.visibility = View.VISIBLE
//            binding.actvSelectCollege.text.clear()
//            binding.btnEditCollegeName.visibility = View.GONE
//            binding.tvSelectedCollegeName.text = "No College Selected"
//
//        }

        binding.btnNextScreen.setOnClickListener {


            if(isCollegeSelected && isYearSelected && isStreamSelected)
            {
                // go to next screen
                Log.d("GENERAL","college:${collegeName.toString()}")
                Log.d("GENERAL","year:${year.toString()}")
                Log.d("GENERAL","stream:${stream.toString()}")

                val myBundle = bundleOf("collegeName" to collegeName,"year" to year,"stream" to stream)
                findNavController().navigate(R.id.action_collegeSelectFragment_to_splashScreenFragment,myBundle)

            }else{
//                checkEmptyFields()
//                checkFields = true
//                Toast.makeText(thisContext,"Fill All details",Toast.LENGTH_SHORT).show()
                val snackbar = Snackbar.make(rootView, "Fill All Details",
                    Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(resources.getColor(R.color.red))
                snackbar.setTextColor(resources.getColor(R.color.white))
                snackbar.show()
            }

        }
    }
//
//    // this will give Red Errors
//    private fun checkEmptyFields()
//    {   //To give errors
//        if(collegeName == "") binding.tilCollegeName.error = "College Name is a Required Field" else binding.tilCollegeName.error = null
//
//        if (semester=="") binding.tilYearName.error = "Semester is a Required Field" else   binding.tilYearName.error = null
//
//        if (stream =="") binding.tilStreamName.error = "Stream is a Required Field" else binding.tilStreamName.error = null
//    }
//
//    // this changes error as input changes
//    private fun checkFieldsLive()
//    {
//
//        binding.actvSelectCollege.doOnTextChanged { text, start, before, count ->
//            if(collegeName == "") binding.tilCollegeName.error = "College Name is a Required Field" else binding.tilCollegeName.error = null
//        }
//
//        binding.actvSelectYear.doOnTextChanged { text, start, before, count ->
//            if (semester=="") binding.tilYearName.error = "Semester is a Required Field" else   binding.tilYearName.error = null
//        }
//
//        binding.actvSelectStream.doOnTextChanged { text, start, before, count ->
//            if (stream =="") binding.tilStreamName.error = "Stream is a Required Field" else binding.tilStreamName.error = null
//        }
//    }

    fun generateDummyCollegeNamesList(){
        collegeNameList.clear()
        collegeNameList.add("Jabalpur Engineering College")
        collegeNameList.add("Devi Ahilya Vishwavidyalaya")
        collegeNameList.add("Shri Govindram Seksaria Institute of Technology and Science")
        collegeNameList.add("Other")
    }

    private fun handleCollegeSelectorLogic() {


        Log.d("COLLEGE_LIST",collegeNameList.toString())
        val collegeAdapter = ArrayAdapter(thisContext,android.R.layout.simple_spinner_dropdown_item,collegeNameList)

        binding.actvSelectCollege.setAdapter(collegeAdapter)

        Log.d("COLLEGE_LIST","Setted into UI")
        binding.actvSelectCollege.setOnItemClickListener { adapterView, view, i, l ->
            //Toast.makeText(thisContext,adapterView.getItemAtPosition(i).toString(),Toast.LENGTH_SHORT).show()
                isCollegeSelected =true
                collegeName = binding.actvSelectCollege.text.toString()
//            binding.tvSelectedCollegeName.text = adapterView.getItemAtPosition(i).toString()
//            binding.actvSelectCollege.visibility = View.GONE
//            binding.btnEditCollegeName.visibility =View.VISIBLE
        }
    }

    fun getCollegeNames(){
        binding.progressBar.visibility = View.VISIBLE

        GlobalScope.launch(Dispatchers.Main) {
            loadAllCollegesListToArray()
        }
    }
    suspend private fun loadAllCollegesListToArray(){

        val queue = Volley.newRequestQueue(requireContext())
        val url = "https://ayushkiapi.herokuapp.com/"

        val req = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                Log.d("COLLEGE_LIST","Got Response")
               var collegesData = response
                //Log.d("RESPONSE",response.toString())
                Log.d("COLLEGE_LIST","Reponse Length: ${collegesData.length()}")

                for( i in 0 until collegesData.length()){
                    var currentCollegeData = collegesData.getJSONArray(i)
                    //Log.d("RESPONSE_ITEM",currentCollegeData.toString());
                    var currentCollegeName = currentCollegeData[2]
                    //Log.d("RESPONSE_ITEM","CollegeName:"+currentCollegeName.toString());
                    collegeNameList.add(currentCollegeName as String)
                }
                Log.d("COLLEGE_LIST","Completed loading into array")
                Log.d("COLLEGE_LIST","Collegename Length: ${collegeNameList.size}")
                //Log.d("COLLEGE_LIST",collegeNameList.toString())
                binding.progressBar.visibility = View.GONE
                handleCollegeSelectorLogic()

            }, { error ->
                Log.d("RESPONSE_ERROR", error.toString())
            })
        queue.add(req)
    }

}