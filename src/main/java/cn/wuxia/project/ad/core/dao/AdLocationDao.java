/*
* Created on :24 Jun, 2014
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 songlin.li All right reserved.
*/
package cn.wuxia.project.ad.core.dao;

import java.util.List;

import cn.wuxia.project.basic.core.common.BaseCommonDao;
import cn.wuxia.project.ad.core.entity.AdLocation;
import cn.wuxia.project.ad.core.enums.AdLocationLoadTypeEnum;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import cn.wuxia.common.util.ArrayUtil;
import cn.wuxia.common.util.StringUtil;

@Component
public class AdLocationDao extends BaseCommonDao<AdLocation, String> {

    /**
     * find By Parent Id
     * @author songlin
     * @param parentId
     * @return
     */
    public List<AdLocation> findByParentId(String parentId) {
        return createCriteria(Restrictions.eqOrIsNull("parent.id", parentId)).addOrder(Order.asc("sortOrder")).list();
    }

    /**
     * 查找需要同步显示的广告区域
     * @author songlin
     * @param parentId
     * @return
     */
    public List<AdLocation> findByParentIdAndLoadtype(String parentId, AdLocationLoadTypeEnum loadType) {
        return createCriteria(Restrictions.eqOrIsNull("parent.id", parentId), Restrictions.eqOrIsNull("loadType", loadType)).addOrder(
                Order.asc("sortOrder")).list();
    }

    public List<AdLocation> findByLevel(int level) {
        return createCriteria(Restrictions.eq("level", level)).addOrder(Order.asc("sortOrder")).list();
    }

    /**
     * 根据父id查找所有子id
     * @author songlin
     * @param id
     * @return
     */
    public String[] findAllChildIds(String id) {
        Assert.notNull(id, "id 不能为空");
        String[] result = new String[] {};
        Object ids = createSQLQuery("select getAllChildAdLocation(?)", id).uniqueResult();
        if (StringUtil.isNotBlank(ids)) {
            String[] idArrays = StringUtil.split((String) ids, ",");
            for (String idstr : idArrays) {
                //除了当前id外
                if (!StringUtil.equals(idstr, id))
                    result = ArrayUtil.add(result, idstr);
            }
        }
        return result;
    }
}
