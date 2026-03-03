package com.example.quietconnect_backend.user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.example.quietconnect_backend.Community.Communities;
import com.example.quietconnect_backend.Community.CommunityMember;
import com.example.quietconnect_backend.Post.Comment;
import com.example.quietconnect_backend.Post.Like;
import com.example.quietconnect_backend.Post.Post;
import com.example.quietconnect_backend.Post.Reply;
import com.example.quietconnect_backend.User_Reputation.Reputation;
import com.example.quietconnect_backend.message.Message;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="user_data")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false)
    private String email;

    @Column(unique = true,nullable = false)
    private String google_id;

    @Column(unique = true)
    private String username;

    private String bio;

    private LocalDate createdAt;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<CommunityMember> communityMember =new ArrayList<>();

    @OneToMany(mappedBy = "createdBy")
    private List<Communities> communities=new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Like> likes;

    @OneToMany(mappedBy = "user")
    private List<Comment> comments;

    @OneToMany(mappedBy ="createdBy")
    private List<Post> posts;

    @OneToMany(mappedBy = "user")
    private List<Reply> replies;

    @OneToMany(mappedBy = "user")
    private List<Reputation> reputations;

    @OneToMany(mappedBy = "follower")
    private List<FollowUser> follower;

    @OneToMany(mappedBy = "following")
    private List<FollowUser> following;

    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL)
    private Battery battery;

    @OneToMany(mappedBy="sender")
    private List<Message> sender=new ArrayList<>();
    
    @OneToMany(mappedBy = "receiver")
    private List<Message> receiver=new ArrayList<>();


    private static final Log log=LogFactory.getLog(User.class);

    @PrePersist
    public void beforeSignup(){
        this.createdAt=LocalDate.now();
        log.info("[USER ACC AUDIT] signup attempt for use : "+this.email);
    }

    @PreUpdate
    @PreRemove
    public void beforeSignupAttempt(){
        log.info("[USER ACC AUDIT] signup/update/remove attempt for use : "+this.email);
    }
    
    @PostUpdate
    @PostPersist
    @PostRemove
    public void afterSignupAttempt(){
        log.info("[USER ACC AUDIT] account created/deleted/update with id : "+this.getId() +this.getEmail());
    }

}
