#import "RNBootSplash.h"

#import <React/RCTUtils.h>

#if RCT_NEW_ARCH_ENABLED
#import <React/RCTFabricSurfaceHostingProxyRootView.h>
#import <React/RCTSurfaceHostingView.h>
#else
#import <React/RCTRootView.h>
#endif

static NSMutableArray<RCTPromiseResolveBlock> *_resolveQueue = nil;
static UIView *_loadingView = nil;
static UIView *_rootView = nil;
static bool _fade = false;
static bool _nativeHidden = false;

@implementation RNBootSplash

RCT_EXPORT_MODULE();

+ (BOOL)requiresMainQueueSetup {
  return NO;
}

- (dispatch_queue_t)methodQueue {
  return dispatch_get_main_queue();
}

+ (bool)isLoadingViewVisible {
  return _loadingView != nil && ![_loadingView isHidden];
}

+ (void)clearResolveQueue {
  if (_resolveQueue == nil) {
    return;
  }

  while ([_resolveQueue count] > 0) {
    RCTPromiseResolveBlock resolve = [_resolveQueue objectAtIndex:0];
    [_resolveQueue removeObjectAtIndex:0];
    resolve(@(true));
  }
}

+ (void)hideLoadingView {
  if (![self isLoadingViewVisible]) {
    return [RNBootSplash clearResolveQueue];
  }

  if (_fade) {
    dispatch_async(dispatch_get_main_queue(), ^{
      [UIView transitionWithView:_rootView
                        duration:0.250
                         options:UIViewAnimationOptionTransitionCrossDissolve
                      animations:^{
        _loadingView.hidden = YES;
      }
                      completion:^(__unused BOOL finished) {
        [_loadingView removeFromSuperview];
        _loadingView = nil;

        return [RNBootSplash clearResolveQueue];
      }];
    });
  } else {
    _loadingView.hidden = YES;
    [_loadingView removeFromSuperview];
    _loadingView = nil;

    return [RNBootSplash clearResolveQueue];
  }
}

+ (void)initWithStoryboard:(NSString * _Nonnull)storyboardName
                  rootView:(UIView * _Nullable)rootView {
  if (rootView == nil
#ifdef RCT_NEW_ARCH_ENABLED
      || ![rootView isKindOfClass:[RCTFabricSurfaceHostingProxyRootView class]]
#else
      || ![rootView isKindOfClass:[RCTRootView class]]
#endif
      || _rootView != nil
      || _resolveQueue != nil // hide has already been called, abort init
      || RCTRunningInAppExtension()) {
    return;
  }

#ifdef RCT_NEW_ARCH_ENABLED
  RCTFabricSurfaceHostingProxyRootView *proxy = (RCTFabricSurfaceHostingProxyRootView *)rootView;
  _rootView = (RCTSurfaceHostingView *)proxy.surface.view;
#else
  _rootView = (RCTRootView *)rootView;
#endif

  UIStoryboard *storyboard = [UIStoryboard storyboardWithName:storyboardName bundle:nil];

  _loadingView = [[storyboard instantiateInitialViewController] view];
  _loadingView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
  _loadingView.frame = _rootView.bounds;
  _loadingView.center = (CGPoint){CGRectGetMidX(_rootView.bounds), CGRectGetMidY(_rootView.bounds)};
  _loadingView.hidden = NO;

  [_rootView addSubview:_loadingView];

  [NSTimer scheduledTimerWithTimeInterval:0.35
                                  repeats:NO
                                    block:^(NSTimer * _Nonnull timer) {
    // wait for native iOS launch screen to fade out
    _nativeHidden = true;

    // hide has been called before native launch screen fade out
    if (_resolveQueue != nil) {
      [self hideLoadingView];
    }
  }];

  [[NSNotificationCenter defaultCenter] addObserver:self
                                           selector:@selector(onJavaScriptDidLoad)
                                               name:RCTJavaScriptDidLoadNotification
                                             object:nil];

  [[NSNotificationCenter defaultCenter] addObserver:self
                                           selector:@selector(onJavaScriptDidFailToLoad)
                                               name:RCTJavaScriptDidFailToLoadNotification
                                             object:nil];
}

+ (void)onJavaScriptDidLoad {
  [[NSNotificationCenter defaultCenter] removeObserver:self];
}

+ (void)onJavaScriptDidFailToLoad {
  [self hideLoadingView];
  [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (NSDictionary *)constantsToExport {
  return @{
    @"statusBarHeight": @(0),
    @"navigationBarHeight": @(0)
  };
}

- (void)hideImpl:(BOOL)fade
         resolve:(RCTPromiseResolveBlock)resolve {
  if (_resolveQueue == nil) {
    _resolveQueue = [[NSMutableArray alloc] init];
  }

  [_resolveQueue addObject:resolve];

  if (![RNBootSplash isLoadingViewVisible] || RCTRunningInAppExtension()) {
    return [RNBootSplash clearResolveQueue];
  }

  _fade = fade;

  if (_nativeHidden) {
    return [RNBootSplash hideLoadingView];
  }
}

- (void)isVisibleImpl:(RCTPromiseResolveBlock)resolve {
  resolve(@([RNBootSplash isLoadingViewVisible]));
}

#ifdef RCT_NEW_ARCH_ENABLED

// New architecture

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:(const facebook::react::ObjCTurboModule::InitParams &)params {
  return std::make_shared<facebook::react::NativeRNBootSplashSpecJSI>(params);
}

- (facebook::react::ModuleConstants<JS::NativeRNBootSplash::Constants::Builder>)getConstants {
  return [self constantsToExport];
}

- (void)hide:(BOOL)fade
     resolve:(RCTPromiseResolveBlock)resolve
      reject:(RCTPromiseRejectBlock)reject {
  [self hideImpl:fade resolve:resolve];
}

- (void)isVisible:(RCTPromiseResolveBlock)resolve
           reject:(RCTPromiseRejectBlock)reject {
  [self isVisibleImpl:resolve];
}

#else

// Old architecture

RCT_EXPORT_METHOD(hide:(BOOL)fade
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject) {
  [self hideImpl:fade resolve:resolve];
}

RCT_EXPORT_METHOD(isVisible:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject) {
  [self isVisibleImpl:resolve];
}

#endif

@end
