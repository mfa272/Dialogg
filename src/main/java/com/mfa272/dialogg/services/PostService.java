package com.mfa272.dialogg.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mfa272.dialogg.dto.PostDTO;
import com.mfa272.dialogg.entities.Post;
import com.mfa272.dialogg.entities.User;
import com.mfa272.dialogg.repositories.PostRepository;
import com.mfa272.dialogg.repositories.UserRepository;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository){
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public boolean createPost(PostDTO postDTO){
        Post post = new Post(postDTO.getContent(), userRepository.findByUsername(postDTO.getUsername()).get());
        postRepository.save(post);
        return true;
    }
}
