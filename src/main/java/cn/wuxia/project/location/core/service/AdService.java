/*
* Created on :24 Jun, 2014
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.com All right reserved.
*/
package cn.wuxia.project.location.core.service;

import java.util.List;
import java.util.Map;

import cn.wuxia.project.location.core.entity.Ad;
import cn.wuxia.project.common.service.CommonService;
import cn.wuxia.common.orm.query.Pages;

public interface AdService extends CommonService<Ad, String> {

    /**
     * 根据广告位查找拥有的广告
     * @author songlin
     * @param locationId
     * @return
     */
    public List<Ad> findByLocationId(String locationId);

    /**
     * 查找可用的广告并且按照level倒序排序
     * @author songlin
     * @param locationId
     * @return
     */
    public List<Ad> findByLocationIdForGenerator(String locationId, Integer adLength);

    /**
     * 删除广告
     * @author songlin
     * @param id
     * @param fileUrl
     */
    public void deletePic(String id, Long fileInfoId);

    /**
     * 查询为空里面 为您推荐 中的换一批
     * @author wuwenhao
     * @param term 条件
     * @return
     */
    public Pages<Map<String, Object>> findBuyChange(Pages page, String oldTerm, String newTerm);

    /**
     * 根据广告位code查找拥有的广告
     * @author CaRson.Yan
     * @param code
     * @return
     */
    public List<Map<String, Object>> findAdByLocationCode(String code, String userId);

    /**
     * 通过广告id生成广告
     * @author CaRson.Yan
     * @param id
     * @return
     */
    public String generatePageAdById(String id);
}
