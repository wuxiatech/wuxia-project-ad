/*
* Created on :24 Jun, 2014
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.com All right reserved.
*/
package cn.wuxia.project.location.core.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import cn.wuxia.project.common.model.ModifyInfoEntity;

/**
 * The persistent class for the p_ad database table.
 * 
 */
@Entity
@Table(name = "p_ad")
@Where(clause = ModifyInfoEntity.ISOBSOLETE_DATE_IS_NULL)
@JsonIgnoreProperties("hibernateLazyInitializer")
public class Ad extends ModifyInfoEntity {
    private static final long serialVersionUID = 1L;

    private String alias;

    private Timestamp beginTime;

    private Timestamp effectiveTime;

    private String picFilesetId;

    private String picFile;

    private String picUrl;

    private boolean selected;

    private String keyword;

    private String fee;

    private Integer level;

    private AdLocation location;

    private TargetEnum target;

    private String title;

    private Short type;

    private String url;

    private String userId;

    public Ad() {
        super();
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Column(name = "BEGIN_TIME")
    public Timestamp getBeginTime() {
        return this.beginTime;
    }

    public void setBeginTime(Timestamp beginTime) {
        this.beginTime = beginTime;
    }

    @Column(name = "EFFECTIVE_TIME")
    public Timestamp getEffectiveTime() {
        return this.effectiveTime;
    }

    public void setEffectiveTime(Timestamp effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    @Column(name = "PIC_FILESET_ID")
    public String getPicFilesetId() {
        return picFilesetId;
    }

    public void setPicFilesetId(String picFilesetId) {
        this.picFilesetId = picFilesetId;
    }

    @Column(name = "PIC_FILE_ID")
    public String getPicFile() {
        return picFile;
    }

    public void setPicFile(String picFile) {
        this.picFile = picFile;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getKeyword() {
        //        if (this.getPrice() != null && StringUtils.isNoneBlank(this.getPrice().getKeyword()))
        //            return this.getPrice().getKeyword();
        return this.keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    @Column(columnDefinition = "tinyint")
    public Integer getLevel() {
        return this.level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @ManyToOne
    @JoinColumn(name = "LOCATION_ID")
    public AdLocation getLocation() {
        return location;
    }

    public void setLocation(AdLocation location) {
        this.location = location;
    }

    public String getTitle() {
        //        if (this.getPrice() != null && StringUtils.isNoneBlank(this.getPrice().getTitle()))
        //            return this.getPrice().getTitle();
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(columnDefinition = "tinyint")
    public Short getType() {
        return this.type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Enumerated(EnumType.STRING)
    public TargetEnum getTarget() {
        return target;
    }

    public void setTarget(TargetEnum target) {
        this.target = target;
    }

    private String pic;

    //    @Transient
    //    public String getPic() {
    //        if (null != picFile) {
    //            return InitializationFile.downloadUrl + picFile.getLocation();
    //        }
    //        return this.pic;
    //    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    @Column(name = "PIC_URL")
    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public enum TargetEnum {
        _blank, _self
    }

    @Column(name = "USER_ID")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
