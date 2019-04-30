/*
 * Created on :20 Mar, 2014 Author :songlin Change History Version Date Author
 * Reason <Ver.No> <date> <who modify> <reason>
 */
package cn.wuxia.project.ad.core.bean;

import java.io.Serializable;

/**
 * suggest box 统一输出格式 <br>
 * [ticket id] Description of the class
 * 
 * @author songlin @ Version : V<Ver.No> <20 Mar, 2014>
 */
public class SuggestBoxVo implements Serializable {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -5828946586086763700L;

    private String id;

    private String name;

    private String shortName;

    private String mergerName;

    private String pinyin;

    private String simplePinyin;

    private Long parentId;

    public SuggestBoxVo() {
    }

    public SuggestBoxVo(String id, String enName, String name, String shortName, String mergerName, String pinyin, String simplePinyin) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.mergerName = mergerName;
        this.pinyin = pinyin;
        this.simplePinyin = simplePinyin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getMergerName() {
        return mergerName;
    }

    public void setMergerName(String mergerName) {
        this.mergerName = mergerName;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getSimplePinyin() {
        return simplePinyin;
    }

    public void setSimplePinyin(String simplePinyin) {
        this.simplePinyin = simplePinyin;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

}
