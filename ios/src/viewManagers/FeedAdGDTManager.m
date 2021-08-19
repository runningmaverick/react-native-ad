//
//  FeedAdGTDManager.m
//  datizhuanqian
//
//  Created by ivan zhang on 2019/9/19.
//  Copyright © 2019 Facebook. All rights reserved.
//

#import <React/RCTViewManager.h>
#import "FeedAdGDT.h"

@interface FeedAdGDTManager : RCTViewManager

@end


@implementation FeedAdGDTManager

RCT_EXPORT_MODULE(FeedAdGDT)

RCT_EXPORT_VIEW_PROPERTY(onAdLayout, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onAdError, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onAdClick, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onAdClose, RCTBubblingEventBlock)

- (FeedAdGDT *)view
{
  return [[FeedAdGDT alloc] init];
}

RCT_CUSTOM_VIEW_PROPERTY(codeid, NSString, FeedAdGDT)
{
  if (json) {
    [view  setCodeId:json];
  }
}

RCT_CUSTOM_VIEW_PROPERTY(width, NSString, FeedAdGDT)
{
  if (json) {
    [view  setAdWidth:json];
  }
}

@end
