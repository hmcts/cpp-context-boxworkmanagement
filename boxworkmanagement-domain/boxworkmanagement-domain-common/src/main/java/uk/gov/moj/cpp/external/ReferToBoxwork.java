package uk.gov.moj.cpp.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings({"squid:S2384"})
public class ReferToBoxwork {
    private Hearing hearing;

    public ReferToBoxwork() {
    }

    public ReferToBoxwork(final Hearing hearing) {
        this.hearing = hearing;
    }

    public Hearing getHearing() {
        return hearing;
    }

    @Override
    public String toString() {
        return "ReferToBoxwork{" +
                "hearing=" + hearing +
                '}';
    }
}
