package com.healtify.healtify.repository;

import com.healtify.healtify.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class AddData {

    private final MoodRepository moodRepository;

    @Autowired
    public AddData(
            MoodRepository moodRepository) {
        this.moodRepository = moodRepository;
    }

    public void addUserProfileData(UserAccount userAccount) {

        // Utwórz obiekt UserProfile
        MoodTracker moodTracker = new MoodTracker();
        moodTracker.setUserAccount(userAccount);
        moodTracker.setMoodDate(LocalDate.now());
        moodTracker.setMoodRating(4);
        moodTracker.setComments("Feeling okay today!");

        // Zapisz obiekt UserProfile w bazie danych
        moodRepository.save(moodTracker);

        System.out.println("User profile "+ userAccount.getUsername() +" data added successfully!");
    }
}