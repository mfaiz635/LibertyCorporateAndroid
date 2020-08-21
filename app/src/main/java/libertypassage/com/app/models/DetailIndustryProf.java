
package libertypassage.com.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DetailIndustryProf{

    @SerializedName("prof_id")
    @Expose
    private Integer profId;
    @SerializedName("industry_id")
    @Expose
    private Integer industryId;
    @SerializedName("title")
    @Expose
    private String title;

    public DetailIndustryProf(Integer profId, Integer industryId, String title) {
        this.profId = profId;
        this.industryId = industryId;
        this.title = title;

    }


    public Integer getProfId() {
        return profId;
    }

    public void setProfId(Integer profId) {
        this.profId = profId;
    }

    public Integer getIndustryId() {
        return industryId;
    }

    public void setIndustryId(Integer industryId) {
        this.industryId = industryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



}
