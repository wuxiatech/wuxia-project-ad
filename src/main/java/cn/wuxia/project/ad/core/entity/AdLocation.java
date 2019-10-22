/*
* Created on :24 Jun, 2014
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 songlin.li All right reserved.
*/
package cn.wuxia.project.ad.core.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import cn.wuxia.project.ad.core.enums.AdLocationLoadTypeEnum;
import cn.wuxia.project.common.model.ModifyInfoEntity;
import cn.wuxia.common.util.StringUtil;

/**
 * The persistent class for the p_ad_page database table.
 * 
 */
@Entity
@Table(name = "p_ad_location")
@Where(clause = ModifyInfoEntity.ISOBSOLETE_DATE_IS_NULL)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "parent", "ads", "violations", "infoEntity" })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AdLocation extends ModifyInfoEntity {
    private static final long serialVersionUID = 1L;

    private String code;

    private String name;

    private AdLocation parent;

    private Integer sortOrder;

    private int level;

    private String templateContent;

    private Integer adLength;

    private AdLocationLoadTypeEnum loadType;

    public AdLocation() {
        super();
    }

    public AdLocation(String id) {
        super(id);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "NAME")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    public AdLocation getParent() {
        return parent;
    }

    public void setParent(AdLocation parent) {
        this.parent = parent;
    }

    @Column(name = "SORT_ORDER", columnDefinition = "tinyint")
    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Column(name = "TEMPLATE_CONTENT", columnDefinition = "text")
    @JsonIgnore
    public String getTemplateContent() {
        return templateContent;
    }

    public void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
    }

    @Column(columnDefinition = "tinyint")
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Column(name = "AD_LENGTH", columnDefinition = "smallint")
    public Integer getAdLength() {
        return adLength;
    }

    public void setAdLength(Integer adLength) {
        this.adLength = adLength;
    }

    @Column(name = "LOAD_TYPE")
    @Enumerated(EnumType.STRING)
    public AdLocationLoadTypeEnum getLoadType() {
        return loadType;
    }

    public void setLoadType(AdLocationLoadTypeEnum loadType) {
        this.loadType = loadType;
    }

    private String parentId;

    @Transient
    public String getParentId() {
        if (this.getParent() != null)
            return this.getParent().getId();
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
        if (StringUtil.isNotBlank(this.parentId)) {
            this.parent = new AdLocation(this.parentId);
        }
    }

    private List<Ad> ads;

    @Transient
    public List<Ad> getAds() {
        return ads;
    }

    public void setAds(List<Ad> ads) {
        this.ads = ads;
    }
}
