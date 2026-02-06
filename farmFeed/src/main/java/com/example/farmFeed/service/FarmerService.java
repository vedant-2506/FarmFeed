package com.example.farmFeed.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.farmFeed.entity.Farmer;
import com.example.farmFeed.repository.FarmerRepository;

@Service
public class FarmerService {

    @Autowired
    private FarmerRepository repository;

    public Farmer save(Farmer farmer) {
        return repository.save(farmer);
    }
}
