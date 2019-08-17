package liang.lollipop.guidelinesview.util

/**
 * @author lollipop
 * @date 2019-08-17 02:19
 * 引导页面尺寸计算的参照物
 */
enum class Association {
    /**
     * 关联到屏幕，
     * 那么计算将以屏幕为作为参照物
     * 但是引导组件的展示位置并不会发生改变
     **/
    Screen,
    /**
     * 关联到容器，那么计算将会以容器作为参照物，
     * 这个容器是指组件View展示的ViewGroup
     * 组件将会自动寻找能够展示引导View的容器并且展示
     **/
    Group,
    /**
     * 关联到target View的父容器
     * 相关尺寸的计算都将以父容器为参照物
     * 此处关联仅仅是针对尺寸的计算
     */
    Parent,
    /**
     * 关联到自身
     * 相关尺寸的计算都将以自身为参照物
     */
    Self,
    /**
     * 参照到具体参数值
     * 等同于直接填写的尺寸
     */
    Value
}