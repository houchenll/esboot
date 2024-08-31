package com.yulin.es.boot.esboot.service;

import com.yulin.es.boot.esboot.es.Poem;
import com.yulin.es.boot.esboot.es.PoemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PoemService {

    @Autowired
    private PoemRepository poemRepository;

    public Iterable<Poem> searchAll() {
        return poemRepository.findAll();
    }

    public List<Poem> searchPosts(String query) {
        return poemRepository.findByTextOrTitle(query);
    }

}
