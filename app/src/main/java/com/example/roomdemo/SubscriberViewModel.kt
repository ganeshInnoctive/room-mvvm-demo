package com.example.roomdemo

import android.text.TextUtils
import android.util.Patterns
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomdemo.db.Subscriber
import com.example.roomdemo.db.SubscriberRepository
import kotlinx.coroutines.launch

class SubscriberViewModel(private val repository: SubscriberRepository) : ViewModel(), Observable {
    val subscribers = repository.subscribers
    private var isUpdateOrDelete = false
    private lateinit var subscriberToUpdateOrDelete: Subscriber

    @Bindable
    val inputName = MutableLiveData<String>()

    @Bindable
    val inputEmail = MutableLiveData<String>()

    @Bindable
    val saveOrUpdateButtonText = MutableLiveData<String>()

    @Bindable
    val clearAllOrDeleteButtonText = MutableLiveData<String>()

    private val statusMessage = MutableLiveData<Event<String>>()

    val message: LiveData<Event<String>>
        get() = statusMessage

    init {
        saveOrUpdateButtonText.value = "Save"
        clearAllOrDeleteButtonText.value = "Clear All"
    }

    fun initUpdateAndDelete(subscriber: Subscriber) {
        inputName.value = subscriber.subscriberName
        inputEmail.value = subscriber.subscriberEmail
        isUpdateOrDelete = true
        subscriberToUpdateOrDelete = subscriber
        saveOrUpdateButtonText.value = "Update"
        clearAllOrDeleteButtonText.value = "Delete"
    }

    fun saveOrUpdate() {
        if (TextUtils.isEmpty(inputName.value)) {
            statusMessage.value = Event("Subscriber name is mandatory")
        } else if (TextUtils.isEmpty(inputEmail.value)) {
            statusMessage.value = Event("Subscriber email is mandatory")
        } else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.value!!).matches()) {
            statusMessage.value = Event("Subscriber email is invalid")
        } else {
            if (isUpdateOrDelete) {
                subscriberToUpdateOrDelete.subscriberName = inputName.value!!
                subscriberToUpdateOrDelete.subscriberEmail = inputEmail.value!!
                update(subscriberToUpdateOrDelete)
            } else {
                val name = inputName.value!!
                val email = inputEmail.value!!

                insert(Subscriber(0, name, email))
                inputName.value = null
                inputEmail.value = null
            }
        }
    }

    fun clearAllOrDelete() {
        if (isUpdateOrDelete) {
            delete(subscriberToUpdateOrDelete)
        } else {
            deleteAll()
        }
    }

    private fun insert(subscriber: Subscriber) = viewModelScope.launch {
        val newRowId = repository.insert(subscriber)
        if (newRowId > -1) {
            statusMessage.value = Event("Subscriber Inserted Successfully $newRowId")
        } else {
            statusMessage.value = Event("Error occurred during insertion")
        }
    }

    private fun update(subscriber: Subscriber) = viewModelScope.launch {
        val noOfRowsUpdated = repository.update(subscriber)

        if (noOfRowsUpdated > 0) {
            inputName.value = null
            inputEmail.value = null
            isUpdateOrDelete = false
            subscriberToUpdateOrDelete = subscriber
            saveOrUpdateButtonText.value = "Save"
            clearAllOrDeleteButtonText.value = "Clear All"
            statusMessage.value = Event("$noOfRowsUpdated Rows Updated Successfully")
        } else {
            statusMessage.value = Event("Error occurred while updating data")
        }
    }

    private fun delete(subscriber: Subscriber) = viewModelScope.launch {
        val rowsDeleted = repository.delete(subscriber)

        if (rowsDeleted > 0) {
            inputName.value = null
            inputEmail.value = null
            isUpdateOrDelete = false
            subscriberToUpdateOrDelete = subscriber
            saveOrUpdateButtonText.value = "Save"
            clearAllOrDeleteButtonText.value = "Clear All"
            statusMessage.value = Event("$rowsDeleted Deleted Successfully")
        } else {
            statusMessage.value = Event("Error occurred while deleting data")
        }
    }

    private fun deleteAll() = viewModelScope.launch {
        val rowsDeleted = repository.deleteAll()

        if (rowsDeleted > 0) {
            statusMessage.value = Event("$rowsDeleted rows Deleted Successfully")
        } else {
            statusMessage.value = Event("Error occurred while deleting all data")
        }
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

}