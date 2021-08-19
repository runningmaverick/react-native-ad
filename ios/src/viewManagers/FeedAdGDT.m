//
//  FeedAdGDT.m
//  datizhuanqian
//
//  Created by ivan zhang on 2019/9/19.
//  Copyright © 2019 Facebook. All rights reserved.
//

#import "FeedAdGDT.h"
#include "AdBoss.h"

#import "GDTNativeExpressAd.h"
#import "GDTNativeExpressAdView.h"

//@interface NativeExpressAdViewController : UIViewController <GDTNativeExpressAdDelegete>
@interface FeedAdGDT ()<GDTNativeExpressAdDelegete>
@property (nonatomic, strong) NSArray *expressAdViews;
@property (nonatomic, strong) GDTNativeExpressAd *nativeExpressAd;

//@property (strong, nonatomic) NSMutableArray<__kindof GDTNativeExpressAdView *> *expressAdViews;

@property(nonatomic, strong) NSString *_codeid;
@property(nonatomic) NSInteger _adwidth ;

@end

@implementation FeedAdGDT


- (void)setCodeId:(NSString *)codeid {
    self._codeid = codeid;
    NSLog(@"开始 加载Feed广告 codeid: %@", self._codeid);
    [self loadFeedAd];
}

- (void)setAdWidth:(NSString *)width {
    self._adwidth = [width integerValue];
    NSLog(@"开始 加载Feed广告 adwidth: %ld", self._adwidth);
    [self loadFeedAd];
}

/**
 加载Feed广告
 */
- (void)loadFeedAd{
    
    if(self._codeid == nil) {
        return;
    }
    
    if(!self._adwidth){
        self._adwidth  = 228;  //默认feed尺寸 228 * 150
    }
    
    if (!self.expressAdViews) {
        self.expressAdViews = [NSMutableArray arrayWithCapacity:20];
    }

    // 支持视频广告的 PlacementId 会混出视频与图片广告
   CGSize adSize = CGSizeMake(self._adwidth, self._adwidth * 0);
   self.nativeExpressAd = [[GDTNativeExpressAd alloc] initWithPlacementId:self._codeid adSize:adSize];
   self.nativeExpressAd.delegate = self;
   // 配置视频播放属性
   //self.nativeExpressAd.maxVideoDuration = (NSInteger)self.maxVideoDurationSlider.value;  // 如果需要设置视频最大时长，可以通过这个参数来进行设置
   //self.nativeExpressAd.videoAutoPlayOnWWAN = self.videoAutoPlaySwitch.on;
   //self.nativeExpressAd.videoMuted = self.videoMutedSwitch.on;
   [self.nativeExpressAd loadAd:1];

}


# pragma GDT
- (void)nativeExpressAdSuccessToLoad:(GDTNativeExpressAd *)nativeExpressAd views:(NSArray<__kindof GDTNativeExpressAdView *> *)views
{
   self.expressAdViews = [NSArray arrayWithArray:views];
   if (self.expressAdViews.count) {
       [self.expressAdViews enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
           GDTNativeExpressAdView *expressView = (GDTNativeExpressAdView *)obj;
           //UIViewController* viewController = [[[[UIApplication sharedApplication] delegate] window] rootViewController];
           expressView.controller = [AdBoss getRootVC];
           [expressView render];
       }];
   }
}

- (void)nativeExpressAdViewRenderSuccess:(GDTNativeExpressAdView *)nativeExpressAdView {
    BUD_Log(@"feed ad render success");
    [self addSubview:nativeExpressAdView];

    CGFloat adWidth = nativeExpressAdView.bounds.size.width;
    BUD_Log(@"adWidth %f", adWidth);
    CGFloat adHeight = nativeExpressAdView.bounds.size.height;
    BUD_Log(@"adHeight %f", adHeight);
    self.onAdLayout(@{
        @"width":@(adWidth),
        @"height":@(adHeight)
    });
}

- (void)nativeExpressAdRenderFail:(GDTNativeExpressAdView *)nativeExpressAdView {
    NSLog(@"Express Ad Render Fail");
}

- (void)nativeExpressAdFailToLoad:(GDTNativeExpressAd *)nativeExpressAd error:(NSError *)error
{
    NSLog(@"Express Ad Load Fail : %@",error);
}


- (void)nativeExpressAdViewClosed:(GDTNativeExpressAdView *)nativeExpressAdView {
    self.onAdClose(@{
        @"message": @"ad closed",
    });
}

- (void)nativeExpressAdViewClicked:(GDTNativeExpressAdView *)nativeExpressAdView {
    BUD_Log(@"feed ad clicked");
    self.onAdClick(@{
        @"message": @"ad been clicked",
    });
}

@end
