package com.agro.livechatdemo

import android.net.Uri
import android.util.Log

import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.agro.livechatdemo.data.CHATS
import com.agro.livechatdemo.data.ChatData
import com.agro.livechatdemo.data.ChatUser

import com.agro.livechatdemo.data.Event
import com.agro.livechatdemo.data.USER_NODE
import com.agro.livechatdemo.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore

import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LCViewModel @Inject constructor(
    val auth: FirebaseAuth,
    var db: FirebaseFirestore,
    val storage: FirebaseStorage,
) : ViewModel() {

    var inProcess = mutableStateOf(false)
    var inProcessChat = mutableStateOf(false)
    val eventMutableState = mutableStateOf<Event<String>?>(null)
    var signIn = mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)
    val chats = mutableStateOf<List<ChatData>>(listOf())

    init {
        val currentUser = auth.currentUser
        signIn.value = currentUser != null
        currentUser?.uid?.let {
            getUserData(it)
        }
    }

    fun signUp(name: String, email: String, mobileNum: String, password: String) {
        inProcess.value = true
        if (name.isEmpty() or email.isEmpty() or mobileNum.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "Please Fill all Fields!")
            return
        }
        inProcess.value = true
        db.collection(USER_NODE).whereEqualTo("mobileNum", mobileNum).get().addOnSuccessListener {
            if (it.isEmpty) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { it ->
                    if (it.isSuccessful) {
                        signIn.value = true
                        createOrUpdateProfile(name, mobileNum)
                    } else {
                        handleException(it.exception, customMessage = "SignUp Failed")
                    }
                }
            } else {
                handleException(customMessage = "number already exist")
                inProcess.value = false
            }
        }

    }

    fun loginIn(email: String, password: String) {
        if (email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "Please Fill The All Fields")
            return
        } else {
            inProcess.value = true
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { it ->
                    if (it.isSuccessful) {
                        signIn.value = true
                        inProcess.value = false
                        auth.currentUser?.uid?.let {
                            getUserData(it)
                        }
                    } else {
                        handleException(exception = it.exception, customMessage = "Login Failed")
                    }
                }
        }
    }

    fun uploadProfileImage(uri: Uri) {
        uploadImage(uri) {
            createOrUpdateProfile(imageUrl = it.toString())
        }
    }

    fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {

        inProcess.value = true
        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("images/$uuid")
        val uploadTask = imageRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            val result = it.metadata?.reference?.downloadUrl
            result?.addOnSuccessListener(onSuccess)
            inProcess.value = false
        }
            .addOnFailureListener {
                handleException(it)
            }
    }

    fun createOrUpdateProfile(
        name: String? = null,
        mobileNum: String? = null,
        imageUrl: String? = null,
    ) {
        val uId = auth.currentUser?.uid
        val userData = UserData(
            userId = uId,
            name = name ?: userData.value?.name,
            mobileNum = mobileNum ?: userData.value?.mobileNum,
            imageUrl = imageUrl ?: userData.value?.imageUrl
        )
        uId?.let {
            inProcess.value = true
            db.collection(USER_NODE).document(uId).get().addOnSuccessListener {
                if (it.exists()) {

                } else {
                    db.collection(USER_NODE).document(uId).set(userData)
                    inProcess.value = false
                    getUserData(uId)
                }
            }.addOnFailureListener {
                handleException(it, "Can not retrive User")
            }
        }

    }

    private fun getUserData(uId: String) {
        inProcess.value = true
        db.collection(USER_NODE).document(uId).addSnapshotListener { value, error ->

            if (error != null) {
                handleException(error, "can not retrive data")
            }
            if (value != null) {
                val user = value.toObject<UserData>()
                userData.value = user
                inProcess.value = false
            }
        }
    }


    fun handleException(exception: Exception? = null, customMessage: String = "") {
        Log.e("LiveChatApp", "live chat exception : ", exception)
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage ?: ""
        val message = if (customMessage.isNullOrEmpty()) errorMsg else customMessage
        eventMutableState.value = Event(message)
        inProcess.value = false
    }

    fun logout() {
        auth.signOut()
        signIn.value = false
        userData.value = null
        eventMutableState.value = Event("Logged out")
    }

    fun onAddChat(number: String) {

        if (number.isEmpty() or !number.isDigitsOnly()) {
            handleException(customMessage = "Number must be contain digits only")
        } else {
            db.collection(CHATS).where(
                Filter.or(
                    Filter.and(
                        Filter.equalTo("user1.number", number),
                        Filter.equalTo("user2.number", userData.value?.mobileNum)
                    ),
                    Filter.and(
                        Filter.equalTo("user1.number", userData.value?.mobileNum),
                        Filter.equalTo("user2.number", number)
                    )
                )
            ).get().addOnSuccessListener {
                if (it.isEmpty) {
                    db.collection(USER_NODE).whereEqualTo("mobileNum", number).get()
                        .addOnSuccessListener {
                            if (it.isEmpty) {
                                handleException(customMessage = "Number not Found")
                            } else {
                                val chatPartner = it.toObjects<UserData>()[0]
                                val id = db.collection(CHATS).document().id
                                val chat = ChatData(
                                    chatId = id,
                                    ChatUser(
                                        userData.value?.userId,
                                        userData.value?.name,
                                        userData.value?.imageUrl,
                                        userData.value?.mobileNum
                                    ),
                                    ChatUser(
                                        chatPartner.userId,
                                        chatPartner.name,
                                        chatPartner.imageUrl,
                                        chatPartner.mobileNum
                                    )
                                )
                                db.collection(CHATS).document(id).set(chat)
                            }
                        }.addOnFailureListener{
                            handleException(it)
                        }
                } else {
                    handleException(customMessage = "Chat already exist")
                }
            }
        }
    }

}

