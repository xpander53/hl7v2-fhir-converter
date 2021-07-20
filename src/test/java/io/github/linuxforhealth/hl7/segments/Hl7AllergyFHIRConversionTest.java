/*
 * (C) Copyright IBM Corp. 2021
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package io.github.linuxforhealth.hl7.segments;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.github.linuxforhealth.hl7.segments.util.AllergyUtils;

public class Hl7AllergyFHIRConversionTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void test_allergy_single() {
        String hl7message = "MSH|^~\\&|SE050|050|PACS|050|20120912011230||ADT^A01|102|T|2.6|||AL|NE\r"
                + "PID|0010||PID1234^5^M11^A^MR^HOSP~1234568965^^^USA^SS||DOE^JOHN^A^||19800202|F||W|111 TEST_STREET_NAME^^TEST_CITY^NY^111-1111^USA||(905)111-1111|||S|ZZ|12^^^124|34-13-312||||TEST_BIRTH_PLACE\r"
                + "AL1|1|DA|^PENICILLIN|MI|PRODUCES HIVES~RASH|MI\r"
                + "AL1|2|AA|^CAT DANDER|SV";

        AllergyIntolerance allergy = AllergyUtils.createAllergyFromHl7Segment(hl7message);
        assertThat(allergy.getCriticality().toCode()).isEqualTo("low");
        assertThat(allergy.getCategory().get(0).getCode()).isEqualTo("medication");
        assertThat(allergy.getCode().getText()).isEqualTo("PENICILLIN");
        assertThat(allergy.getReaction().get(0).getManifestation()).extracting(m -> m.getText())
                .containsExactlyInAnyOrder("PRODUCES HIVES", "RASH");
    }

    @Test
    public void test_allergy_no_severity_no_coding_system() {
        String hl7message = "MSH|^~\\&|SE050|050|PACS|050|20120912011230||ADT^A01|102|T|2.6|||AL|NE\r"
                + "PID|0010||PID1234^5^M11^A^MR^HOSP~1234568965^^^USA^SS||DOE^JOHN^A^||19800202|F||W|111 TEST_STREET_NAME^^TEST_CITY^NY^111-1111^USA||(905)111-1111|||S|ZZ|12^^^124|34-13-312||||TEST_BIRTH_PLACE\r"
                + "AL1|1|DA|00000741^OXYCODONE||HYPOTENSION\r";

        AllergyIntolerance allergy = AllergyUtils.createAllergyFromHl7Segment(hl7message);
        assertThat(allergy.hasCriticality()).isFalse();
        assertThat(allergy.getCategory().get(0).getCode()).isEqualTo("medication");
        assertThat(allergy.getCode().getText()).isEqualTo("OXYCODONE");
        List<Coding> codings = allergy.getCode().getCoding();
        assertEquals(codings.size(), 1);
        Coding coding = codings.get(0);
        assertEquals(coding.getCode(), "00000741");
        assertNull(coding.getDisplay());
        assertThat(allergy.getReaction().get(0).getManifestation()).extracting(m -> m.getText())
                .containsExactly("HYPOTENSION");
    }

    @Test
    /**
     * Verifies AL1-6 is put into AllergyIntolerance.onsetDateTime; AllergyIntolerance.reaction.onset is not set.
     */
    public void test_allergy_onset() {
        String hl7message = "MSH|^~\\&|SE050|050|PACS|050|20120912011230||ADT^A01|102|T|2.6|||AL|NE\r"
                + "PID|0010||PID1234||DOE^JANE|||F\r"
                + "AL1|1|DA|00000741^OXYCODONE||HYPOTENSION|20210101\r";

        AllergyIntolerance allergy = AllergyUtils.createAllergyFromHl7Segment(hl7message);
        assertThat(allergy.getCategory().get(0).getCode()).isEqualTo("medication");
        assertThat(allergy.getReaction().get(0).getManifestation()).extracting(m -> m.getText())
                .containsExactly("HYPOTENSION");
        Date onsetReaction = allergy.getReaction().get(0).getOnset();
        assertNull(onsetReaction);
        DateTimeType onsetAllergy = allergy.getOnsetDateTimeType();
        assertThat(onsetAllergy.getValueAsString().equals("20210101"));
    }

}