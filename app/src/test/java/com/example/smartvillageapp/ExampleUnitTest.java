package com.example.smartvillageapp;

import org.junit.Test;

import static org.junit.Assert.*;

public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void userModel_initialization_isCorrect() {
        UserModel user = new UserModel("123", "John Doe", "john@example.com");
        assertEquals("123", user.id);
        assertEquals("John Doe", user.name);
        assertEquals("john@example.com", user.email);
    }

    @Test
    public void businessModel_initialization_isCorrect() {
        BusinessModel business = new BusinessModel(
                "Village Shop", "Retail", "123 Main St",
                "General store", "9876543210", "shop@village.com", "http://image.url"
        );
        assertEquals("Village Shop", business.getName());
        assertEquals("Retail", business.getDomain());
        assertEquals("123 Main St", business.getAddress());
        assertEquals("General store", business.getDescription());
        assertEquals("9876543210", business.getPhone());
        assertEquals("shop@village.com", business.getEmail());
        assertEquals("http://image.url", business.getImageUrl());
    }

    @Test
    public void jobModel_initialization_isCorrect() {
        JobModel job = new JobModel("job1", "Carpenter", "5 years exp", "Build furniture", "admin", "Open");
        assertEquals("job1", job.jobId);
        assertEquals("Carpenter", job.title);
        assertEquals("5 years exp", job.requirement);
        assertEquals("Build furniture", job.description);
        assertEquals("admin", job.postedBy);
        assertEquals("Open", job.status);
    }

    @Test
    public void locationModel_initialization_isCorrect() {
        LocationModel location = new LocationModel("Village Square", "Main gathering spot", 18.5204, 73.8567, "http://map.url");
        assertEquals("Village Square", location.name);
        assertEquals("Main gathering spot", location.description);
        assertEquals(18.5204, location.latitude, 0.0001);
        assertEquals(73.8567, location.longitude, 0.0001);
        assertEquals("http://map.url", location.imageUrl);
    }
}