package libertypassage.com.corporate.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import libertypassage.com.corporate.model.Industry
import libertypassage.com.corporate.repository.IndustryListRepository


class IndustryListViewModel : ViewModel() {

    var servicesLiveData: MutableLiveData<Industry>? = null

    fun getList(): LiveData<Industry>? {
        servicesLiveData = IndustryListRepository.getServicesApiCall()

        return servicesLiveData
    }

}