package libertypassage.com.corporate.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import libertypassage.com.corporate.model.IndustryProfessions
import libertypassage.com.corporate.repository.ProfessionListRepository


class ProfessionListViewModel : ViewModel() {

    var servicesLiveData: MutableLiveData<IndustryProfessions>? = null

    fun getList(industryId: String): LiveData<IndustryProfessions>? {
        servicesLiveData = ProfessionListRepository.getServicesApiCall(industryId)

        return servicesLiveData
    }

}