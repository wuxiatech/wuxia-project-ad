/*
* Created on :24 Jun, 2014
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.com All right reserved.
*/
package cn.wuxia.project.location.core.service.impl;

import cn.wuxia.common.orm.query.Pages;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.util.reflection.ReflectionUtil;
import cn.wuxia.project.location.core.dao.AdDao;
import cn.wuxia.project.location.core.entity.Ad;
import cn.wuxia.project.location.core.entity.AdLocation;
import cn.wuxia.project.location.core.service.AdService;
import cn.wuxia.project.common.dao.CommonDao;
import cn.wuxia.project.common.service.impl.CommonServiceImpl;
import cn.wuxia.project.common.support.Constants;
import cn.wuxia.project.storage.core.model.UploadFileInfo;
import cn.wuxia.project.storage.core.model.UploadFileSetInfo;
import cn.wuxia.project.storage.core.service.UploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AdServiceImpl extends CommonServiceImpl<Ad, String> implements AdService {

    @Autowired
    private AdDao adDao;

    @Autowired
    private UploadFileService uploadFileService;

    @Override
    protected CommonDao<Ad, String> getCommonDao() {
        return adDao;
    }

    @Override
    public List<Ad> findByLocationId(String locationId) {
        return adDao.findBy("location.id", locationId, "level", false);
    }

    @Override
    public List<Ad> findByLocationIdForGenerator(String locationId, Integer adLength) {
        return adDao.findByLocationIdForGenerator(locationId, adLength);
    }

    @Override
    public void deletePic(String id, Long fileInfoId) {
        if (id != null) {
            Ad ad = findById(id);
            ad.setPicFile(null);
            save(ad);
        }
        //uploadFileService.deletePicture(fileInfoId);
    }

    @Override
    public Pages<Map<String, Object>> findBuyChange(Pages page, String oldTerm, String newTerm) {
        return adDao.findBuyChange(page, oldTerm, newTerm);
    }

    @Override
    public List<Map<String, Object>> findAdByLocationCode(String code, String userId) {
        return adDao.findAdByLocationCode(code, userId);
    }

    @Override
    @Cacheable(key = Constants.CACHED_KEY_DEFAULT + "+#id", value = Constants.CACHED_VALUE_BASE)
    public String generatePageAdById(String id) {
        Ad ad = findById(id);
        String template = ad.getLocation().getTemplateContent();
        Map<String, Object> param = beanToMap(ad);
        return StringUtil.replaceKeysSimple(param, template);
    }

    /**
     * AD 转为Map
     * @author songlin
     * @param ad
     * @return
     */
    private final Map<String, Object> beanToMap(Ad ad) {
        Map<String, Object> returnMap = new HashMap<String, Object>();
        Field[] fields = Ad.class.getDeclaredFields();
        for (Field f : fields) {
            Class<?> c = f.getType();
            if (!StringUtil.equalsIgnoreCase("serialVersionUID", f.getName())
                    && !StringUtil.equalsIgnoreCase(c.getName(), AdLocation.class.getName())
                    && !StringUtil.equalsIgnoreCase(c.getName(), UploadFileSetInfo.class.getName())
                    && !StringUtil.equalsIgnoreCase(c.getName(), UploadFileInfo.class.getName())) {
                try {
                    Object value = ReflectionUtil.invokeGetterMethod(ad, f.getName());
                    returnMap.put(f.getName(), value);
                } catch (Exception e) {
                    logger.warn("属性：" + f.getName() + " 不能读取,类型是：" + c.getName());
                }
            }
        }
        return returnMap;
    }
}
