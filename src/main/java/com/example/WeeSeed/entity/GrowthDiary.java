package com.example.WeeSeed.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GrowthDiary {
    @Id
    @Column
    private String growthId;

    @Column
    private String childCode;

    @Column
    private int imageCardNum;

    @Column
    private int videoCardNum;

    @Column
    private int usageTime;

    @Column
    private  String childId;

    @Column
    private String userId;

    @Builder
    public GrowthDiary(String growthId,String childCode,int iCN,int vCN,int uT,String childId,String userId){
        this.growthId = growthId;
        this.childCode = childCode;
        this.imageCardNum = iCN;
        this.videoCardNum = vCN;
        this.usageTime = uT;
        this.childId = childId;
        this.userId = userId;
    }

}

