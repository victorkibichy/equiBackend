package com.EquiFarm.EquiFarm.user;

import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import com.EquiFarm.EquiFarm.Tracking.TrackingEntityListener;
import com.EquiFarm.EquiFarm.utils.Gender;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Size;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper=false)
@EntityListeners(TrackingEntityListener.class)
public abstract class Profile extends TrackingEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(name = "id_number", length = 14, nullable = true)
    private String idNumber;

    @Column(name = "bio", columnDefinition = "TEXT", nullable = true)
    @Size(max = 250)
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Gender gender;

    @Lob
    @Column(name = "profile_picture", nullable = true, length = 16777215)
    private String profilePicture;

    @PrePersist
    protected void onCreate() {
        bio = "***No Abouts***";
        profilePicture = getDefaultProfilePicture();

    }

    private String getDefaultProfilePicture() {
        String defaultImagePath = "media/default.png";
        System.out.println("Default Path: " + defaultImagePath);
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(defaultImagePath);
            byte[] fileContent = inputStream.readAllBytes();

            // Compress the image data
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream,
                    new Deflater(Deflater.BEST_COMPRESSION));
            deflaterOutputStream.write(fileContent);
            deflaterOutputStream.close();
            byte[] compressedData = byteArrayOutputStream.toByteArray();

            // Convert to Base64 and return as a string
            return Base64.getEncoder().encodeToString(compressedData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // private String getDefaultProfilePicture() {
    // String defaultImagePath = "media/default.png";
    // System.out.println("Default Path: " + defaultImagePath);
    // try {
    // ClassLoader classLoader = getClass().getClassLoader();
    // InputStream inputStream = classLoader.getResourceAsStream(defaultImagePath);
    // byte[] fileContent = inputStream.readAllBytes();
    // return Base64.getEncoder().encodeToString(fileContent);
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // return null;
    // }

}
