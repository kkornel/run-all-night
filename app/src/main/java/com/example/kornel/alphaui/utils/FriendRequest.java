package com.example.kornel.alphaui.utils;

public class FriendRequest {
    private String friendUid;
    private String requestType;
    private String friendName;
    private String avatarUrl;

    public FriendRequest() {
    }

    public FriendRequest(String friendUid, String requestType) {
        this.friendUid = friendUid;
        this.requestType = requestType;
    }

    public FriendRequest(String friendUid, String requestType, String friendName, String avatarUrl) {
        this(friendUid, requestType);
        this.friendName = friendName;
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getFriendUid() {
        return friendUid;
    }

    public void setFriendUid(String friendUid) {
        this.friendUid = friendUid;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    @Override
    public String toString() {
        return "FriendRequest{" +
                "friendKey='" + friendUid + '\'' +
                ", requestType='" + requestType + '\'' +
                ", friendName='" + friendName + '\'' +
                '}';
    }
}
