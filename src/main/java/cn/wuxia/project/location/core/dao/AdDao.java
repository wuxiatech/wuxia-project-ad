/*
* Created on :24 Jun, 2014
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.com All right reserved.
*/
package cn.wuxia.project.location.core.dao;

import java.util.List;
import java.util.Map;

import cn.wuxia.project.location.core.entity.Ad;
import cn.wuxia.project.basic.core.common.BaseCommonDao;
import org.springframework.stereotype.Component;

import cn.wuxia.common.orm.query.Pages;
import cn.wuxia.common.util.DateUtil;

@Component
public class AdDao extends BaseCommonDao<Ad, String> {

    public List<Ad> findByLocationIdForGenerator(String locationId, Integer adLength) {
        String hql = "from Ad where location.id = ? and (beginTime < ? or beginTime is null) "
                + " and (effectiveTime > ? or effectiveTime is null) and selected = ? order by level desc";
        Object[] value = new Object[] { locationId, DateUtil.newInstanceDate(), DateUtil.newInstanceDate(), true };
        if (null != adLength && adLength > 0){
            Pages<Ad> page = findPage(new Pages(1, adLength.intValue()), hql, value);
            return page.getResult();
        }else
            return find(hql, value);
    }

    /**
     * 查询为空里面 为您推荐 中的换一批
     * @author wuwenhao
     * @param term 条件
     * @return
     */
    public Pages<Map<String, Object>> findBuyChange(Pages page, String oldTerm, String newTerm) {
        String sql = "SELECT ad.url url,ad.PIC_URL picUrl,ufi.LOCATION location, ad.TITLE title,ad.FEE fee from p_ad ad LEFT JOIN upload_file_info ufi ON ufi.ID = ad.PIC_FILE_ID "
                + "  where (ad.url like ? or ad.url like ?) AND ad.PIC_FILE_ID IS NOT NULL and fee <> '' AND ad.FEE IS NOT NULL and ad.is_Obsolete_Date is null order by ad.level desc";
        return (Pages<Map<String, Object>>) findPageBySql(page, sql, oldTerm, newTerm);
    }

    /**
     * 根据广告位code查找拥有的广告 <br/>
     * 供管理员使用
     * @author CaRson.Yan
     * @param code
     * @param userId
     * @return
     */
    public List<Map<String, Object>> findAdByLocationCode(String code, String userId) {
        String sql = "SELECT pAd.ID pId, pAd.ALIAS pAlias, pAd.TITLE title, pAd.URL pUrl FROM p_ad pAd "
                + "LEFT JOIN p_ad_location pAl ON pAl.ID = pAd.LOCATION_ID LEFT JOIN u_user_doctor u ON u.ID = pAd.USER_ID "
                + " WHERE pAl.CODE = ? AND (pAd.BEGIN_TIME < ? OR pAd.EFFECTIVE_TIME > ? ) AND pAd.is_Obsolete_Date is null ";
        return (List<Map<String, Object>>) queryForMap(sql, code, DateUtil.newInstanceDate(), DateUtil.newInstanceDate());
    }

}
