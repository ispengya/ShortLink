package com.ispengya.shortlink.project.dao;

/**
 * @author ispengya
 * @date 2023/12/9 10:01
 */
//@Repository
//public class ShortLinkGoToDao extends ServiceImpl<ShortLinkGotoMapper, ShortLinkGotoDO> {
//
//    public ShortLinkGotoDO getByFullShortUrl(String fullShortUrl) {
//        return lambdaQuery()
//                .eq(ShortLinkGotoDO::getFullShortUrl,fullShortUrl)
//                .eq(ShortLinkGotoDO::getDelFlag, YesOrNoEnum.YES.getCode())
//                .one();
//    }
//
//    public ShortLinkGotoDO getByFullShortUrlWithOut(String fullShortUrl) {
//        return lambdaQuery()
//                .eq(ShortLinkGotoDO::getFullShortUrl,fullShortUrl)
//                .one();
//    }
//
//    public void updateStatus(String username, String fullShortUrl,YesOrNoEnum yesOrNoEnum) {
//        lambdaUpdate()
//                .eq(ShortLinkGotoDO::getUsername,username)
//                .eq(ShortLinkGotoDO::getFullShortUrl,fullShortUrl)
//                .set(ShortLinkGotoDO::getDelFlag,yesOrNoEnum.getCode())
//                .update();
//    }
//
//    public ShortLinkGotoDO getByFullShortUrlAndUserName(String fullShortUrl, String username) {
//        return lambdaQuery()
//                .eq(ShortLinkGotoDO::getUsername,username)
//                .eq(ShortLinkGotoDO::getFullShortUrl,fullShortUrl)
//                .one();
//    }
//
//    public void deleteByConditions(ShortLinkGotoDO shortLinkGoTo) {
//        LambdaQueryWrapper<ShortLinkGotoDO> linkGotoQueryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
//                .eq(ShortLinkGotoDO::getFullShortUrl, shortLinkGoTo.getFullShortUrl())
//                .eq(ShortLinkGotoDO::getUsername,shortLinkGoTo.getUsername())
//                .eq(ShortLinkGotoDO::getGid, shortLinkGoTo.getGid());
//        baseMapper.delete(linkGotoQueryWrapper);
//    }
//}
