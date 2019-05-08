package io.ensure.deepsea.ai;

import io.ensure.deepsea.admin.enrolment.models.Device;
import io.ensure.deepsea.admin.enrolment.models.Enrolment;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Runner {
    public static void main(String[] args) {
        Enrolment enrolment = new Enrolment();

        Device device = new Device();
        device.setModel("iPhone 7");
        device.setManufacturer("Apple");

        List<Device> devices = new ArrayList<>();
        devices.add(device);

        enrolment.setEnrolmentId("enrolment-2");
        enrolment.setAgreeTerms(true);
        enrolment.setClientId("the-bank");
        enrolment.setEmail("andrew.ward@ust-global.com");
        enrolment.setFirstName("Andy");
        enrolment.setLastName("Ward");
        enrolment.setGrossPremium(25);
        enrolment.setIpt(5);
        enrolment.setProductId("gold");
        enrolment.setTitle("Mr");
        enrolment.setDateOfBirth(Instant.now());
        enrolment.setStartDate(Instant.now());
        enrolment.setDevices(devices);

        Loader loader = new Loader();
        loader.loadEnrolment(enrolment);
    }
}
