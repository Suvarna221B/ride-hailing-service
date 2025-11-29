package com.example.ridehailing.repository;

import com.example.ridehailing.model.Driver;
import com.example.ridehailing.model.DriverStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class DriverRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DriverRepository driverRepository;

    @Test
    public void testFindByUserId_Found() {
        com.example.ridehailing.model.User user = new com.example.ridehailing.model.User(
                "testuser",
                "password",
                com.example.ridehailing.model.UserType.DRIVER);
        entityManager.persist(user);

        Driver driver = new Driver();
        driver.setName("Test Driver");
        driver.setStatus(DriverStatus.AVAILABLE);
        driver.setUser(user);
        entityManager.persist(driver);
        entityManager.flush();

        Optional<Driver> found = driverRepository.findByUserId(user.getId());

        assertTrue(found.isPresent());
        assertEquals("Test Driver", found.get().getName());
        assertEquals(DriverStatus.AVAILABLE, found.get().getStatus());
    }

    @Test
    public void testFindByUserId_NotFound() {
        Optional<Driver> found = driverRepository.findByUserId(999L);

        assertFalse(found.isPresent());
    }

    @Test
    public void testSaveDriver() {
        Driver driver = new Driver();
        driver.setName("New Driver");
        driver.setStatus(DriverStatus.OFFLINE);

        Driver saved = driverRepository.save(driver);

        assertNotNull(saved.getId());
        assertEquals("New Driver", saved.getName());
        assertEquals(DriverStatus.OFFLINE, saved.getStatus());
    }
}
