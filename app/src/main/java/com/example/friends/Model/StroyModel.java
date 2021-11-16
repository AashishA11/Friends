package com.example.friends.Model;

import java.util.ArrayList;

public class StroyModel {

     private String storyBy;
     private long storyAt;

     ArrayList<UserStories> stories;

     public StroyModel() {
     }

     public String getStoryBy() {
          return storyBy;
     }

     public void setStoryBy(String storyBy) {
          this.storyBy = storyBy;
     }

     public long getStoryAt() {
          return storyAt;
     }

     public void setStoryAt(long storyAt) {
          this.storyAt = storyAt;
     }

     public ArrayList<UserStories> getStories() {
          return stories;
     }

     public void setStories(ArrayList<UserStories> stories) {
          this.stories = stories;
     }
}
