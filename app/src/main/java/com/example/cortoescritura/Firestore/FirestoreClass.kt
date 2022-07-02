package com.example.cortoescritura.Firestore

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.cortoescritura.activities.*
import com.example.cortoescritura.models.*
import com.example.cortoescritura.utils.Constants
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions


class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener {
                    e->
                Log.e(activity.javaClass.simpleName, "Error writing document")
            }
    }

    fun getCurrentUserId():String {

        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""

        if (currentUser != null) {
            return currentUser.uid
        }
        return currentUserID
    }


    fun createStory(activity:CreateStoryActivity, story: Story){

        mFireStore.collection(Constants.STORIES).document().set(story, SetOptions.merge())
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Story created successfully")
                Toast.makeText(activity, "Se ha creado la historia exitosamente!", Toast.LENGTH_LONG).show()
                getStoryByNameAndUser(story.createdBy, story.name)
                activity.storyCreatedSuccessfully()

         }.addOnFailureListener {
                exception ->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName, "Board created UNsuccessfully", exception)
        }
    }

    fun createReport(activity:ReadStoryActivity, report : Report){
        mFireStore.collection(Constants.REPORT).document().set(report, SetOptions.merge())
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Report created successfully")

            }.addOnFailureListener {
                    exception ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Board created UNsuccessfully", exception)
            }
    }

    private fun getStoryByNameAndUser(user:String, name:String){

        mFireStore.collection(Constants.STORIES)
            .whereEqualTo(Constants.CREATED_BY, user)
            .whereEqualTo("name", name)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (documentSnapshot in task.result.documents) {
                        var storyUID = documentSnapshot.getId()
                        var storyHashMap: HashMap<String, Any> = HashMap()
                        storyHashMap.put("storyId", storyUID)
                        updateStoryId(storyHashMap)
                    }
                }
            }
    }


    private fun updateStoryId(storyHashMap : HashMap<String, Any>){
        mFireStore.collection(Constants.STORIES)
            .document(storyHashMap.get("storyId").toString())
            .update(storyHashMap)
    }


    fun deleteStory(activity:Activity, storyId:String){
        mFireStore.collection(Constants.STORIES).document(storyId)
            .delete().addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Story updated successfully")
                Toast.makeText(activity, "Se ha eliminado la historia exitosamente!", Toast.LENGTH_LONG).show()
            }.addOnFailureListener{

            }
    }

    fun getStoriesList(activity:MyStoriesProfileActivity){
        mFireStore.collection(Constants.STORIES)
            .whereEqualTo(Constants.CREATED_BY, getCurrentUserId())
            .get()
            .addOnSuccessListener {
                    document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val storiesList : ArrayList<Story> = ArrayList()
                for(i in document.documents){
                    val story = i.toObject(Story::class.java)!!
                    storiesList.add(story)
                }
                activity.populateStoryListToUI(storiesList)
            }.addOnFailureListener {
                    e->
                Log.e(activity.javaClass.simpleName, "Error cargando las historias")
            }
    }

    fun getStoryQueryList(activity:ShowOthersStoriesActivity, searchBy:String, key:String){

        if(searchBy.equals("Random")){
            mFireStore.collection(Constants.STORIES)
                .get()
                .addOnSuccessListener {
                        document ->
                    Log.i(activity.javaClass.simpleName, document.documents.toString())
                    val storiesList : ArrayList<Story> = ArrayList()
                    for(i in document.documents){
                        val story = i.toObject(Story::class.java)!!
                        if(!story.createdBy.equals(getCurrentUserId())) {
                            storiesList.add(story)
                        }
                    }
                    activity.setStories(storiesList)
                }.addOnFailureListener {
                        e->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error cargando las historias")
                }
        }
        else if(searchBy.equals("Genre")){
            mFireStore.collection(Constants.STORIES)
                .whereEqualTo("genre",key)
                .get()
                .addOnSuccessListener {
                        document ->
                    Log.i(activity.javaClass.simpleName, document.documents.toString())
                    val storiesList : ArrayList<Story> = ArrayList()
                    for(i in document.documents){
                        val story = i.toObject(Story::class.java)!!
                        if(!story.createdBy.equals(getCurrentUserId())) {
                            storiesList.add(story)
                        }
                    }
                    activity.setStories(storiesList)
                }.addOnFailureListener {
                        e->
                    Log.e(activity.javaClass.simpleName, "Error cargando las historias")
                    activity.hideProgressDialog()
                }
        }
        else if(searchBy.equals("User")){
            mFireStore.collection(Constants.STORIES)
                .whereEqualTo("createdBy", key)
                .get()
                .addOnSuccessListener {
                        document ->
                    Log.i(activity.javaClass.simpleName, document.documents.toString())
                    val storiesList : ArrayList<Story> = ArrayList()
                    for(i in document.documents){
                        val story = i.toObject(Story::class.java)!!
                        if(!story.createdBy.equals(getCurrentUserId())) {
                            storiesList.add(story)
                        }
                    }
                    activity.setStories(storiesList)
                }.addOnFailureListener {
                        e->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error cargando las historias")
                }
        }
        else if(key.equals("Score")){
            mFireStore.collection(Constants.STORIES)
                .orderBy("averageRating")
                .get()
                .addOnSuccessListener {
                        document ->
                    Log.i(activity.javaClass.simpleName, document.documents.toString())
                    val storiesList : ArrayList<Story> = ArrayList()
                    for(i in document.documents){
                        val story = i.toObject(Story::class.java)!!
                        if(!story.createdBy.equals(getCurrentUserId())) {
                            storiesList.add(story)
                        }
                    }
                    activity.setStories(storiesList)
                }.addOnFailureListener {
                        e->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error cargando las historias")
                }
        }
        else if(key.equals("Recent")){
            mFireStore.collection(Constants.STORIES)
                //.whereNotEqualTo("createdBy", getCurrentUserId())
                .orderBy("dateCreated")
                .get()
                .addOnSuccessListener {
                        document ->
                    Log.i(activity.javaClass.simpleName, document.documents.toString())
                    val storiesList : ArrayList<Story> = ArrayList()
                    for(i in document.documents){
                        val story = i.toObject(Story::class.java)!!
                        if(!story.createdBy.equals(getCurrentUserId())) {
                            storiesList.add(story)
                        }
                    }
                    activity.setStories(ArrayList(storiesList.reversed()))
                }.addOnFailureListener {
                        e->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error cargando las historias")
                }
        }

    }

    fun updateStoryData(activity:Activity, storyId:String, storyMap:HashMap<String,Any>){
        mFireStore.collection(Constants.STORIES).document(storyId)
            .update(storyMap).addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Story updated successfully")
                if(activity is CreateStoryActivity) {
                    Toast.makeText(
                        activity,
                        "Se ha actualizado la historia exitosamente!",
                        Toast.LENGTH_LONG
                    ).show()
                    activity.storyUpdateSuccess()
                }
                if(activity is ReadStoryActivity){
                    Toast.makeText(
                        activity,
                        "Gracias por comentar!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }.addOnFailureListener{

            }
    }

    fun updateUserProfileData(activity:Activity, userHashMap : HashMap<String, Any>){
        mFireStore.collection(Constants.USERS).document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                when(activity) {
                    is MainActivity -> {
                       activity.tokenUpdateSuccess()
                    }
                    is MainMenuActivity -> {
                        activity.tokenUpdateSuccess()
                    }
                    is MyProfileActivity -> {
                        activity.profileUpdateSuccess()
                    }
                }
            }.addOnFailureListener {
                    e ->
                when(activity) {
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainMenuActivity -> {
                        activity.hideProgressDialog()
                    }
                    is SignInActivity -> {
                        Log.i(activity.javaClass.simpleName, "Confirmation Email NOT Sent")
                    }
                    is MyProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error ", e)
                Toast.makeText(activity,"Error, no se puedo actualizar la información", Toast.LENGTH_LONG)
            }
    }

    fun loadUserData(activity: Activity, readBoardsList:Boolean = false){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).get()
            .addOnSuccessListener {document->
                val loggedInUser = document.toObject(User::class.java)!!
                when(activity){
                    is SignInActivity ->{
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser, readBoardsList)
                    }
                    is MainMenuActivity->{
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                    is MyProfileActivity -> {
                        activity.setUserDataInUI(loggedInUser)
                    }
                }
            }.addOnFailureListener {
                    e->
                when(activity){
                    is SignInActivity ->{  activity.hideProgressDialog() }
                    is MainActivity -> { activity.hideProgressDialog() }
                    is MainMenuActivity -> { activity.hideProgressDialog() }
                }
                Log.e("SignInUser", "Error writing document")
            }
    }

    fun getMemberDetails(activity: SignInActivity, email:String):Boolean{
        var flag = false
        mFireStore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL,email).get()
            .addOnSuccessListener {
                    document ->
                if(document.documents.size>0){
                    val user =document.documents[0].toObject(User::class.java)!!
                    flag = user.confirmedEmail
                }else{
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("Ningun usuario encontrado con ese E-mail...")
                }
            }.addOnFailureListener {  e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while trying to get user details",
                    e
                )
            }

        return flag
    }

    fun getStoryDetails(activity: Activity, mStoryDocumentID: String) {
        mFireStore.collection(Constants.STORIES)
            .document(mStoryDocumentID)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                val story = document.toObject(Story::class.java)!!
                story.storyId = document.id
                if(activity is CreateStoryActivity) {
                    activity.setStoryDataInUI(story)
                }
                if(activity is ReadStoryActivity){
                    activity.setStoryDataInUI(story)
                }

            }.addOnFailureListener {
                    e->
                Log.e(activity.javaClass.simpleName, "Error cargando la historia")
            }
    }

    fun rateStory(activity:Activity, storyId: String, rating: Rating) {
        mFireStore.collection(Constants.STORIES)
            .document(storyId)
            .get()
            .addOnSuccessListener {
                document->
                var flag =false
                var avgRating = 0.0f
                var story  = document.toObject(Story::class.java)!!
                for(i in story.ratings){
                    if(i.user == rating.user){
                        i.rate = rating.rate
                        flag = true
                        avgRating+=rating.rate
                    }
                    else{
                        avgRating+=i.rate
                    }
                }
                if(flag == false){
                    story.ratings.add(rating)
                    avgRating +=rating.rate
                }

                avgRating /=story.ratings.size

                var map = HashMap<String, Any>()
                map.put(Constants.RATING, story.ratings)
                map.put("averageRating", avgRating)

                updateStoryData(activity,storyId, map)
            }.addOnFailureListener{
                    e->
                Log.e(activity.javaClass.simpleName, "Error actualizando la historia")
            }


    }

    fun getListImages(list: ArrayList<Comment>): ArrayList<String> {
        var images : ArrayList<String> = ArrayList()
        for(i in list){
            mFireStore.collection(Constants.USERS)
                .document(i.commentedBy)
                .get()
                .addOnSuccessListener {
                    document ->
                    var user = document.toObject(User::class.java)!!
                    images.add(user.image)
                }
        }
        return images
    }


}