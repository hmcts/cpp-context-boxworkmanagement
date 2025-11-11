package uk.gov.moj.cpp.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings({"squid:S2384"})
public class HearingResulted {
    private Hearing hearing;

    public HearingResulted() {
    }

    public HearingResulted(final Hearing hearing) {

        this.hearing = hearing;
    }

    public Hearing getHearing() {
        return hearing;
    }

    @Override
    public String toString() {
        return "HearingResulted{" +
                "hearing=" + hearing +
                '}';
    }
}
