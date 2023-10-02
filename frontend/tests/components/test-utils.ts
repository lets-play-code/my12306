import { afterEach } from "vitest";
import { render as rtlRender, RenderResult } from "@testing-library/vue";
import { Component } from "vue";

let unmount: () => void;

interface CustomRenderOptions {
  // 在这里可以添加其他可能的选项类型
  props?: Record<string, any>;
}

const render = (
  component: Component,
  options?: CustomRenderOptions
): RenderResult => {
  const renderResult = rtlRender(component, options);
  unmount = renderResult.unmount;
  return renderResult;
};

export * from "@testing-library/vue";
export { render };

// 组件测试render渲染完页面后，下个测试运行前需要清理渲染的内容，以免影响后续测试运行
//将 afterEach 钩子移到 test-utils.ts 中,减少重复
afterEach(() => {
  if (unmount) {
    unmount();
  }
});
