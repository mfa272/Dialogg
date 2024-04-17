package com.mfa272.dialogg.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.mfa272.dialogg.dto.PostDTO;
import com.mfa272.dialogg.entities.Post;
import com.mfa272.dialogg.repositories.PostRepository;
import com.mfa272.dialogg.repositories.AccountRepository;
@Service
public class PostService {
    private final PostRepository postRepository;
    private final AccountRepository userRepository;

    @Autowired
    public PostService(PostRepository postRepository, AccountRepository userRepository){
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public boolean createPost(PostDTO postDTO, String username){
        Post post = new Post(postDTO.getContent(), userRepository.findByUsername(username).get());
        postRepository.save(post);
        return true;
    }

    public Page<PostDTO> getPostsByUser(String username, int page, int size) {
        Page<Post> posts = postRepository.findByAccountUsernameOrderByCreatedAtDesc(username, PageRequest.of(page, size));
        return posts.map(p -> {
            PostDTO dto = new PostDTO();
            dto.setContent(p.getContent());
            dto.setCreatedAt(p.getCreatedAt());
            dto.setUsername(p.getAccount().getUsername());
            return dto;
        });
    }
}
