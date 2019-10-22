/*
* Created on :24 Jun, 2014
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 songlin.li All right reserved.
*/
package cn.wuxia.project.ad.core.service;

import java.util.List;

import cn.wuxia.project.ad.core.entity.AdLocation;
import cn.wuxia.project.common.service.CommonService;
import cn.wuxia.common.orm.query.Pages;

public interface AdLocationService extends CommonService<AdLocation, String> {

    /**
     * 根据父节点查找子节点
     * @author songlin
     * @param parentId
     * @return
     */
    public List<AdLocation> findAdLocationByParentId(String parentId);

    /**
     * 根据广告排位级别查找广告位
     * @author songlin
     * @param level
     * @return
     */
    public List<AdLocation> findAdLocationByLevel(int level);

    /**
     * 找出所有的可配置广告位(level > 1)
     * @author songlin
     * @param page
     * @return
     */
    public Pages<AdLocation> findAllAdLocation(Pages<AdLocation> page);

    /**
     * 清除指定广告的缓存
     * @author songlin
     * @param pageId
     * @param isAsync
     */
    public void cleanAdCacheByPageId(String pageId, boolean isAsync);

    /**
     * 清除指定广告的缓存
     * @author songlin
     * @param pageId
     * @param isAsync
     */
    public void cleanAdCacheByPageCode(String code, boolean isAsync);

    /**
     * 根据页面生成，拼接广告html
     * @author songlin
     * @param pageId
     * @return
     */
    public String generatePageAdByPageId(String pageId, boolean isAsync);

    /**
     * 根据页面生成，拼接广告html
     * @author songlin
     * @param pageId
     * @return
     */
    public String generatePageAdByPageCode(String code, boolean isAsync);

    /**
     * 复制模版
     * <pre>adLocationService.copy(3L, 1L);</pre>
     * @author songlin
     * @param id 传递要复制的模版的ID
     * @param parentId 传递要复制的模版的父ID
     * @throws Exception
     */
    public void copy(String id, String parentId) throws Exception;

    /**
     * 当更新加载方式时，需要把子模版也修改为同父一样的加载方式
     * @author songlin
     * @param adLocation
     */
    public void saveAndFlushAllChild(AdLocation adLocation);
}
