/*
 * Created on :24 Jun, 2014
 * Author     :songlin
 * Change History
 * Version       Date         Author           Reason
 * <Ver.No>     <date>        <who modify>       <reason>
 * Copyright 2014-2020 songlin.li All right reserved.
 */
package cn.wuxia.project.ad.core.service.impl;

import cn.wuxia.common.exception.AppDaoException;
import cn.wuxia.common.exception.AppServiceException;
import cn.wuxia.common.orm.query.Pages;
import cn.wuxia.common.util.ArrayUtil;
import cn.wuxia.common.util.ListUtil;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.util.reflection.BeanUtil;
import cn.wuxia.common.util.reflection.ReflectionUtil;
import cn.wuxia.project.ad.core.dao.AdLocationDao;
import cn.wuxia.project.ad.core.entity.Ad;
import cn.wuxia.project.ad.core.entity.AdLocation;
import cn.wuxia.project.ad.core.service.AdLocationService;
import cn.wuxia.project.ad.core.service.AdService;
import cn.wuxia.project.basic.support.ApplicationPropertiesUtil;
import cn.wuxia.project.common.dao.CommonDao;
import cn.wuxia.project.common.service.impl.CommonServiceImpl;
import cn.wuxia.project.common.support.Constants;
import cn.wuxia.project.storage.core.model.UploadFileInfo;
import cn.wuxia.project.storage.core.model.UploadFileSetInfo;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AdLocationServiceImpl extends CommonServiceImpl<AdLocation, String> implements AdLocationService {

    @Autowired
    private AdLocationDao adLocationDao;

    @Autowired
    private AdService adService;

    @Override
    protected CommonDao<AdLocation, String> getCommonDao() {
        return adLocationDao;
    }

    @Override
    public List<AdLocation> findAdLocationByParentId(String parentId) {
        return adLocationDao.findByParentId(parentId);
    }

    @Override
    public Pages<AdLocation> findAllAdLocation(Pages<AdLocation> page) {
        return adLocationDao.findPage(page, Restrictions.gt("level", 1));
    }

    @Override
    public List<AdLocation> findAdLocationByLevel(int level) {
        return adLocationDao.findByLevel(level);
    }

    /**
     * 删除广告位时也删除广告
     */
    @Override
    public void delete(String id) {
        AdLocation adl = findById(id);
        if (adl.getAds() != null) {
            for (Ad ad : adl.getAds()) {
                adService.delete(ad.getId());
            }
        }
        super.delete(id);
    }

    @Override
    @CacheEvict(key = Constants.CACHED_KEY_DEFAULT + "+#pageId+#isAsync", value = Constants.CACHED_VALUE_BASE)
    public void cleanAdCacheByPageId(String pageId, boolean isAsync) {

    }

    @Override
    @CacheEvict(key = Constants.CACHED_KEY_DEFAULT + "+#code+#isAsync", value = Constants.CACHED_VALUE_BASE)
    public void cleanAdCacheByPageCode(String code, boolean isAsync) {

    }

    @Override
    @Cacheable(key = Constants.CACHED_KEY_DEFAULT + "+#pageId+#isAsync", value = Constants.CACHED_VALUE_BASE)
    public String generatePageAdByPageId(String pageId, boolean isAsync) {
        AdLocation pageAdLocation = findById(pageId);
        String returnHtml = generateHtml(pageAdLocation, isAsync);
        //        //得到模版中所有的key
        String[] keys = StringUtil.getTemplateKey(returnHtml);
        if (ArrayUtil.isEmpty(keys)) {
            return returnHtml;
        }
        //Map<String, Object> param = beanToMap(new Ad());
        Map<String, Object> param = Maps.newHashMap();
        //替换全局参数ctx
        for (String key : keys) {
            if (StringUtil.equalsIgnoreCase("ctx", key)) {
                param.put("ctx", ApplicationPropertiesUtil.getPropertiesValue("ctx.servicename"));
            } else {
                param.put(key, "");

            }
        }
        //替换空白参数
        return StringUtil.replaceKeysSimple(param, returnHtml);
    }

    @Override
    @Cacheable(key = Constants.CACHED_KEY_DEFAULT + "+#code+#isAsync", value = Constants.CACHED_VALUE_BASE)
    public String generatePageAdByPageCode(String code, boolean isAsync) {
        StringBuffer html = new StringBuffer();
        List<AdLocation> list = getCommonDao().findBy("code", code);
        for (AdLocation adl : list) {
            html.append(generatePageAdByPageId(adl.getId(), isAsync));
        }
        return html.toString();
    }

    /**
     * @param adls
     * @param isAsync 默认为false（同步）;
     * @author songlin
     */
    private String generateHtml(AdLocation adls, boolean isAsync) {
        StringBuffer html = new StringBuffer();

        if (!isAsync && adls.getLoadType() != null && StringUtil.equalsIgnoreCase("async", adls.getLoadType().name())) {
            logger.debug("本模版【" + adls.getName() + "-{}】采用异步加载，已忽略。", adls.getCode());
            return "<div class='" + adls.getCode() + "' data-id='" + adls.getId() + "'></div>";
        }
        String adHtml = generateAD(adls);
        if (StringUtil.isNotBlank(adHtml)) {
            html.append(adHtml);
        }
        String navHtml = generateNav(adls);
        if (StringUtil.isNotBlank(navHtml)) {
            html.append(navHtml);
        }
        //如果还存在子广告
        List<AdLocation> childAdl = adLocationDao.findByParentId(adls.getId());
        final String template = StringUtil.isBlank(adls.getTemplateContent()) ? "" : adls.getTemplateContent();
        if (ListUtil.isNotEmpty(childAdl)) {
            Map<String, Object> m = Maps.newHashMap();
            int index = 0;
            for (AdLocation adl : childAdl) {
                String childHtml = generateHtml(adl, isAsync);
                m.put(adl.getCode(), childHtml);
                m.put("index", index++);
            }
            String currentHtml = StringUtil.replaceKeysSimple(m, template);
            //当前的html
            html.append(currentHtml);
        }
        //String  

        if (StringUtil.isNotBlank(html.toString())) {
            if (StringUtil.isNotBlank(adls.getCode())) {
                Map<String, Object> m = Maps.newHashMap();
                m.put(adls.getCode(), template);
                return StringUtil.replaceKeysSimple(m, html.toString());
            }
            return html.toString();
        } else {
            return template;
        }
    }

    /**
     * 替换AD 表的内容
     *
     * @param adl
     * @return
     * @author songlin
     */
    private String generateAD(AdLocation adl) {
        //当前的位置的模版
        StringBuffer htmlTemplate = new StringBuffer();
        String templateContent = adl.getTemplateContent();
        if (StringUtil.isBlank(templateContent)) {
            return htmlTemplate.toString();
        }
        //当前位置拥有的广告
        String[] keys = StringUtil.getTemplateKey(templateContent);
        List<Ad> ads = adService.findByLocationIdForGenerator(adl.getId(), adl.getAdLength());
        if (ListUtil.isEmpty(ads)) {
            //ads.add(new Ad());
        }
        //如果采用异步加载，修改图片加载方式为非lazy加载，否则同步src属性不存在被data-original lazy加载
        if (StringUtil.equalsIgnoreCase("async", adl.getLoadType().name())) {
            templateContent = StringUtil.replace(templateContent, " data-original=", " src=");
        }
        int index = 0;
        for (Ad adOrginal : ads) {
            Ad ad = new Ad();
            // FIXME 防止直接操作原AD对象，会刷新数据库数据
            BeanUtils.copyProperties(adOrginal, ad);
            ad.setId(null);

            //如果以"/"开头，则默认加上域名
            if (StringUtil.indexOf(ad.getUrl(), "/") == 0) {
                ad.setUrl(ApplicationPropertiesUtil.getPropertiesValue("ctx.servicename") + ad.getUrl());
            }

            if (StringUtil.indexOf(ad.getPicUrl(), "/") == 0) {
                ad.setPicUrl(ApplicationPropertiesUtil.getPropertiesValue("ctx.servicename") + ad.getPicUrl());
            }

            //如果图片的链接为空则使用广告的链接代替
            if (StringUtil.isBlank(ad.getPicUrl()) && StringUtil.isNotBlank(ad.getUrl())) {
                ad.setPicUrl(ad.getUrl());
            }
            Map<String, Object> param = beanToMap(ad);

            param.put("index", index++);
            //得到模版中所有的key
            //替换广告属性
            htmlTemplate.append(StringUtil.replaceKeysSimple(param, templateContent));
        }

        return htmlTemplate.toString();
    }

    /**
     * 替换AD 表的内容
     *
     * @param adl
     * @return
     * @author songlin
     */
    private String generateNav(AdLocation adl) {
        //当前的位置的模版
        StringBuffer htmlTemplate = new StringBuffer();
        String templateContent = adl.getTemplateContent();
        if (StringUtil.isBlank(templateContent)) {
            return htmlTemplate.toString();
        }
        //当前位置拥有的广告
        String[] keys = StringUtil.getTemplateKey(templateContent);
        //        ServiceMenu sm = serviceMenuService.findByName(adl.getCode());
        //        if (sm != null && ListUtil.isNotEmpty(sm.getSubMenuService())) {
        //            int index = 0;
        //            for (ServiceMenuRef smr : sm.getSubMenuService()) {
        //                Services s = smr.getServices();
        //                Map<String, Object> param = Maps.newHashMap();
        //                param.put("index", index++);
        //                param.put("serviceCode", s.getServiceCode());
        //                param.put("serviceName", s.getServiceName());
        //                //替换链接
        //                htmlTemplate.append(StringUtil.replaceKeysSimple(param, templateContent));
        //            }
        //        }
        return htmlTemplate.toString();
    }

    /**
     * AD 转为Map
     *
     * @param ad
     * @return
     * @author songlin
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

    @Override
    public void copy(String id, String parentId) throws Exception {
        AdLocation adl = findById(id);
        AdLocation adlcopy = (AdLocation) BeanUtil.cloneBean(adl);
        adlcopy.setId(null);
        adlcopy.setName(adlcopy.getName() + "-copy");
        adlcopy.setParentId(parentId);
        save(adlcopy);
        List<AdLocation> adls = findAdLocationByParentId(id);
        for (AdLocation l : adls) {
            copy(l.getId(), adlcopy.getId());
        }
    }

    @Override
    public void saveAndFlushAllChild(AdLocation adLocation) {
        String[] ids = adLocationDao.findAllChildIds(adLocation.getId());
        try {
            save(adLocation);
        } catch (AppDaoException e) {
            throw new AppServiceException("保存失败");
        }
        if (ArrayUtils.isNotEmpty(ids)) {
            List<AdLocation> childs = adLocationDao.findIn("id", ids);
            for (AdLocation adl : childs) {
                adl.setLevel(adl.getParent().getLevel() + 1);
                adl.setLoadType(adLocation.getLoadType());
            }
            try {
                batchSave(childs);
            } catch (AppDaoException e) {
                throw new AppServiceException("保存失败");
            }
        }
    }

}
