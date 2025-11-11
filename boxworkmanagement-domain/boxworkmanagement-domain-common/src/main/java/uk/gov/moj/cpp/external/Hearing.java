package uk.gov.moj.cpp.external;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings({"squid:S2384"})
public class Hearing  {
  private UUID id;

  private List<CourtApplications> courtApplications;
  @JsonProperty("isBoxHearing")
  private Boolean isBoxHearing;

  public Hearing() {
  }

  public Hearing(final UUID id, final List<CourtApplications> courtApplications, final Boolean isBoxHearing) {
    this.courtApplications = courtApplications;
    this.id = id;
    this.isBoxHearing = isBoxHearing;
  }

  public List<CourtApplications> getCourtApplications() {
    return courtApplications;
  }

  public void setCourtApplications(final List<CourtApplications> courtApplications) {
    this.courtApplications = courtApplications;
  }

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }
  @JsonProperty("isBoxHearing")
  public Boolean isBoxHearing() {
    return isBoxHearing;
  }
  @JsonProperty("isBoxHearing")
  public void setBoxHearing(final Boolean isboxHearing) {
    this.isBoxHearing = isboxHearing;
  }


  @java.lang.Override
  public java.lang.String toString() {
    return "Hearing{" +
            "courtApplications=" + courtApplications +
            ", id=" + id +
            ", isBoxHearing=" + isBoxHearing +
            '}';
  }
}
