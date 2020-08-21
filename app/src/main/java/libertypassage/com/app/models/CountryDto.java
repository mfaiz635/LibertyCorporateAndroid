package libertypassage.com.app.models;

public class CountryDto {
    String countryName;
    int countryCode;
    int countryId;

    public CountryDto(String countryName, Integer countryCode, Integer countryId) {
        this.countryName = countryName;
        this.countryCode = countryCode;
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Integer getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(Integer countryCode) {
        this.countryCode = countryCode;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }
}
