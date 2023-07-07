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
static bool _nativeHidden = false;

@implementation RNBootSplash

RCT_EXPORT_MODULE();

- (dispatch_queue_t)methodQueue {
  return dispatch_get_main_queue();
}

+ (bool)isLoadingViewHidden {
  return _loadingView == nil || [_loadingView isHidden];
}

+ (bool)hasResolveQueue {
  return _resolveQueue != nil;
}

+ (void)clearResolveQueue {
  if (![self hasResolveQueue])
    return;

  while ([_resolveQueue count] > 0) {
    RCTPromiseResolveBlock resolve = [_resolveQueue objectAtIndex:0];
    [_resolveQueue removeObjectAtIndex:0];
    resolve(@(true));
  }
}

+ (void)hideLoadingView {
  if (![self isLoadingViewHidden]) {
    _loadingView.hidden = YES;
    [_loadingView removeFromSuperview];
    _loadingView = nil;
  }

  return [RNBootSplash clearResolveQueue];
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
      || [self hasResolveQueue] // hide has already been called, abort init
      || RCTRunningInAppExtension())
    return;

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
    if ([self hasResolveQueue])
      [self hideLoadingView];
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

- (void)hideImpl:(RCTPromiseResolveBlock)resolve {
  if (_resolveQueue == nil)
    _resolveQueue = [[NSMutableArray alloc] init];

  [_resolveQueue addObject:resolve];

  if ([RNBootSplash isLoadingViewHidden] || RCTRunningInAppExtension())
    return [RNBootSplash clearResolveQueue];

  if (_nativeHidden)
    return [RNBootSplash hideLoadingView];
}

- (bool)visibleImpl {
  return ![RNBootSplash isLoadingViewHidden];
}

#ifdef RCT_NEW_ARCH_ENABLED

// New architecture

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:(const facebook::react::ObjCTurboModule::InitParams &)params {
  return std::make_shared<facebook::react::NativeRNBootSplashSpecJSI>(params);
}

- (void)hide:(RCTPromiseResolveBlock)resolve
      reject:(RCTPromiseRejectBlock)reject {
  [self hideImpl:resolve];
}

- (NSNumber *)visible {
  return @([self visibleImpl]);
}

#else

// Old architecture

RCT_EXPORT_METHOD(hide:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject) {
  [self hideImpl:resolve];
}

RCT_EXPORT_BLOCKING_SYNCHRONOUS_METHOD(visible) {
  return @([self visibleImpl]);
}

#endif

@end
