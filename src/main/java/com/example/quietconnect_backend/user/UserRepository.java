package com.example.quietconnect_backend.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;



@Repository
public interface UserRepository extends JpaRepository<User,Long> {


    Optional<User> findByEmail(String email);

    boolean existsByIdAndPosts_Id(Long userId, Long postId);

    boolean existsByUsername(String username);



    
}
