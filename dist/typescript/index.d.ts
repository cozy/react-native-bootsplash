export declare type VisibilityStatus = "visible" | "hidden" | "transitioning";
export declare type Config = {
    fade?: boolean;
    bootsplashName?: string;
};
export declare type ResultStatus = boolean | "activity_finishing" | "already_visible" | "already_hidden" | "shift_next";
export declare function show(config?: Config): Promise<ResultStatus>;
export declare function hide(config?: Config): Promise<ResultStatus>;
export declare function getVisibilityStatus(): Promise<VisibilityStatus>;
declare const _default: {
    show: typeof show;
    hide: typeof hide;
    getVisibilityStatus: typeof getVisibilityStatus;
};
export default _default;
