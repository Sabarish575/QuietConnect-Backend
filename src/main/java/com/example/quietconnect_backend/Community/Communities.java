package com.example.quietconnect_backend.Community;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.quietconnect_backend.Post.Post;
import com.example.quietconnect_backend.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Communities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="community_title")
    private String communityTitle;

    @Column(name="community_Bio")
    private String communityBio;

    @Column(name="created_At")
    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "communities", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private List<CommunityMember> communityMember=new ArrayList<>();

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private List<CommunityTopics> communityTopics=new ArrayList<>();

    @OneToMany(mappedBy = "community",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Post> posts;

    @PrePersist
    public void update() {
        this.createdAt = LocalDate.now();
    }
}
