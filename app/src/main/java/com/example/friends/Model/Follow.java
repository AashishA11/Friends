package com.example.friends.Model;

public class Follow {

     private String followedBy;
     private long FpllowedAt;

    public Follow() {
    }

    public String getFollowedBy() {
        return followedBy;
    }

    public void setFollowedBy(String followedBy) {
        this.followedBy = followedBy;
    }

    public long getFpllowedAt() {
        return FpllowedAt;
    }

    public void setFpllowedAt(long fpllowedAt) {
        FpllowedAt = fpllowedAt;
    }
}
