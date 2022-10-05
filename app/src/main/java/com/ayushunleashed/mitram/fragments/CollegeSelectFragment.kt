package com.ayushunleashed.mitram.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.databinding.FragmentCollegeSelectBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject


class CollegeSelectFragment : Fragment() {
    lateinit var thisContext: Context
    private lateinit var binding: FragmentCollegeSelectBinding

    var collegeNameList:ArrayList<String> = arrayListOf()
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
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCollegeSelectBinding.bind(view)
        generateDummyCollegeNamesList()
        handleActvLogic()
        handleButtons()
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
            var collegeName = binding.actvSelectCollege.text
            Log.d("GENERAL",collegeName.toString())
        }
    }

    fun generateDummyCollegeNamesList(){
        collegeNameList.clear()
        collegeNameList.add("Jabalpur Engineering College")
        collegeNameList.add("Devi Ahilya Vishwavidyalaya")
        collegeNameList.add("Shri Govindram Seksaria Institute of Technology and Science")
        collegeNameList.add("Other")
    }

    private fun handleActvLogic() {


        Log.d("COLLEGE_LIST",collegeNameList.toString())
        val collegeAdapter = ArrayAdapter(thisContext,android.R.layout.simple_spinner_dropdown_item,collegeNameList)

        binding.actvSelectCollege.setAdapter(collegeAdapter)

        Log.d("COLLEGE_LIST","Setted into UI")
        binding.actvSelectCollege.setOnItemClickListener { adapterView, view, i, l ->
            //Toast.makeText(thisContext,adapterView.getItemAtPosition(i).toString(),Toast.LENGTH_SHORT).show()
            binding.btnNextScreen.visibility = View.VISIBLE
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
                handleActvLogic()

            }, { error ->
                Log.d("RESPONSE_ERROR", error.toString())
            })
        queue.add(req)
    }

}