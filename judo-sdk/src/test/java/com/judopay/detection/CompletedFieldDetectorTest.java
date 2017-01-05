package com.judopay.detection;

import com.judopay.validation.Validation;

import org.junit.Test;

import java.util.Collection;

import rx.Observable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class CompletedFieldDetectorTest {

    @Test
    public void shouldOnlyHaveValidFieldsInCompletedFields() {
        Validation validValidation = new Validation(true, 0, false);
        Validation invalidValidation = new Validation(false, 0, false);

//        CompletedFieldsDetector completedFieldsDetector = new CompletedFieldsDetector.Builder()
//                .add("test1", Observable.just(validValidation))
//                .add("test2", Observable.just(invalidValidation))
//                .build();
//
//        Collection<CompletedField> completedFields = completedFieldsDetector.getFieldsOrderedByCompletion();
//
//        assertThat(completedFields.size(), equalTo(1));
//        CompletedField completedField = (CompletedField) completedFields.toArray()[0];
//
//        assertThat(completedField.getField(), equalTo("test1"));
    }

    @Test
    public void shouldOnlyHaveOneCompletedFieldWhenFieldCompletedTwice() {
        Validation validValidation1 = new Validation(true, 0, false);
        Validation validValidation2 = new Validation(true, 0, false);

//        CompletedFieldsDetector completedFieldsDetector = new CompletedFieldsDetector.Builder()
//                .add("test1", Observable.just(validValidation1, validValidation2))
//                .build();
//
//        Collection<CompletedField> completedFields = completedFieldsDetector.getFieldsOrderedByCompletion();
//
//        assertThat(completedFields.size(), equalTo(1));
//        CompletedField completedField = (CompletedField) completedFields.toArray()[0];
//
//        assertThat(completedField.getField(), equalTo("test1"));
    }
}