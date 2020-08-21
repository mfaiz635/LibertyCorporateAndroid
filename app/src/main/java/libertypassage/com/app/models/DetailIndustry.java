
package libertypassage.com.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DetailIndustry {

    @SerializedName("industry_id")
    @Expose
    private Integer industryId;
    @SerializedName("title")
    @Expose
    private String title;

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
