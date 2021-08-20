//
//  SplashAd.m
//
//  Created by ivan zhang on 2019/9/19.
//  Copyright © 2019 Facebook. All rights reserved.
//

#import "SplashAdGDT.h"

@interface SplashAdGDT () <GDTSplashAdDelegate>
 @property (strong, nonatomic) GDTSplashAd *_splash;
@end

@implementation SplashAdGDT

RCT_EXPORT_MODULE();

+ (BOOL)requiresMainQueueSetup {
    return YES;
}

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

- (NSArray<NSString *> *)supportedEvents {
    return @[
        @"SplashAd-onAdClose",
        @"SplashAd-onAdSkip",
        @"SplashAd-onAdError",
        @"SplashAd-onAdClick",
        @"SplashAd-onAdShow"
    ];
}

RCT_EXPORT_METHOD(loadSplashAd:(NSDictionary *)options resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)
{
    
    NSString  *codeid = options[@"codeid"];
    if(codeid == nil) {
        return;
    }
    
    NSString  *appid = options[@"appid"];
    if(appid != nil) {
        [AdBoss initTx:appid];
    }
    
    NSLog(@"Bytedance splash 开屏ios 代码位id %@", codeid);
    
    // splash LoadAd 逻辑
    GDTSplashAd *splash = [[GDTSplashAd alloc] initWithPlacementId:codeid];
    self._splash = splash;
    splash.delegate = self; //设置代理
    //根据iPhone设备不同设置不同背景图
    /*
    if ([[UIScreen mainScreen] bounds].size.height >= 568.0f) {
     splash.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"LaunchImage-568h"]];
    } else {
     splash.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"LaunchImage"]];
    }
    */
    splash.fetchDelay = 3; //开发者可以设置开屏拉取时间，超时则放弃展示
    [splash loadFullScreenAd];
    
    resolve(@"结果：Splash Ad 成功");
}

- (void)splashAdDidLoad:(GDTSplashAd *)splashAd {
    if ([splashAd isAdValid]) {
        UIWindow *window = [[UIApplication sharedApplication] keyWindow];
        [self._splash showFullScreenAdInWindow:window withLogoImage:nil skipView:nil];
    }
}

- (void)splashAdSuccessPresentScreen {
	NSLog(@"SplashAd-onAdShow ...");
    [self sendEventWithName:@"SplashAd-onAdShow" body:@""];
}

- (void)splashAdClicked {
	NSLog(@"SplashAd-onAdClick ...");
    [self sendEventWithName:@"SplashAd-onAdClick" body:@"..."];
}

- (void)splashAdClosed {
	NSLog(@"SplashAd-onAdClose ...");
    [self sendEventWithName:@"SplashAd-onAdClose" body:@""];
}

- (void)splashAdFailToPresent:(GDTSplashAd *)splashAd
withError:(NSError *)error
{
    NSLog(@"%s%@",__FUNCTION__,error);
    [self sendEventWithName:@"SplashAd-onAdError" body:[NSString stringWithFormat:@"%@", error]];
}

@end


