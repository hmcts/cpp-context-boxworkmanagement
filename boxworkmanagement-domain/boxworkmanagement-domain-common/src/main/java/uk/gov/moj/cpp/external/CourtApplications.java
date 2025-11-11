package uk.gov.moj.cpp.external;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CourtApplications {

  private UUID id;

  public CourtApplications() {
  }

  public CourtApplications(final UUID id) {
    this.id = id;
  }

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "CourtApplication{" +
            "id=" + id +
            '}';
  }
}
